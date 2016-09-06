package kikuko72.app.logic.util;

import kikuko72.app.model.record.identifier.Class;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RecordValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * hostsファイルを読み込んで連想配列を生成するクラスです
 * Created by User on 2016/09/06.
 */
public class HostsReader {
    // 試験用なので短めにする
    public static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

    public static Map<RecordKey, RecordValue> parseHosts(InputStream stream) throws IOException {
        Map<RecordKey, RecordValue> recordStore = new HashMap<RecordKey, RecordValue>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while((line = reader.readLine()) != null) {
            String[] tokens = line.replaceAll("^\\s*", "").replaceAll("#.*$", "").split("\\s+");
            if(tokens.length < 2) {
                continue;
            }
            String domainName = completeSuffix(tokens[1]);
            RecordKey recordKey = new RecordKey(domainName, Type.A, Class.INTERNET);
            if (recordStore.get(recordKey) == null) {
                RecordValue recordValue = new RecordValue(DEFAULT_TTL,
                        (Inet4Address)InetAddress.getByName(tokens[0]));
                recordStore.put(recordKey, recordValue);
            }
            if(tokens.length > 2) {
                RecordName cName = new RecordName(domainName);
                for(String alias : Arrays.copyOfRange(tokens, 2, tokens.length)) {
                    String aliasDomainName = completeSuffix(alias);
                    recordStore.put(new RecordKey(aliasDomainName, Type.A, Class.INTERNET),
                                    new RecordValue(DEFAULT_TTL, cName));
                }
            }
        }
        return recordStore;
    }

    private static String completeSuffix(String domainName) {
        return domainName.endsWith(".") ? domainName : domainName + ".";
    }
}

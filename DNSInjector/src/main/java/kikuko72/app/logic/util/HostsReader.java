package kikuko72.app.logic.util;

import kikuko72.app.model.record.identifier.RecordClass;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RecordValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            String domainName = tokens[1].endsWith(".") ? tokens[1] : tokens[1] + ".";
            RecordKey recordKey = new RecordKey(domainName, RecordType.A_RECORD, RecordClass.INTERNET);
            if (recordStore.get(recordKey) == null) {
                RecordValue recordValue = new RecordValue(RecordType.A_RECORD.bytes(),
                        DEFAULT_TTL,
                        InetAddress.getByName(tokens[0]).getAddress());
                recordStore.put(recordKey, recordValue);
            }
            if(tokens.length > 2) {
                RecordName cName = new RecordName(tokens[1]);
                for(String alias : Arrays.copyOfRange(tokens, 2, tokens.length)) {
                    String aliasDomainName = alias.endsWith(".") ? alias : alias + ".";
                    recordStore.put(new RecordKey(aliasDomainName, RecordType.A_RECORD, RecordClass.INTERNET),
                                    new RecordValue(RecordType.CNAME_RECORD.bytes(), DEFAULT_TTL, cName.bytes()));
                }
            }
        }
        return recordStore;
    }
}

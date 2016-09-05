package kikuko72.app.model.record.value;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.name.RecordName;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * レコードタイプに応じてRDataのインスタンスを返します
 * Created by User on 2016/09/05.
 */
class RDataFactory {
    static RData scanRData(byte[] recordType, byte[] message, int from, int to) {
        try {
            if (RecordType.A_RECORD.isMatch(recordType)) {
                byte[] rData = Arrays.copyOfRange(message, from, to);
                return createAsTypeA(rData);
            } else if(RecordType.CNAME_RECORD.isMatch(recordType)) {
                return createAsTypeC(message, from);
            }
        } catch (IOException e) {
            e.printStackTrace();
            byte[] rData = Arrays.copyOfRange(message, from, to);
            System.out.println("error RData : " + Arrays.toString(BytesTranslator.toUnsignedArray(rData)));
        }
        byte[] rData = Arrays.copyOfRange(message, from, to);
        return new RawRData(rData);
    }

    private static RDataTypeA createAsTypeA(byte[] rData) throws IOException {
        return new RDataTypeA(InetAddress.getByAddress(rData));
    }

    private static RDataTypeC createAsTypeC(byte[] message, int startOffset) {
        RecordName recordName = RecordName.scanStart(message, startOffset);
        return new RDataTypeC(recordName);
    }
}

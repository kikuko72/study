package kikuko72.app.model.record.value;

import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.net.InetAddress;

/**
 * レコードタイプに応じてRDataのインスタンスを返します
 * Created by User on 2016/09/05.
 */
class RDataFactory {
    static RData createRDataObject(byte[] recordType, byte[] rData) {
        try {
            if (RecordType.A_RECORD.isMatch(recordType)) {
                return createAsTypeA(rData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RawRData(rData);
    }

    private static RDataTypeA createAsTypeA(byte[] rData) throws IOException {
        return new RDataTypeA(InetAddress.getByAddress(rData));
    }
}

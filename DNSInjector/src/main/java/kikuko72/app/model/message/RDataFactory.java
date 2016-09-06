package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.Type;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RData;
import kikuko72.app.model.record.value.RDataTypeA;
import kikuko72.app.model.record.value.RDataTypeC;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * レコードタイプに応じてRDataのインスタンスを返します
 * Created by User on 2016/09/05.
 */
 class RDataFactory {
    static RData scanRData(RecordType recordType, byte[] message, int from, int to) {
        try {
            if (Type.A == recordType) {
                byte[] rData = Arrays.copyOfRange(message, from, to);
                return createAsTypeA(rData);
            } else if(Type.C_NAME == recordType) {
                return createAsTypeC(message, from);
            }
        } catch (IOException e) {
            e.printStackTrace();
            byte[] rData = Arrays.copyOfRange(message, from, to);
            System.err.println("error RData : " + Arrays.toString(BytesTranslator.toUnsignedArray(rData)));
        }
        byte[] rData = Arrays.copyOfRange(message, from, to);
        return new RawRData(rData);
    }

    private static RDataTypeA createAsTypeA(byte[] rData) throws IOException {
        return new RDataTypeA(InetAddress.getByAddress(rData));
    }

    private static RDataTypeC createAsTypeC(byte[] message, int startOffset) {
        StringBuilder sb = DNSMessageScanner.scanLabel(message, startOffset).getElement();
        RecordName recordName = new RecordName(sb.toString());
        return new RDataTypeC(recordName);
    }
}

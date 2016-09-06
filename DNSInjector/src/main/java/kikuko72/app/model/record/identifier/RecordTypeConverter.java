package kikuko72.app.model.record.identifier;

import kikuko72.app.logic.util.BytesTranslator;

/**
 * Created by User on 2016/09/10.
 */
public class RecordTypeConverter {

    public static RecordType convert(byte[] rawType) {
        switch(BytesTranslator.twoBytesToInt(rawType)) {
            case RawValues.TYPE_A:
                return Type.A;
            case RawValues.TYPE_C_NAME:
                return Type.C_NAME;
            default:
                return new RawRecordType(rawType);
        }
    }
}

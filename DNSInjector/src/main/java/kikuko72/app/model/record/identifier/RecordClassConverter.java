package kikuko72.app.model.record.identifier;

import kikuko72.app.logic.util.BytesTranslator;

/**
 * Created by User on 2016/09/10.
 */
public class RecordClassConverter {
    public static RecordClass convert(byte[] rawClass) {
        switch(BytesTranslator.twoBytesToInt(rawClass)) {
            case RawValues.CLASS_INTERNET:
                return Class.INTERNET;
            default:
                return new RawRecordClass(rawClass);
        }
    }
}

package kikuko72.app.model.record.identifier;

import kikuko72.app.logic.util.BytesTranslator;

/**
 * Created by User on 2016/06/12.
 */
public enum Class implements RecordClass {
    INTERNET(BytesTranslator.intToTwoBytes(RawValues.CLASS_INTERNET));
    private final byte[] bytes;

    Class(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes() {
        return bytes;
    }
}

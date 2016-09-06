package kikuko72.app.model.record.identifier;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.Arrays;

/**
 * Created by User on 2016/06/12.
 */
public enum Type implements RecordType {
    A(BytesTranslator.intToTwoBytes(RawValues.TYPE_A)),
    C_NAME(BytesTranslator.intToTwoBytes(RawValues.TYPE_C_NAME));

    private final byte[] bytes;

    Type(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}

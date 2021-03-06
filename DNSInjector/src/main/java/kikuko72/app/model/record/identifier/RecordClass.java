package kikuko72.app.model.record.identifier;

/**
 * Created by User on 2016/06/12.
 */
public enum RecordClass {
    INTERNET(new byte[]{0x0, 0x1});
    private final byte[] bytes;

    RecordClass(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes() {
        return bytes;
    }
    public boolean isMatch(byte[] target) {
        return target.length == 2 && bytes[0] == target[0] && bytes[1] == target[1];
    }
}

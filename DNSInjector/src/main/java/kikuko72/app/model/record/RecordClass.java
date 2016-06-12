package kikuko72.app.model.record;

/**
 * Created by User on 2016/06/12.
 */
public enum RecordClass {
    INTERNET(new byte[]{0x0, 0x1});
    private byte[] bytes;

    RecordClass(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] bytes() {
        return bytes;
    }
    public boolean isMatch(byte[] target) {
        if (target.length != 2) {
            return false;
        }
        return bytes[0] == target[0] && bytes[1] == target[1];
    }
}

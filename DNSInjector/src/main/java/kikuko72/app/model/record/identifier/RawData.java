package kikuko72.app.model.record.identifier;

import java.util.Arrays;

/**
 * Created by User on 2016/09/10.
 */
abstract class RawData {
    private final byte[] value;
    private static final int THIS_LENGTH = 2;

    RawData(byte[] value) {
        this.value = value;
    }

    public byte[] bytes() {
        assert  value.length == THIS_LENGTH;
        return Arrays.copyOf(value, THIS_LENGTH);
    }

    @Override
    public String toString() {
        return "RawData{" + Arrays.toString(value) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawData rawData = (RawData) o;

        return Arrays.equals(value, rawData.value);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}

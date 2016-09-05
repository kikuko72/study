package kikuko72.app.model.record.value;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.Arrays;

/**
 * 未実装のタイプのRData用のクラスです
 * Created by User on 2016/09/05.
 */
class RawRData implements  RData {
    private final byte[] rData; // 可変長(IPv4レコードなら32bit)

    RawRData(byte[] rData) {
        this.rData = rData;
    }

    @Override
    public int length() {
        return rData.length;
    }

    @Override
    public byte[] rdLength() {
        return BytesTranslator.intToTwoBytes(rData.length);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(rData, rData.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawRData rawRData = (RawRData) o;

        return Arrays.equals(rData, rawRData.rData);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(rData);
    }
}

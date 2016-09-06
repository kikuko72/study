package kikuko72.app.model.record.value;

import java.net.InetAddress;

/**
 * タイプAのRDataクラスです
 * Created by User on 2016/09/05.
 */
class RDataTypeA implements RData {
    private static final int TYPE_A_LENGTH_VALUE = 4;
    private static final byte[] TYPE_A_LENGTH = new byte[] {0, TYPE_A_LENGTH_VALUE};
    private final InetAddress inetAddress;

    RDataTypeA(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    @Override
    public int length() {
        return TYPE_A_LENGTH_VALUE;
    }

    @Override
    public byte[] rdLength() {
        return TYPE_A_LENGTH;
    }

    @Override
    public byte[] bytes() {
        return inetAddress.getAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RDataTypeA that = (RDataTypeA) o;

        return inetAddress.equals(that.inetAddress);

    }

    @Override
    public int hashCode() {
        return inetAddress.hashCode();
    }
}

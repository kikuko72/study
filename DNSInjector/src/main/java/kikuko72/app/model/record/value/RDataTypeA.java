package kikuko72.app.model.record.value;

import java.net.InetAddress;

/**
 * タイプAのRDataクラスです
 * Created by User on 2016/09/05.
 */
public class RDataTypeA implements RData {
    private static final int TYPE_A_LENGTH_VALUE = 4;
    private static final byte[] TYPE_A_LENGTH = new byte[] {0, TYPE_A_LENGTH_VALUE};
    private final InetAddress inetAddress;

    public RDataTypeA(InetAddress inetAddress) {
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

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    @Override
    public String toString() {
        return "address=" + inetAddress;
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

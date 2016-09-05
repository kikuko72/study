package kikuko72.app.model.record.value;

import kikuko72.app.logic.util.BytesTranslator;

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
}

package kikuko72.app.model.record.value;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.util.Arrays;

/**
 * DNS問い合わせで返されるリソースレコードのTTL、RDataとその長さを値部としてまとめたものです。
 * Created by User on 2016/09/05.
 */
public class RecordValue {
    private final byte[] ttl; // 32bit
    private final RData rData;

    private RecordValue(byte[] ttl, RData rData) {
        this.ttl = ttl;
        this.rData = rData;
    }

    public RecordValue(RecordType recordType, byte[] ttl, byte[] rData) throws IOException {
        this.ttl = ttl;
        this.rData = RDataFactory.createRDataObject(recordType.bytes(), rData);
    }

    /**
     * バイト配列の指定の位置からレコード値1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param recordType レコード値のタイプ
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return RecordValueのインスタンス
     */
    public static RecordValue scanAs(byte[] recordType, byte[] message, int startOffset) {
        byte[] ttl      = Arrays.copyOfRange(message, startOffset    , startOffset + 4);
        byte[] rdLength = Arrays.copyOfRange(message, startOffset + 4, startOffset + 6);
        byte[] rData    = Arrays.copyOfRange(message, startOffset + 6, startOffset + 6 + BytesTranslator.twoBytesToInt(rdLength));
        return new RecordValue(ttl, RDataFactory.createRDataObject(recordType, rData));
    }

    public int length() {
        return 4 + 2 + rData.length();
    }

    public byte[] getTtl() { return Arrays.copyOf(ttl, ttl.length); }

    public byte[] getRdLength() { return rData.rdLength(); }

    public byte[] getBinaryRData() { return rData.bytes(); }

    public byte[] bytes() {
        byte[] ret = new byte[length()];
        System.arraycopy(              ttl, 0, ret, 0,              4);
        System.arraycopy( rData.rdLength(), 0, ret, 4,              2);
        System.arraycopy(    rData.bytes(), 0, ret, 6, rData.length());
        return ret;
    }
}

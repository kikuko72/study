package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.Arrays;

/**
 * DNS問い合わせで返されるリソースレコードのTTL、RDataとその長さを値部としてまとめたものです。
 * Created by User on 2016/09/05.
 */
public class RecordValue {
    private final byte[] ttl; // 32bit
    private final byte[] rdLength; // 16bit
    private final byte[] rData; // 可変長(IPv4レコードなら32bit)

    public RecordValue(byte[] ttl, byte[] rdLength, byte[] rData) {
        this.ttl = ttl;
        this.rdLength = rdLength;
        this.rData = rData;
    }

    /**
     * バイト配列の指定の位置からレコード値1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return RecordValueのインスタンス
     */
    public static RecordValue scanStart(byte[] message, int startOffset) {
        byte[] ttl      = Arrays.copyOfRange(message, startOffset    , startOffset + 4);
        byte[] rdLength = Arrays.copyOfRange(message, startOffset + 4, startOffset + 6);
        byte[] rData    = Arrays.copyOfRange(message, startOffset + 6, startOffset + 6 + BytesTranslator.twoBytesToInt(rdLength));
        return new RecordValue(ttl, rdLength, rData);
    }

    public int length() {
        return 4 + 2 + rData.length;
    }

    public byte[] getTtl() { return Arrays.copyOf(ttl, ttl.length); }

    public byte[] getRdLength() { return Arrays.copyOf(rdLength, rdLength.length); }

    public byte[] getRData() { return Arrays.copyOf(rData, rData.length); }

    public byte[] bytes() {
        byte[] ret = new byte[length()];
        System.arraycopy(              ttl, 0, ret, 0,            4);
        System.arraycopy(         rdLength, 0, ret, 4,            2);
        System.arraycopy(            rData, 0, ret, 6, rData.length);
        return ret;
    }
}

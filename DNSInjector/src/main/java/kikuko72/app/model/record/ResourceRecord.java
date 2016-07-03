package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.RecordKey;

import java.net.InetAddress;
import java.util.Arrays;

public class ResourceRecord {
	// 試験用なので短めにする
	private static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

	private final RecordKey recordKey;
	private final byte[] ttl; // 32bit
	private final byte[] rdLength; // 16bit
	private final byte[] rData; // 可変長(IPv4レコードなら32bit)

    public ResourceRecord(RecordKey recordKey, InetAddress rData) {
        this.recordKey = recordKey;
		ttl = DEFAULT_TTL;
		rdLength = BytesTranslator.intToTwoBytes(rData.getAddress().length);
		this.rData = rData.getAddress();
	}

	public ResourceRecord(RecordKey recordKey, byte[] ttl, byte[] rdLength, byte[] rData) {
        this.recordKey = recordKey;
        this.ttl = ttl;
        this.rdLength = rdLength;
        this.rData = rData;
    }

    public ResourceRecord createCompressedRecord(RecordKey compressedKey) {
        return new ResourceRecord(compressedKey, ttl, rdLength, rData);
    }

    /**
     * バイト配列の指定の位置からリソースレコード1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return ResourceRecordのインスタンス
     */
	public static ResourceRecord scanStart(byte[] message, int startOffset) {
		RecordKey recordKey = RecordKey.scanStart(message, startOffset);
		byte[] ttl      = Arrays.copyOfRange(message, startOffset + recordKey.length()    , startOffset + recordKey.length() + 4);
		byte[] rdLength = Arrays.copyOfRange(message, startOffset + recordKey.length() + 4, startOffset + recordKey.length() + 6);
		byte[] rData    = Arrays.copyOfRange(message, startOffset + recordKey.length() + 6, startOffset + recordKey.length() + 6 + BytesTranslator.twoBytesToInt(rdLength));
        return new ResourceRecord(recordKey, ttl, rdLength, rData);
	}

	public int length() {
		return recordKey.length() + 4 + 2 + rData.length;
	}

    public  RecordKey getRecordKey() { return  recordKey; }

	public byte[] getType() { return recordKey.getRecordType();	}

	public byte[] getRData() {
		return rData;
	}

	public byte[] bytes() {
		byte[] ret = new byte[length()];
		System.arraycopy(recordKey.bytes(), 0, ret,                      0, recordKey.length());
		System.arraycopy(              ttl, 0, ret, recordKey.length()    ,                  4);
		System.arraycopy(         rdLength, 0, ret, recordKey.length() + 4,                  2);
		System.arraycopy(            rData, 0, ret, recordKey.length() + 6,       rData.length);
		return ret;
	}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceRecord that = (ResourceRecord) o;

        if (!recordKey.equals(that.recordKey)) return false;
        if (!Arrays.equals(ttl, that.ttl)) return false;
        if (!Arrays.equals(rdLength, that.rdLength)) return false;
        return Arrays.equals(rData, that.rData);

    }

    @Override
    public int hashCode() {
        int result = recordKey.hashCode();
        result = 31 * result + Arrays.hashCode(ttl);
        result = 31 * result + Arrays.hashCode(rdLength);
        result = 31 * result + Arrays.hashCode(rData);
        return result;
    }

}

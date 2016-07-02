package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.name.RecordName;

import java.net.InetAddress;
import java.util.Arrays;

public class ResourceRecord {
	// 試験用なので短めにする
	private static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

	private final RecordKey recordKey;
	private final byte[] ttl; // 32bit
	private final byte[] rdLength; // 16bit
	private final byte[] rData; // 可変長(IPv4レコードなら32bit)

	public ResourceRecord(byte[]name, InetAddress rData) {
		// 今のところAレコードしか扱う気なし
		RecordName recordName = RecordName.scan(name);
		byte[] recordType = RecordType.A_RECORD.bytes() ;
		byte[] recordClass = RecordClass.INTERNET.bytes();
        recordKey = new RecordKey(recordName, recordType, recordClass);
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

    /**
     * バイト配列の先頭からリソースレコード1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されます。
     * @param input 入力となるバイト配列
     * @return ResourceRecordのインスタンス
     */
    public  static ResourceRecord scan(byte[] input) {
        return scan(input, 0);
    }

    /**
     * バイト配列の指定の位置からリソースレコード1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return ResourceRecordのインスタンス
     */
	public static ResourceRecord scan(byte[] input, int startOffset) {
		RecordKey recordKey = RecordKey.scan(input, startOffset);
		byte[] ttl      = Arrays.copyOfRange(input, startOffset + recordKey.length()    , startOffset + recordKey.length() + 4);
		byte[] rdLength = Arrays.copyOfRange(input, startOffset + recordKey.length() + 4, startOffset + recordKey.length() + 6);
		byte[] rData    = Arrays.copyOfRange(input, startOffset + recordKey.length() + 6, startOffset + recordKey.length() + 6 + BytesTranslator.twoBytesToInt(rdLength));
        return new ResourceRecord(recordKey, ttl, rdLength, rData);
	}

	public int length() {
		return recordKey.length() + 4 + 2 + rData.length;
	}

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

}

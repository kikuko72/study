package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.name.RecordName;

import java.net.InetAddress;
import java.util.Arrays;

public class ResourceRecord {
	// 試験用なので短めにする
	private static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

	private final RecordName name; // 可変長
	private final byte[] type; // 16bit
	private final byte[] dnsClass; // 16bit
	private final byte[] ttl; // 32bit
	private final byte[] rdLength; // 16bit
	private final byte[] rData; // 可変長(IPv4レコードなら32bit)

	public ResourceRecord(byte[]name, InetAddress rData) {
		// 今のところAレコードしか扱う気なし
		this.name = RecordName.scan(name);
		type = RecordType.A_RECORD.bytes() ;
		dnsClass = RecordClass.INTERNET.bytes();
		ttl = DEFAULT_TTL;
		rdLength = BytesTranslator.intToTwoBytes(rData.getAddress().length);
		this.rData = rData.getAddress();
	}

	public ResourceRecord(RecordName name, byte[] type, byte[] dnsClass, byte[] ttl, byte[] rdLength, byte[] rData) {
        this.name = name;
        this.type = type;
        this.dnsClass = dnsClass;
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
		RecordName name = RecordName.scan(input, startOffset);
		byte[] type     = Arrays.copyOfRange(input,      startOffset + name.length(), startOffset + name.length() +  2);
		byte[] dnsClass = Arrays.copyOfRange(input,  startOffset + name.length() + 2, startOffset + name.length() +  4);
		byte[] ttl      = Arrays.copyOfRange(input,  startOffset + name.length() + 4, startOffset + name.length() +  8);
		byte[] rdLength = Arrays.copyOfRange(input,  startOffset + name.length() + 8, startOffset + name.length() + 10);
		byte[] rData    = Arrays.copyOfRange(input, startOffset + name.length() + 10, startOffset + name.length() + 10 + BytesTranslator.twoBytesToInt(rdLength));
        return new ResourceRecord(name, type, dnsClass, ttl, rdLength, rData);
	}

	public int length() {
		return name.length() + 10 + rData.length;
	}

	public byte[] getType() {
		return type;
	}

	public byte[] getRData() {
		return rData;
	}

	public byte[] bytes() {
		byte[] ret = new byte[length()];
		System.arraycopy(name.bytes(), 0, ret,                  0,  name.length());
		System.arraycopy(        type, 0, ret,      name.length(),            2);
		System.arraycopy(    dnsClass, 0, ret,  name.length() + 2,            2);
		System.arraycopy(         ttl, 0, ret,  name.length() + 4,            4);
		System.arraycopy(    rdLength, 0, ret,  name.length() + 8,            2);
		System.arraycopy(       rData, 0, ret, name.length() + 10, rData.length);
		return ret;
	}

}

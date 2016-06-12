package kikuko72.app.model.message;

public class ResourceRecord {
	private static final byte[] A_RECORD = new byte[] {0, 1};
	private static final byte[] INTERNET = new byte[] {0, 1};
	// 試験用なので短めにする
	private static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};
	private static final byte[] IPV4_LENGTH = new byte[] {0, 4};

	private byte[] type; // 16bit
	private byte[] dnsClass; // 16bit
	private byte[] ttl; // 32bit
	private byte[] rdLength; // 16bit
	private byte[] rData; // 可変長(IPv4レコードなら32bit)

	ResourceRecord(byte[] rData) {
		// 今のところAレコードしか扱う気なし
		type = A_RECORD ;
		dnsClass = INTERNET;
		ttl = DEFAULT_TTL;
		rdLength = IPV4_LENGTH;
		this.rData = rData;
	}

	byte[] bytes() {
		byte[] ret = new byte[14];
		System.arraycopy(    type, 0, ret,  0, 2);
		System.arraycopy(dnsClass, 0, ret,  2, 2);
		System.arraycopy(     ttl, 0, ret,  4, 4);
		System.arraycopy(rdLength, 0, ret,  8, 2);
		System.arraycopy(   rData, 0, ret, 10, 4);
		return ret;
	}
}

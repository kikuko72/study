package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.name.RecordName;

import java.net.Inet4Address;
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
		this.name = RecordName.parse(name);
		type = RecordType.A_RECORD.bytes() ;
		dnsClass = RecordClass.INTERNET.bytes();
		ttl = DEFAULT_TTL;
		rdLength = intTo2Bytes(rData.getAddress().length);
		this.rData = rData.getAddress();
	}

	public ResourceRecord(byte[] input) {
		    name = RecordName.parse(input);
		    type = Arrays.copyOfRange(input,      name.length(),  name.length() + 2);
		dnsClass = Arrays.copyOfRange(input,  name.length() + 2,  name.length() + 4);
		     ttl = Arrays.copyOfRange(input,  name.length() + 4,  name.length() + 8);
		rdLength = Arrays.copyOfRange(input,  name.length() + 8, name.length() + 10);
		int unsignedRdLength = BytesTranslator.unSign(rdLength[0]) * 0xff
				+ BytesTranslator.unSign(rdLength[1]);
		   rData = Arrays.copyOfRange(input, name.length() + 10, name.length() + 10 + unsignedRdLength);
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

	private byte[] intTo2Bytes(int src) {
		return new byte[] {(byte)(src / 0x100), (byte)(src % 0x100)};
	}
}

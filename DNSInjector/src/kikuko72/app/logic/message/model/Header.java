package kikuko72.app.logic.message.model;

import java.util.Arrays;

class Header {
	static final int HEADER_LENGTH = 12;
	static final int FLAG_OFFSET = 2;
	private static final byte[] SYNGLE_ANSWER = new byte[] {0, 1};

	private final byte[] id; // 16bit
	private final Flag flag; // 16bit
	private final byte[] qdCount; // 16bit
	private final byte[] anCount; // 16bit
	private final byte[] nsCount; // 16bit
	private final byte[] arCount; // 16bit

	Header(byte[] headerBytes) {
		id      =          Arrays.copyOfRange(headerBytes,  0,  2);
		flag    = new Flag(Arrays.copyOfRange(headerBytes,  2,  4));
		qdCount =          Arrays.copyOfRange(headerBytes,  4,  6);
		anCount =          Arrays.copyOfRange(headerBytes,  6,  8);
		nsCount =          Arrays.copyOfRange(headerBytes,  8, 10);
		arCount =          Arrays.copyOfRange(headerBytes, 10, 12);
	}

	Header createAnswerHeader() {
		byte[] ret = this.bytes();
		System.arraycopy(flag.createAnswerFlag().bytes(), 0, ret, FLAG_OFFSET, Flag.LENGTH);
		System.arraycopy(                  SYNGLE_ANSWER, 0, ret,           6,           2);
		return new Header(ret);
	}

	byte[] bytes() {
		byte[] ret = new byte[12];
		System.arraycopy(          id, 0, ret,           0,           2);
		System.arraycopy(flag.bytes(), 0, ret, FLAG_OFFSET, Flag.LENGTH);
		System.arraycopy(     qdCount, 0, ret,           4,           2);
		System.arraycopy(     anCount, 0, ret,           6,           2);
		System.arraycopy(     nsCount, 0, ret,           8,           2);
		System.arraycopy(     arCount, 0, ret,          10,           2);
		return ret;
	}
}

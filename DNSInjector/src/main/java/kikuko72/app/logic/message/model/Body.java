package kikuko72.app.logic.message.model;

import java.util.Arrays;


public class Body {
	private static final byte[] DOMAIN_REPEAT_OFFSET = new byte[] {(byte)0xc0, 0x0c};
	private final byte[] value;
	private final Query query;

	Body(byte[] value) {
		this.value = value;
		this.query = new Query(value);
	}

	byte[] bytes() {
		return Arrays.copyOf(this.value, this.value.length);
	}

	String getDomainName() {
		return this.query.getDomainName();
	}

	Body createAnswerBody() {
		byte[] ret = new byte[value.length];
		ResourceRecord localhostRecord = new ResourceRecord(new byte[]{127, 0, 0, 1});
		int answerOffset = this.query.bytes().length;
		byte[] localhostAnswer = localhostRecord.bytes();
		System.arraycopy(               value, 0, ret,                 0,           answerOffset);
		System.arraycopy(DOMAIN_REPEAT_OFFSET, 0, ret,      answerOffset,                      2);
		System.arraycopy(     localhostAnswer, 0, ret,  answerOffset + 2, localhostAnswer.length);
		return new Body(ret);
	}

}

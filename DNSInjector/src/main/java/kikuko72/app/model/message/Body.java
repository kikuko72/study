package kikuko72.app.model.message;

import kikuko72.app.model.record.ResourceRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Body {
	private static final byte[] DOMAIN_REPEAT_OFFSET = new byte[] {(byte)0xc0, 0x0c};
	private final byte[] value;
	private final Query query;
	private final List<ResourceRecord> records;

	Body(byte[] input) {
		value = input;
		query = new Query(input);
		List<ResourceRecord> records = new ArrayList();
		for (int i = query.length(); i < input.length;) {
			ResourceRecord record = new ResourceRecord(Arrays.copyOfRange(input, i, input.length));
			records.add(record);
			i += record.length();
		}
		this.records = records;
	}

	byte[] bytes() {
		return Arrays.copyOf(this.value, this.value.length);
	}

	String getDomainName() {
		return this.query.getDomainName();
	}

	List<ResourceRecord> getResourcrRecords() {
		return records;
	}

	Body createAnswerBody() {
		ResourceRecord localhostRecord = new ResourceRecord(DOMAIN_REPEAT_OFFSET, new byte[]{127, 0, 0, 1});
		int answerOffset = this.query.length();
		byte[] localhostAnswer = localhostRecord.bytes();
		byte[] ret = new byte[value.length + localhostAnswer.length];
		System.arraycopy(               value, 0, ret,             0,           answerOffset);
		System.arraycopy(     localhostAnswer, 0, ret,  answerOffset, localhostAnswer.length);
		return new Body(ret);
	}

}

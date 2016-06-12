package kikuko72.app.model.message;

import kikuko72.app.model.record.ResourceRecord;

import java.util.Arrays;
import java.util.List;

public class DNSMessage {
	private Header header;
	private Body body;

	public DNSMessage(byte[] input) {
		header = new Header(Arrays.copyOf     (input, Header.HEADER_LENGTH));
		body =   new Body  (Arrays.copyOfRange(input, Header.HEADER_LENGTH, input.length));
	}

	public DNSMessage createAnswerMessage() {
		byte[] responseHeader = header.createAnswerHeader().bytes();
		byte[] responseBody = body.createAnswerBody().bytes();
		byte[] answerBytes = new byte[Header.HEADER_LENGTH + responseBody.length];
		System.arraycopy(responseHeader, 0, answerBytes,                     0, Header.HEADER_LENGTH);
		System.arraycopy(  responseBody, 0, answerBytes,  Header.HEADER_LENGTH,  responseBody.length);
		return new DNSMessage(answerBytes);
	}

	public String getDomainName() {
		return this.body.getDomainName();
	}

	public List<ResourceRecord> getAllResourceRecords() {
		return body.getResourcrRecords();
	}

	public byte[] bytes() {
		byte[] headerBytes = header.bytes();
		byte[] bodyBytes = body.bytes();
		byte[] ret = new byte[Header.HEADER_LENGTH + bodyBytes.length];
		System.arraycopy(headerBytes, 0, ret,                    0, Header.HEADER_LENGTH);
		System.arraycopy(  bodyBytes, 0, ret, Header.HEADER_LENGTH,     bodyBytes.length);
		return ret;
	}
}

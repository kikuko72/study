package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.ResourceRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DNSMessage {
	private static final byte[] DOMAIN_REPEAT_OFFSET = new byte[] {(byte)0xc0, 0x0c};
	private final Header header;
	private final Query query;
	private final List<ResourceRecord> records;

	public DNSMessage(byte[] input) {
        byte[] trimmedInput = BytesTranslator.trim(input);
		header = new Header(Arrays.copyOf     (trimmedInput, Header.HEADER_LENGTH));
		query =  Query.parse(trimmedInput, Header.HEADER_LENGTH);
		List<ResourceRecord> records = new ArrayList<ResourceRecord>();
		for (int i = Header.HEADER_LENGTH + query.length(); i < trimmedInput.length;) {
			ResourceRecord record = new ResourceRecord(Arrays.copyOfRange(trimmedInput, i, trimmedInput.length));
			records.add(record);
			i += record.length();
		}
		this.records = records;
	}

	public DNSMessage(Header header, Query query, List<ResourceRecord> records) {
		this.header = header;
		this.query = query;
		this.records = records;
	}

	public DNSMessage createAnswerMessage() {
		Header responseHeader = header.createAnswerHeader();
		ResourceRecord localhostRecord = new ResourceRecord(DOMAIN_REPEAT_OFFSET, new byte[]{127, 0, 0, 1});
		List<ResourceRecord> responseRecords = Arrays.asList(localhostRecord);
		return new DNSMessage(responseHeader, query, responseRecords);
	}

	public String getQueryDomainName() {
		return this.query.getDomainName();
	}

	public List<ResourceRecord> getAllResourceRecords() {
		return new ArrayList<ResourceRecord>(records);
	}

	public byte[] bytes() {
		byte[] headerBytes = header.bytes();
		byte[] queryBytes = query.bytes();
		byte[] recordsBytes = recordsToBytes(records);
		byte[] ret = new byte[Header.HEADER_LENGTH + queryBytes.length + recordsBytes.length];
		System.arraycopy( headerBytes, 0, ret,                                        0, Header.HEADER_LENGTH);
		System.arraycopy(  queryBytes, 0, ret, Header.HEADER_LENGTH                    ,    queryBytes.length);
		System.arraycopy(recordsBytes, 0, ret, Header.HEADER_LENGTH + queryBytes.length,  recordsBytes.length);
		return ret;
	}

	private byte[] recordsToBytes(List<ResourceRecord> records) {
		List<Byte> binary = new ArrayList<Byte>();
		for(ResourceRecord record :records) {
			for(byte b : record.bytes()) {
				binary.add(b);
			}
		}
		byte[] ret = new byte[binary.size()];
		for(int i = 0; i < ret.length; i++) {
			ret[i] = binary.get(i);
		}
		return ret;
	}
}

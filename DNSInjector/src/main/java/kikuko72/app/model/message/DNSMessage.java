package kikuko72.app.model.message;

import kikuko72.app.model.record.ResourceRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DNSメッセージを表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
public class DNSMessage {
	private final Header header;
	private final Query query;
	private final List<ResourceRecord> records;

	public DNSMessage(Header header, Query query, List<ResourceRecord> records) {
		this.header = header;
		this.query = query;
		this.records = records;
	}

    /**
     * バイト配列の先頭からDNSメッセージ1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されます。
     * @param input 入力となるバイト配列
     * @return DNSMessageのインスタンス
     */
    public static DNSMessage scan(byte[] input) {
        Header header = Header.scan(input);
        Query query   =  Query.scan(input, Header.DEFINITE_LENGTH);

        int cursor = Header.DEFINITE_LENGTH + query.length();
        List<ResourceRecord> records = new ArrayList<ResourceRecord>();
        for (int i = 0; i < header.getAnCount() + header.getNsCount() + header.getArCount(); i++) {
            ResourceRecord record = ResourceRecord.scan(input, cursor);
            records.add(record);
            cursor += record.length();
        }
        return new DNSMessage(header, query, records);
    }

	public DNSMessage createAnswerMessage(ResourceRecord... records) {
		return new DNSMessage(header.createAnswerHeader(records.length),
				              query,
                              Arrays.asList(records)
		);
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
		byte[] ret = new byte[Header.DEFINITE_LENGTH + queryBytes.length + recordsBytes.length];
		System.arraycopy( headerBytes, 0, ret,                                        0, Header.DEFINITE_LENGTH);
		System.arraycopy(  queryBytes, 0, ret, Header.DEFINITE_LENGTH,    queryBytes.length);
		System.arraycopy(recordsBytes, 0, ret, Header.DEFINITE_LENGTH + queryBytes.length,  recordsBytes.length);
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

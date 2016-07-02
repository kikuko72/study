package kikuko72.app.model.message;

import kikuko72.app.model.record.RecordType;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.name.RecordName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DNSメッセージを表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
public class DNSMessage {
	private final Header header;
	private final List<Query> queries;
	private final List<ResourceRecord> records;

	public DNSMessage(Header header, List<Query> queries, List<ResourceRecord> records) {
		this.header = header;
		this.queries = queries;
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

        int cursor = Header.DEFINITE_LENGTH;
        List<Query> queries = new ArrayList<Query>();
        for (int i = 0; i < header.getQdCount(); i++) {
            Query query   =  Query.scan(input, cursor);
            queries.add(query);
            cursor += query.length();
        }

        List<ResourceRecord> records = new ArrayList<ResourceRecord>();
        for (int i = 0; i < header.getAnCount() + header.getNsCount() + header.getArCount(); i++) {
            ResourceRecord record = ResourceRecord.scan(input, cursor);
            records.add(record);
            cursor += record.length();
        }
        return new DNSMessage(header, queries, records);
    }

	public DNSMessage createAnswerMessage(ResourceRecord... records) {
		return new DNSMessage(header.createAnswerHeader(records.length),
				              queries,
                              Arrays.asList(records)
		);
	}

	public List<RecordName> getQueryRecordNames() {
        List<RecordName> queryDomainNames = new ArrayList<RecordName>();
        for(Query query : queries) {
            queryDomainNames.add(query.getRecordName());
        }
		return queryDomainNames;
	}

	public List<ResourceRecord> getAllResourceRecords() {
		return new ArrayList<ResourceRecord>(records);
	}

	public byte[] bytes() {
		byte[] headerBytes = header.bytes();
		byte[] queryBytes = queriesToBytes(queries);
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

    private byte[] queriesToBytes(List<Query> queries) {
        List<Byte> binary = new ArrayList<Byte>();
        for(Query query :queries) {
            for(byte b : query.bytes()) {
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

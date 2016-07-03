package kikuko72.app.model.message;

import kikuko72.app.model.record.identifier.RecordKey;
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
	private final List<RecordKey> queries;
	private final List<ResourceRecord> records;

	public DNSMessage(Header header, List<RecordKey> queries, List<ResourceRecord> records) {
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
        List<RecordKey> queries = new ArrayList<RecordKey>();
        for (int i = 0; i < header.getQdCount(); i++) {
            RecordKey query   =  RecordKey.scan(input, cursor);
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

    Header getHeader() { return header; }

	public List<RecordKey> getQueries() {
        return new ArrayList<RecordKey>(queries);
	}

	public List<ResourceRecord> getAllResourceRecords() {
		return new ArrayList<ResourceRecord>(records);
	}

	public byte[] bytes() {
		return DNSMessageCompressor.compress(header, queries, records);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNSMessage that = (DNSMessage) o;

        if (!header.equals(that.header)) return false;
        if (!queries.equals(that.queries)) return false;
        return records.equals(that.records);

    }

    @Override
    public int hashCode() {
        int result = header.hashCode();
        result = 31 * result + queries.hashCode();
        result = 31 * result + records.hashCode();
        return result;
    }
}

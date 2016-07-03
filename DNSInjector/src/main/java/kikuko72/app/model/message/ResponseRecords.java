package kikuko72.app.model.message;

import kikuko72.app.model.record.ResourceRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * DNSメッセージの応答リソースレコード郡を表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 * Created by User on 2016/07/03.
 */
public class ResponseRecords {
    private final List<ResourceRecord> anRecords;
    private final List<ResourceRecord> nsRecords;
    private final List<ResourceRecord> arRecords;

    public ResponseRecords(List<ResourceRecord> anRecords, List<ResourceRecord> nsRecords, List<ResourceRecord> arRecords) {
        this.anRecords = new ArrayList<ResourceRecord>(anRecords);
        this.nsRecords = new ArrayList<ResourceRecord>(nsRecords);
        this.arRecords = new ArrayList<ResourceRecord>(arRecords);
    }

    /**
     * バイト配列の指定の位置からDNSヘッダで指定された個数のリソースレコードとして読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return ResponseRecordsのインスタンス
     */
    static ResponseRecords scanAsRecords(byte[] input, int startOffset, Header header) {
        int cursor = startOffset;
        List<ResourceRecord> anRecords = scanAsRecords(input, cursor, header.getAnCount());
        cursor += countLength(anRecords);
        List<ResourceRecord> nsRecords = scanAsRecords(input, cursor, header.getNsCount());
        cursor += countLength(nsRecords);
        List<ResourceRecord> arRecords = scanAsRecords(input, cursor, header.getNsCount());

        return new ResponseRecords(anRecords, nsRecords, arRecords);
    }

    private static List<ResourceRecord> scanAsRecords(byte[] input, int startOffset, int recordsCount) {
        int cursor = startOffset;
        List<ResourceRecord> records = new ArrayList<ResourceRecord>();
        for (int i = 0; i < recordsCount; i++) {
            ResourceRecord record = ResourceRecord.scanStart(input, cursor);
            records.add(record);
            cursor += record.length();
        }
        return records;
    }

    private static int countLength(List<ResourceRecord> records) {
        int length = 0;
        for(ResourceRecord record : records) {
            length += record.length();
        }
        return length;
    }

    int getAnCount() { return anRecords.size(); }
    int getNsCount() { return nsRecords.size(); }
    int getArCount() { return arRecords.size(); }

    List<ResourceRecord> getAllResourceRecords() {
        List<ResourceRecord> allRecords = new ArrayList<ResourceRecord>(anRecords);
        allRecords.addAll(nsRecords);
        allRecords.addAll(arRecords);
        return allRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseRecords that = (ResponseRecords) o;

        if (!anRecords.equals(that.anRecords)) return false;
        if (!nsRecords.equals(that.nsRecords)) return false;
        return arRecords.equals(that.arRecords);

    }

    @Override
    public int hashCode() {
        int result = anRecords.hashCode();
        result = 31 * result + nsRecords.hashCode();
        result = 31 * result + arRecords.hashCode();
        return result;
    }

}

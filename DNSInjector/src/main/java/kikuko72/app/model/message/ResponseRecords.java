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

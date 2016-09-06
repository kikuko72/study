package kikuko72.app.model.record.identifier.name;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * リソースレコードの名前部を表します。
 * このクラスは不変クラスとしてデザインされています。
 * Created by User on 2016/06/12.
 */
public class RecordName {
    private final List<LabelUnit> labels;
    private final String domainName;

    public RecordName(String domainName) {
        this.labels = toLabel(domainName);
        this.domainName = domainName;
    }

    public RecordName(List<LabelUnit> labels) {
        this.labels = new ArrayList<LabelUnit>(labels);
        domainName = joinDomainName(labels);
    }

    static List<LabelUnit> toLabel(String domainName) {
        List<LabelUnit> labels = new ArrayList<LabelUnit>();
        for(String part : domainName.split("\\.")) {
            if(part.length() == 0) { break; }
            LabelUnit label = new NameLabel((byte)part.length(), part.getBytes());
            labels.add(label);
        }
        labels.add(NameLabel.EMPTY_LABEL);
        return labels;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<LabelUnit> getLabels() { return new ArrayList<LabelUnit>(labels); }

    /**
     * このレコード名のバイト数を返します。
     * @return このレコード名のバイト数
     */
    public int length() {
        int ret = 0;
        for(LabelUnit label : labels) {
            ret += label.length();
        }
        return ret;
    }

    public byte[] bytes() {
        ByteBuffer buffer = ByteBuffer.allocate(length());
        for(LabelUnit label : labels) {
            buffer.put(label.bytes());
        }
        return buffer.array();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordName that = (RecordName) o;

        if (!labels.equals(that.labels)) return false;
        return domainName.equals(that.domainName);

    }

    @Override
    public int hashCode() {
        int result = labels.hashCode();
        result = 31 * result + domainName.hashCode();
        return result;
    }

    private String joinDomainName(List<LabelUnit> labels) {
        StringBuilder builder = new StringBuilder();
        for (LabelUnit label : labels) {
            builder.append(label).append(label.hasNextLabel() ? "." : "");
        }
        return builder.toString();
    }
}

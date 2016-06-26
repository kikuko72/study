package kikuko72.app.model.record.name;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * リソースレコードの名前部を表します。
 * Created by User on 2016/06/12.
 */
public class RecordName {
    private final List<LabelUnit> labels;
    private final String domainName;

    private RecordName(List<LabelUnit> labels) {
        this.labels = labels;
        domainName = joinDomainName(labels);
    }

    public static RecordName parse(byte[] input) {
        return parse(input, 0);
    }

    public static RecordName parse(byte[] input, int startOffset) {
        int pos = startOffset;
        List<LabelUnit> labels = new ArrayList<LabelUnit>();
        LabelUnit label;
        do {
            label = LabelUnit.parse(input, pos);
            labels.add(label);
            pos += label.length();
        } while (label.hasNextLabel());
        return new RecordName(labels);
    }

    public String getDomainName() {
        return domainName;
    }

    /**
     * この名前のバイト数を返します。
     * @return この名前のバイト数
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

    private String joinDomainName(List<LabelUnit> labels) {
        StringBuilder builder = new StringBuilder();
        for (LabelUnit label : labels) {
            builder.append(label).append(label.hasNextLabel() ? "." : "");
        }
        return builder.toString();
    }
}

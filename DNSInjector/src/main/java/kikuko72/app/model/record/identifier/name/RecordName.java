package kikuko72.app.model.record.identifier.name;

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

    public RecordName(List<LabelUnit> labels) {
        this.labels = labels;
        domainName = joinDomainName(labels);
    }

    /**
     * バイト配列の指定の位置からレコード名1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return RecordNameのインスタンス
     */
    public static RecordName scanStart(byte[] message, int startOffset) {
        return new RecordName(LabelScanner.scanStart(message, startOffset));
    }

    public String getDomainName() {
        return domainName;
    }

    public List<LabelUnit> getLabels() { return labels; }

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

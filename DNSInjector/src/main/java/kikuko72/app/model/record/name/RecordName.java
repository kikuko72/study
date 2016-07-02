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

    /**
     * バイト配列の先頭からレコード名1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されます。
     * @param input 入力となるバイト配列
     * @return RecordNameのインスタンス
     */
    public static RecordName scan(byte[] input) {
        return scan(input, 0);
    }

    /**
     * バイト配列の指定の位置からレコード名1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return RecordNameのインスタンス
     */
    public static RecordName scan(byte[] input, int startOffset) {
        int cursor = startOffset;
        List<LabelUnit> labels = new ArrayList<LabelUnit>();
        LabelUnit label;
        do {
            label = LabelUnit.scan(input, cursor);
            labels.add(label);
            cursor += label.length();
        } while (label.hasNextLabel());
        return new RecordName(labels);
    }

    public String getDomainName() {
        return domainName;
    }

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

    private String joinDomainName(List<LabelUnit> labels) {
        StringBuilder builder = new StringBuilder();
        for (LabelUnit label : labels) {
            builder.append(label).append(label.hasNextLabel() ? "." : "");
        }
        return builder.toString();
    }
}

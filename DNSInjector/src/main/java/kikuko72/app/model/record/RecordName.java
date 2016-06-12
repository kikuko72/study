package kikuko72.app.model.record;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 2016/06/12.
 */
public class RecordName {
    private final List<String> labels;
    private final String domainName;
    private final byte[] value;

    private RecordName(List<String> labels, byte[] value) {
        this.labels = labels;
        domainName = joinDomainName(labels);
        this.value = value;
    }

    public static RecordName parse(byte[] input) {
        if (BytesTranslator.unSign(input[0]) >= 0xc0) {
            int first = BytesTranslator.unSign(input[0]) - 0xc0;
            int offset = first * 0xff + BytesTranslator.unSign(input[1]);
            return new RecordName(Arrays.asList(new String[]{"compressed.", "offset: " + offset}),
                    new byte[]{input[0], input[1]});
        }

        int pos = 0;
        int labelLength;
        List<String> labels = new ArrayList<String>();
        do {
            labelLength = input[pos];
            if (labelLength > 0) {
                pos++; // ラベルの長さ1バイトの次からラベル開始
            }
            StringBuilder label = new StringBuilder();
            for (int i = pos; i < pos + labelLength; i++) {
                label.append(input[i]);
            }
            try {
                labels.add(label.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            pos += labelLength;
        } while (labelLength != 0);
        return new RecordName(labels, Arrays.copyOf(input, pos + 1));
    }

    public String getDomainName() {
        return domainName;
    }

    public int length() {
        return value.length;
    }

    public byte[] bytes() {
        return Arrays.copyOf(value, value.length);
    }

    private String joinDomainName(List<String> labels) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < labels.size(); i++) {
            builder.append(labels.get(i));
            if (i < labels.size() -1) {
                builder.append(".");
            }
        }
        return builder.toString();
    }
}

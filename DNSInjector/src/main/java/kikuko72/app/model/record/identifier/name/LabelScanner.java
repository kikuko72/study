package kikuko72.app.model.record.identifier.name;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * バイト配列の指定の位置をラベル列の開始位置として読み取るためのクラスです。
 * Created by User on 2016/07/03.
 */
class LabelScanner {
    private static final LabelUnit EMPTY_LABEL = new NameLabel(NameLabel.EMPTY_HEAD, new byte[]{});

    /**
     * バイト配列の指定の位置からラベル列として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return LabelUnitのリスト
     */
    static List<LabelUnit> scanStart(byte[] message, int startOffset) {
        int cursor = startOffset;
        List<LabelUnit> labels = new ArrayList<LabelUnit>();
        LabelUnit label;
        do {
            label = LabelScanner.scanALabel(message, cursor);
            labels.add(label);
            cursor += label.length();
        } while (label.hasNextLabel());
        return labels;
    }

    private static LabelUnit scanALabel(byte[] message, int startOffset) {
        byte headValue = message[startOffset];
        if (headValue == NameLabel.EMPTY_HEAD) {
            return EMPTY_LABEL;
        } else if (BytesTranslator.unSign(headValue) >= PointerLabel.MINIMUM_POINTER_HEAD) {
            int pointOffset = (BytesTranslator.unSign(headValue) - PointerLabel.MINIMUM_POINTER_HEAD) * 0x100
                            + BytesTranslator.unSign(message[startOffset + 1]);
            return new PointerLabel(message, pointOffset);
        }
        // tailはheadの次のバイトからheadに書かれた長さ分まで
        return new NameLabel(headValue, Arrays.copyOfRange(message, startOffset + 1, startOffset + 1 + headValue));
    }
}

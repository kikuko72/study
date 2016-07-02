package kikuko72.app.model.record.identifier.name;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.Arrays;

/**
 * リソースレコード中のドメイン名の一部を構成するラベルを表現するクラスです。
 * このクラスは不変クラスとしてデザインされています。
 * このクラスのインスタンスはDNSメッセージ中での圧縮されたラベルを表現することがありますが、
 * その場合は圧縮されている事と参照位置（DNSヘッダ先頭を0としたバイト数）を保持するのみで、
 * このクラスはDNSメッセージとしての整合性は保証しません。
 * 参照位置の整合性はこのクラスの利用者がとる必要があります。
 * Created by User on 2016/06/26.
 */
public class LabelUnit {
    private final byte head;
    private final byte[] tail;

    private static final byte[] EMPTY_TAIL = new byte[]{};
    private static final byte EMPTY_HEAD = 0x00;
    private static final int MINIMUM_POINTER_HEAD = 0xc0;

    private LabelUnit(byte head, byte[] tail) {
        this.head = head;
        this.tail = tail;
    }

    /**
     * バイト配列の指定の位置からラベル1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return LabelUnitのインスタンス
     */
    static LabelUnit scan(byte[] input, int startOffset) {
        byte headValue = input[startOffset];
        if (headValue == EMPTY_HEAD) {
            return new LabelUnit(headValue, EMPTY_TAIL);
        } else if (BytesTranslator.unSign(headValue) >= MINIMUM_POINTER_HEAD) {
            return new LabelUnit(headValue, new byte[]{input[startOffset + 1]});
        }
        // tailはheadの次のバイトからheadに書かれた長さ分まで
        return new LabelUnit(headValue, Arrays.copyOfRange(input, startOffset + 1, startOffset + 1 + headValue));
    }

    public static LabelUnit createPointer(int offset) {
        return scan(BytesTranslator.intToTwoBytes(MINIMUM_POINTER_HEAD * 0x100 + offset), 0);
    }

    public boolean isEmpty() {
        return head == EMPTY_HEAD;
    }

    public boolean isPointer() {
        return BytesTranslator.unSign(head) >= MINIMUM_POINTER_HEAD;
    }

    boolean hasNextLabel() {
        return !(isEmpty() || isPointer());
    }

    /**
     * このラベルのバイト数を返します。
     * @return このラベルのバイト数
     */
    int length() {
        return 1 + tail.length;
    }

    /**
     * このラベルが圧縮されたものである場合、このラベルの意味する参照位置を返します。
     * 圧縮されたものでない場合は-1を返します。
     * @return このラベルの参照位置（圧縮されたラベルでない場合は-1）
     */
    int getReferenceOffset() {
        if(!isPointer()) {
            return -1;
        }
        return (BytesTranslator.unSign(head) - MINIMUM_POINTER_HEAD) * 0x100 + BytesTranslator.unSign(tail[0]);
    }

    public byte[] bytes() {
        byte[] ret = new byte[tail.length + 1];
        ret[0] = head;
        System.arraycopy(tail, 0, ret, 1, tail.length);
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabelUnit labelUnit = (LabelUnit) o;

        if (head != labelUnit.head) return false;
        return Arrays.equals(tail, labelUnit.tail);

    }

    @Override
    public int hashCode() {
        int result = (int) head;
        result = 31 * result + Arrays.hashCode(tail);
        return result;
    }

    @Override
    public String toString(){
        if (isEmpty()) {
            return "";
        } else if (isPointer()) {
            return "ref. offset： " + getReferenceOffset();
        }
        StringBuilder builder = new StringBuilder(tail.length);
        for (byte aTail : tail) {
            builder.append((char) aTail);
        }
        return builder.toString();
    }
}

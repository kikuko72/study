package kikuko72.app.model.record.name;

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
class LabelUnit {
    private final byte head;
    private final byte[] tail;

    private static final byte[] EMPTY_TAIL = new byte[]{};
    private static final byte EMPTY_HEAD = 0x00;
    private static final int MINIMUM_POINTER_HEAD = 0xc0;

    private LabelUnit(byte head, byte[] tail) {
        this.head = head;
        this.tail = tail;
    }

    static LabelUnit parse(byte[] input, int startOffset) {
        byte head = input[startOffset];
        if (head == EMPTY_HEAD) {
            return new LabelUnit(head, EMPTY_TAIL);
        } else if (BytesTranslator.unSign(head) >= MINIMUM_POINTER_HEAD) {
            return new LabelUnit(head, new byte[]{input[startOffset + 1]});
        }
        return new LabelUnit(head, Arrays.copyOfRange(input, startOffset + 1, startOffset + 1 + head));
    }

    boolean isEmpty() {
        return head == EMPTY_HEAD;
    }

    boolean isPointer() {
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

    byte[] bytes() {
        byte[] ret = new byte[tail.length + 1];
        ret[0] = head;
        System.arraycopy(tail, 0, ret, 1, tail.length);
        return ret;
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

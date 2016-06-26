package kikuko72.app.model.record.name;

import kikuko72.app.logic.util.BytesTranslator;

import java.util.Arrays;

/**
 * リソースレコード中のドメイン名の一部を構成するラベルを表現するクラスです。
 * このクラスは不変クラスとしてデザインされています。
 * このクラスのインスタンスはDNSメッセージ中での圧縮されたラベルを表現することがありますが、
 * その場合は圧縮されている事と参照位置（DNSヘッダからのバイト数）を保持するのみで、
 * このクラスはDNSメッセージとしての整合性は保証しません。
 * 参照位置の整合性はこのクラスの利用者がとる必要があります。
 * Created by User on 2016/06/26.
 */
class LabelUnit {
    private final byte head;
    private final byte[] tail;

    private static final byte[] EMPTY_TAIL = new byte[]{};
    private static final byte EMPTY_HEAD = 0x00;
    private static final int MINIMUM_COMPRESSED_HEAD = 0xc0;

    private LabelUnit(byte head, byte[] tail) {
        this.head = head;
        this.tail = tail;
    }

    static LabelUnit parse(byte[] input, int startOffset) {
        byte head = input[startOffset];
        if (head == EMPTY_HEAD) {
            return new LabelUnit(head, EMPTY_TAIL);
        } else if (BytesTranslator.unSign(head) >= MINIMUM_COMPRESSED_HEAD) {
            return new LabelUnit(head, new byte[]{input[startOffset + 1]});
        }
        return new LabelUnit(head, Arrays.copyOfRange(input, startOffset + 1, startOffset + 1 + head));
    }

    boolean isEmpty() {
        return head == EMPTY_HEAD;
    }

    boolean isCompressed() {
        return BytesTranslator.unSign(head) >= MINIMUM_COMPRESSED_HEAD;
    }

    boolean hasNextLabel() {
        return !(isEmpty() || isCompressed());
    }

    /**
     * このラベルのバイト数を返します。
     * @return このラベルのバイト数
     */
    int length() {
        return 1 + tail.length;
    }

    /**
     * このラベルが圧縮されたものである場合、このラベルの意味する参照位置（DNSヘッダからのバイト数）を返します。
     * 圧縮されたものでない場合は-1を返します。
     * @return このラベルの参照位置（圧縮されたラベルでない場合は-1）
     */
    int getReferenceOffset() {
        if(!isCompressed()) {
            return -1;
        }
        return (head - MINIMUM_COMPRESSED_HEAD) * 0xff + BytesTranslator.unSign(tail[0]);
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
        } else if (isCompressed()) {
            return "参照：" + getReferenceOffset() + "バイト目";
        }
        StringBuilder builder = new StringBuilder(tail.length);
        for(int i = 0; i < tail.length; i++) {
            builder.append((char)tail[i]);
        }
        return builder.toString();
    }
}

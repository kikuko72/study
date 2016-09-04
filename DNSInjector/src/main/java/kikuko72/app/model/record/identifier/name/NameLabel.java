package kikuko72.app.model.record.identifier.name;

import java.util.Arrays;

/**
 * リソースレコード中のドメイン名の一部を構成するラベルを表現するクラスです。
 * このクラスは不変クラスとしてデザインされています。
 * Created by User on 2016/06/26.
 */
class NameLabel implements  LabelUnit {
    private final byte head;
    private final byte[] tail;

    static final byte EMPTY_HEAD = 0x00;

    NameLabel(byte head, byte[] tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public boolean isEmpty() { return head == EMPTY_HEAD; }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public boolean hasNextLabel() {
        return !isEmpty();
    }

    /**
     * このラベルのバイト数を返します。
     * @return このラベルのバイト数
     */
    @Override
    public int length() { return 1 + tail.length; }

    @Override
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

        NameLabel nameLabel = (NameLabel) o;

        if (head != nameLabel.head) return false;
        return Arrays.equals(tail, nameLabel.tail);

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
        }
        StringBuilder builder = new StringBuilder(tail.length);
        for (byte aTail : tail) {
            builder.append((char) aTail);
        }
        return builder.toString();
    }
}

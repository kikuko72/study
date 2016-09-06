package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.name.LabelUnit;

import java.util.List;

/**
 * リソースレコード中のドメイン名の一部を構成するラベルを表現するクラスです。
 * このクラスは不変クラスとしてデザインされています。
 * このクラスのインスタンスはDNSメッセージ中での圧縮されたラベルを表現します。
 * そのためこのクラスのインスタンスの生成にはこのラベル以前のDNSメッセージの情報を必要とします。
 * Created by User on 2016/07/03.
 */
class PointerLabel implements LabelUnit {

    static final int MINIMUM_POINTER_HEAD = 0xc0;

    private final int pointOffset;
    private final List<LabelUnit> labelSequence;

    PointerLabel(byte[] message, int pointOffset) {
        this.pointOffset = pointOffset;
        labelSequence = LabelConverter.scanStart(message, pointOffset);
    }

    @Override
    public boolean isEmpty() { return false; }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public boolean hasNextLabel() { return false; }

    /**
     * このラベルのバイト数を返します。
     * @return このラベルのバイト数
     */
    @Override
    public int length() { return 2; }

    @Override
    public byte[] bytes() {
        return BytesTranslator.intToTwoBytes(MINIMUM_POINTER_HEAD * 0x100 + pointOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointerLabel that = (PointerLabel) o;

        if (pointOffset != that.pointOffset) return false;
        return labelSequence.equals(that.labelSequence);

    }

    @Override
    public int hashCode() {
        int result = pointOffset;
        result = 31 * result + labelSequence.hashCode();
        return result;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (LabelUnit label : labelSequence) {
            builder.append(label).append(label.hasNextLabel() ? "." : "");
        }
        return builder.toString();
    }
}

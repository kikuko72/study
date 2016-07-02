package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;

import java.nio.ByteBuffer;

/**
 * DNSメッセージのヘッダ部を表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
class Header {
	static final int DEFINITE_LENGTH = 12;

	private final int id; // 16bit
	private final Flag flag; // 16bit
	private final int qdCount; // 16bit
	private final int anCount; // 16bit
	private final int nsCount; // 16bit
	private final int arCount; // 16bit

    Header(int id, Flag flag, int qdCount, int anCount, int nsCount, int arCount) {
        this.id = id;
        this.flag = flag;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
    }

    /**
     * バイト配列の先頭からDNSヘッダ1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されます。
     * @param input 入力となるバイト配列
     * @return Headerのインスタンス
     */
    static Header scan(byte[] input) {
        return scan(input, 0);
    }

    /**
     * バイト配列の指定の位置からDNSヘッダ1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return Headerのインスタンス
     */
    static Header scan(byte[] input, int startOffset) {
        int id      = BytesTranslator.twoBytesToInt(input,  startOffset     );
        Flag flag   =                     Flag.scan(input,  startOffset +  2);
        int qdCount = BytesTranslator.twoBytesToInt(input,  startOffset +  4);
        int anCount = BytesTranslator.twoBytesToInt(input,  startOffset +  6);
        int nsCount = BytesTranslator.twoBytesToInt(input,  startOffset +  8);
        int arCount = BytesTranslator.twoBytesToInt(input,  startOffset + 10);
        return new Header(id, flag, qdCount, anCount, nsCount, arCount);
    }

    int getQdCount() {
        return qdCount;
    }

    int getAnCount() {
        return anCount;
    }

    int getNsCount() {
        return nsCount;
    }

    int getArCount() {
        return arCount;
    }

    /**
     * queryに対する回答用のヘッダを作成します。
     * @param anCount 回答リソースレコードの数
     * @return responseHeader
     */
	Header createAnswerHeader(int anCount) {
        // nsCount, arCountに関しては使う予定がないため保留
		return new Header(this.id,
                          this.flag.createAnswerFlag(),
                          this.qdCount,
                          anCount,
                          this.nsCount,
                          this.arCount
        );
	}

    byte[] bytes() {
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.put(BytesTranslator.intToTwoBytes(id))
			  .put(flag.bytes())
              .put(BytesTranslator.intToTwoBytes(qdCount))
              .put(BytesTranslator.intToTwoBytes(anCount))
              .put(BytesTranslator.intToTwoBytes(nsCount))
              .put(BytesTranslator.intToTwoBytes(arCount));
		return buffer.array();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (id != header.id) return false;
        if (qdCount != header.qdCount) return false;
        if (anCount != header.anCount) return false;
        if (nsCount != header.nsCount) return false;
        if (arCount != header.arCount) return false;
        return flag.equals(header.flag);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + flag.hashCode();
        result = 31 * result + qdCount;
        result = 31 * result + anCount;
        result = 31 * result + nsCount;
        result = 31 * result + arCount;
        return result;
    }
}

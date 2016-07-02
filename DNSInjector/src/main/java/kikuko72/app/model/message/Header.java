package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;

import java.nio.ByteBuffer;
import java.util.Arrays;

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

	Header(byte[] headerBytes) {
		id      = BytesTranslator.twoBytesToInt(headerBytes,  0);
		flag    = new Flag(Arrays.copyOfRange(headerBytes,  2,  4));
		qdCount = BytesTranslator.twoBytesToInt(headerBytes,  4);
		anCount = BytesTranslator.twoBytesToInt(headerBytes,  6);
		nsCount = BytesTranslator.twoBytesToInt(headerBytes,  8);
		arCount = BytesTranslator.twoBytesToInt(headerBytes, 10);
	}

    Header(int id, Flag flag, int qdCount, int anCount, int nsCount, int arCount) {
        this.id = id;
        this.flag = flag;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
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
}

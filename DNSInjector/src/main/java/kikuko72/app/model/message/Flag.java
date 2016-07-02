package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;

/**
 * DNSメッセージヘッダのフラグ部を表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
 class Flag {
	private static final int RESPONSE_FLAG_QR = 0x80 * 0x100;
	private static final int RESPONSE_FLAG_AA = 0x4 * 0x100;
	private static final int RESPONSE_FLAG_TC = 0x2 * 0x100;
	private static final int RESPONSE_FLAG_RA = 0x80;

	static final int DEFINITE_LENGTH = 2; // 16bit
	private final int value;

	Flag(byte[] input) {
		if (input.length == Flag.DEFINITE_LENGTH) {
			value = BytesTranslator.twoBytesToInt(input);
		} else {
			throw new IllegalArgumentException("DNSメッセージのFlagは16bitでなければなりません");
		}
	}

	Flag(int value) {
        this.value = value;
    }

	Flag createAnswerFlag() {
		return new Flag(
                value | RESPONSE_FLAG_QR // 応答フラグON
                      | RESPONSE_FLAG_AA // オーソリティ応答フラグON
                      & (~RESPONSE_FLAG_TC) // 切捨てフラグOFF
                      | RESPONSE_FLAG_RA // 再帰有効
        );
	}

	byte[] bytes() {
		return BytesTranslator.intToTwoBytes(value);
	}

}

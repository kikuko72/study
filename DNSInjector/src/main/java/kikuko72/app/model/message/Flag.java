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
    private static final int RESPONSE_FLAG_R_CODE_SUCCESS = 0x0;
    private static final int RESPONSE_FLAG_R_CODE_FAIL = 0x3;

	private final int value;

    private Flag(int value) {
        this.value = value;
    }

    /**
     * バイト配列の指定の位置からフラグ1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
     * @param input 入力となるバイト配列
     * @param startOffset 読み取り開始位置
     * @return Flagのインスタンス
     */
    static Flag scan(byte[] input, int startOffset) {
        return new Flag(BytesTranslator.twoBytesToInt(input, startOffset));
    }

	Flag createAnswerFlag(boolean succeeded) {
		return new Flag(
                value | RESPONSE_FLAG_QR // 応答フラグON
                      | RESPONSE_FLAG_AA // オーソリティ応答フラグON
                      & (~RESPONSE_FLAG_TC) // 切捨てフラグOFF
                      | RESPONSE_FLAG_RA // 再帰有効
                      | (succeeded ? RESPONSE_FLAG_R_CODE_SUCCESS : RESPONSE_FLAG_R_CODE_FAIL) // 戻りコード
        );
	}

	byte[] bytes() {
		return BytesTranslator.intToTwoBytes(value);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flag flag = (Flag) o;

        return value == flag.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}

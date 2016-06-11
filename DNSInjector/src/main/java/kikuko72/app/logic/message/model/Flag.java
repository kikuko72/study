package kikuko72.app.logic.message.model;

import java.util.Arrays;

import kikuko72.app.logic.util.BytesTranslator;


public class Flag {
	private static final short REAPONCE_FLAG_QR = 0x80;
	private static final short REAPONCE_FLAG_AA = 0x4;
	private static final short REAPONCE_FLAG_TC = 0x2;
	private static final short REAPONCE_FLAG_RA = 0x80;

	static final int LENGTH = 2; // 16bit
	private byte[] value;

	Flag(byte[] value) {
		if (value.length == Flag.LENGTH) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("DNSメッセージのFlagは16bitでなければなりません");
		}
	}

	Flag createAnswerFlag() {
		byte[] ret = this.bytes();
		ret = setQR(ret);
		ret = setAA(ret);
		ret = setTC(ret);
		ret = setRA(ret);
		return new Flag(ret);
	}

	byte[] bytes() {
		return Arrays.copyOf(this.value, this.value.length);
	}

	private byte[] setQR(byte[] value) {
		byte[] ret = Arrays.copyOf(value, 2);
		// OR演算で1(応答フラグ)をセット
		ret[0] = (byte)(BytesTranslator.unSign(ret[0]) | REAPONCE_FLAG_QR);
		return ret;
	}

	private byte[] setAA(byte[] value) {
		byte[] ret = Arrays.copyOf(value, 2);
		// OR演算で1(このネームサーバからの応答)をセット
		ret[0] = (byte)(BytesTranslator.unSign(ret[0]) | REAPONCE_FLAG_AA);
		return ret;
	}

	private byte[] setTC(byte[] value) {
		byte[] ret = Arrays.copyOf(value, 2);
		// 該当bitのnotとのANDで0(切り捨てなし、UDP)をセット
		ret[0] = (byte)(BytesTranslator.unSign(ret[0]) & (~REAPONCE_FLAG_TC));
		return ret;
	}

	private byte[] setRA(byte[] value) {
		byte[] ret = Arrays.copyOf(value, 2);
		// OR演算で1(再帰可能)をセット
		ret[1] = (byte)(BytesTranslator.unSign(ret[1]) | REAPONCE_FLAG_RA);
		return ret;
	}

}

package kikuko72.app.logic.util;

import java.util.Arrays;

/**
 * javaのbyteは符号付でややこしいためintに直す
 */
public class BytesTranslator {
	private static final short SINGLE_BYTE_AND_MASK = 0xFF;

	public static int unSign(byte bits) {
		// bitsが負数の時用に下位8bitだけ抽出する
		return (bits & SINGLE_BYTE_AND_MASK);
	}

	public static byte[] trim(byte[] target) {
		int lastValueOffset = 0;
		for (int i = 0; i < target.length; i++) {
			if (target[i] != 0) {
				lastValueOffset = i;
			}
		}
		return Arrays.copyOf(target, lastValueOffset + 1);
	}

	public static int twoBytesToInt(byte[] src) {
		return twoBytesToInt(src, 0);
	}

	public static int twoBytesToInt(byte[] src, int from) {
		return BytesTranslator.unSign(src[from]) * 0x100 + BytesTranslator.unSign(src[from + 1]);
	}
	public static byte[] intToTwoBytes(int src) {
		return new byte[] {(byte)(src / 0x100), (byte)(src % 0x100)};
	}
}

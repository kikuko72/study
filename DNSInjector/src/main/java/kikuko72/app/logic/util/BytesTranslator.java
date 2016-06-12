package kikuko72.app.logic.util;

import java.util.Arrays;

/**
 * javaのbyteは符号付でややこしいためshortに直す
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
}

package kikuko72.app.logic.message.model;

import java.util.ArrayList;
import java.util.Arrays;

class Query {
	static final int Q_TYPE_LENGTH = 2; // 質問タイプ: 16bit
	static final int Q_CLASS_LENGTH = 2; // 質問クラス: 16bit

	private final String domainName;
	private final byte[] qName;
	private final byte[] qType;
	private final byte[] qClass;

	Query(byte[] input) {
		/*
		 * 質問セクションの形式
		 * 質問名(可変長): 0バイトで終端を示す
		 * 質問タイプ: 16bit
		 * 質問クラス: 16bit
		 */

		int pos = 0;
		short labelLength = 0;
		ArrayList<String> labels = new ArrayList<String>();
		do {
			labelLength = input[pos];
			pos++; // ラベルの長さ1バイトの次からラベル開始
			byte[] label = new byte[labelLength];
			for (int i = pos; i < pos + labelLength; i++) {
				label[i - pos] = input[i];
			}
			try {
				labels.add(new String(label, "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			pos += labelLength;
		} while (labelLength != 0);// 終端判定

		// ループ終了時点でラベルの終端の次の1バイトにいるはず(=ラベル終端までの長さ)
		int qNameLength = pos;
		 qName = Arrays.copyOf(input, qNameLength);
		 qType = Arrays.copyOfRange(input,                 qNameLength,                  qNameLength + Q_TYPE_LENGTH);
		qClass = Arrays.copyOfRange(input, Q_TYPE_LENGTH + qNameLength, Q_CLASS_LENGTH + qNameLength + Q_TYPE_LENGTH);

		labels.remove(labels.size() - 1);// 終端の0バイトが含まれている
		domainName = joinDomainName(labels);

	}

	String getDomainName() {
		return domainName;
	}

	byte[] bytes() {
		byte[] ret = new byte[qName.length + qType.length + qClass.length];
		System.arraycopy( qName, 0, ret,                           0,  qName.length);
		System.arraycopy( qType, 0, ret,                qName.length,  qType.length);
		System.arraycopy(qClass, 0, ret, qName.length + qType.length, qClass.length);
		return ret;
	}

	private String joinDomainName(ArrayList<String> labels) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < labels.size(); i++) {
			builder.append(labels.get(i));
			if (i < labels.size() -1) {
				builder.append(".");
			}
		}
		return builder.toString();
	}
}

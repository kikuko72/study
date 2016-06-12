package kikuko72.app.model.message;

import kikuko72.app.model.record.RecordName;

import java.util.Arrays;

class Query {
	static final int Q_TYPE_LENGTH = 2; // 質問タイプ: 16bit
	static final int Q_CLASS_LENGTH = 2; // 質問クラス: 16bit

	private final RecordName qName;
	private final byte[] qType;
	private final byte[] qClass;

	Query(byte[] input) {
		/*
		 * 質問セクションの形式
		 * 質問名(可変長): 0バイトで終端を示す
		 * 質問タイプ: 16bit
		 * 質問クラス: 16bit
		 */
		 qName = RecordName.parse(input);
		 qType = Arrays.copyOfRange(input,                 qName.length(),                  qName.length() + Q_TYPE_LENGTH);
		qClass = Arrays.copyOfRange(input, Q_TYPE_LENGTH + qName.length(), Q_CLASS_LENGTH + qName.length() + Q_TYPE_LENGTH);
	}

	String getDomainName() {
		return qName.getDomainName();
	}

	int length() {
		return qName.length() + qType.length + qClass.length;
	}

	byte[] bytes() {
		byte[] ret = new byte[qName.length() + qType.length + qClass.length];
		System.arraycopy( qName.bytes(), 0, ret,                             0,  qName.length());
		System.arraycopy(         qType, 0, ret,                qName.length(),    qType.length);
		System.arraycopy(        qClass, 0, ret, qName.length() + qType.length,   qClass.length);
		return ret;
	}
}

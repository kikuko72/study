package kikuko72.app.model.message;

import kikuko72.app.model.record.name.RecordName;

import java.util.Arrays;

class Query {
	private static final int Q_TYPE_LENGTH = 2; // 質問タイプ: 16bit
	private static final int Q_CLASS_LENGTH = 2; // 質問クラス: 16bit

	private final RecordName qName;
	private final byte[] qType;
	private final byte[] qClass;

	private Query(RecordName qName, byte[] qType, byte[] qClass) {
		this.qName = qName;
		this.qType = qType;
		this.qClass = qClass;
	}

	static Query parse(byte[] input, int startOffset) {
		/*
		 * 質問セクションの形式
		 * 質問名(可変長): 0バイトで終端を示す
		 * 質問タイプ: 16bit
		 * 質問クラス: 16bit
		 */
		RecordName qName = RecordName.parse(input, startOffset);
		byte[] qType = Arrays.copyOfRange(input,                  startOffset + qName.length(),                  startOffset + qName.length() + Q_TYPE_LENGTH);
		byte[] qClass = Arrays.copyOfRange(input, startOffset + qName.length() + Q_TYPE_LENGTH, startOffset + qName.length() + Q_TYPE_LENGTH + Q_CLASS_LENGTH);
		return new Query(qName, qType, qClass);
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

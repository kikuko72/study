package kikuko72.app.model.message;

import kikuko72.app.model.record.name.RecordName;

import java.util.Arrays;

/**
 * DNSメッセージの質問部を表すクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
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

	/**
	 * バイト配列の指定の位置からDNSメッセージの質問部1つ分として解釈できる範囲までを読み取り、
	 * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
	 * @param input 入力となるバイト配列
	 * @param startOffset 読み取り開始位置
	 * @return Queryのインスタンス
	 */
	static Query scan(byte[] input, int startOffset) {
		/*
		 * 質問セクションの形式
		 * 質問名(可変長): 0バイトで終端を示す
		 * 質問タイプ: 16bit
		 * 質問クラス: 16bit
		 */
		RecordName qName = RecordName.scan(input, startOffset);
		byte[] qType  = Arrays.copyOfRange(input, startOffset + qName.length()                , startOffset + qName.length() + Q_TYPE_LENGTH                 );
		byte[] qClass = Arrays.copyOfRange(input, startOffset + qName.length() + Q_TYPE_LENGTH, startOffset + qName.length() + Q_TYPE_LENGTH + Q_CLASS_LENGTH);
		return new Query(qName, qType, qClass);
	}

	RecordName getRecordName() {
		return qName;
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

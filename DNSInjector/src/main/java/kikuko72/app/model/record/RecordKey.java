package kikuko72.app.model.record;

import kikuko72.app.model.record.name.RecordName;

import java.util.Arrays;

/**
 * リソースレコードの名前、タイプ、クラスを1つにまとめたクラスです。
 * このクラスは不変クラスとしてデザインされています。
 */
public class RecordKey {
	private static final int RECORD_TYPE_LENGTH = 2; // 質問タイプ: 16bit
	private static final int RECORD_CLASS_LENGTH = 2; // 質問クラス: 16bit

	private final RecordName recordName;
	private final byte[] recordType;
	private final byte[] recordClass;

	public RecordKey(RecordName recordName, byte[] recordType, byte[] recordClass) {
		this.recordName = recordName;
		this.recordType = recordType;
		this.recordClass = recordClass;
	}

	/**
	 * バイト配列の指定の位置からRecordKey1つ分として解釈できる範囲までを読み取り、
	 * 新しいインスタンスを生成します。残りの情報や、読み取り開始位置より前の情報は無視されます。
	 * @param input 入力となるバイト配列
	 * @param startOffset 読み取り開始位置
	 * @return RecordKeyのインスタンス
	 */
	public static RecordKey scan(byte[] input, int startOffset) {
		RecordName recordName = RecordName.scan(input, startOffset);
		byte[] recordType  = Arrays.copyOfRange(input, startOffset + recordName.length()                     , startOffset + recordName.length() + RECORD_TYPE_LENGTH);
		byte[] recordClass = Arrays.copyOfRange(input, startOffset + recordName.length() + RECORD_TYPE_LENGTH, startOffset + recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH);
		return new RecordKey(recordName, recordType, recordClass);
	}

	public boolean isType(RecordType type) {return type.isMatch(this.recordType); }

	public byte[] getRecordType() { return recordType; }

	public String getDomainName() { return recordName.getDomainName(); }

	RecordName getRecordName() {
		return recordName;
	}

	public int length() {
		return recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH;
	}

	public byte[] bytes() {
		byte[] ret = new byte[recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH];
		System.arraycopy(recordName.bytes(), 0, ret,                                        0, recordName.length());
		System.arraycopy(        recordType, 0, ret, recordName.length()                     , RECORD_TYPE_LENGTH );
		System.arraycopy(       recordClass, 0, ret, recordName.length() + RECORD_TYPE_LENGTH, RECORD_CLASS_LENGTH);
		return ret;
	}
}

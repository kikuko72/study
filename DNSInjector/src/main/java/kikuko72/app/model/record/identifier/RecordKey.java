package kikuko72.app.model.record.identifier;

import kikuko72.app.model.record.identifier.name.LabelUnit;
import kikuko72.app.model.record.identifier.name.RecordName;

import java.util.Arrays;
import java.util.List;

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
	 * 新しいインスタンスを生成します。残りの情報は無視されますが、
	 * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
	 * @param message DNSメッセージ全体のバイト配列
	 * @param startOffset 読み取り開始位置
	 * @return RecordKeyのインスタンス
	 */
	public static RecordKey scanStart(byte[] message, int startOffset) {
		RecordName recordName = RecordName.scanStart(message, startOffset);
		byte[] recordType  = Arrays.copyOfRange(message, startOffset + recordName.length()                     , startOffset + recordName.length() + RECORD_TYPE_LENGTH);
		byte[] recordClass = Arrays.copyOfRange(message, startOffset + recordName.length() + RECORD_TYPE_LENGTH, startOffset + recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH);
		return new RecordKey(recordName, recordType, recordClass);
	}

    public RecordKey createCompressedKey(List<LabelUnit> compressedLabels) {
        return new RecordKey(new RecordName(compressedLabels), recordType, recordClass);
    }

	public boolean isType(RecordType type) {return type.isMatch(this.recordType); }

	public byte[] getRecordType() { return Arrays.copyOf(recordType, RECORD_TYPE_LENGTH); }

	public String getDomainName() { return recordName.getDomainName(); }

	public List<LabelUnit> getLabels() { return recordName.getLabels(); }

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

    @Override
    public String toString() {
        return  "domainName=" + recordName.getDomainName() +
                ", recordType=" + Arrays.toString(recordType) +
                ", recordClass=" + Arrays.toString(recordClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordKey recordKey = (RecordKey) o;

        if (!recordName.equals(recordKey.recordName)) return false;
        if (!Arrays.equals(recordType, recordKey.recordType)) return false;
        return Arrays.equals(recordClass, recordKey.recordClass);

    }

    @Override
    public int hashCode() {
        int result = recordName.hashCode();
        result = 31 * result + Arrays.hashCode(recordType);
        result = 31 * result + Arrays.hashCode(recordClass);
        return result;
    }
}

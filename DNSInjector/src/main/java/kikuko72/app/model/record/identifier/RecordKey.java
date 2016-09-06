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
	private final RecordType recordType;
	private final RecordClass recordClass;

    public RecordKey(RecordName recordName, RecordType recordType, RecordClass recordClass) {
        this.recordName = recordName;
        this.recordType = recordType;
        this.recordClass = recordClass;
    }

    public RecordKey(String domainName, RecordType recordType, RecordClass recordClass) {
        this.recordName = new RecordName(domainName);
        this.recordType = recordType;
        this.recordClass = recordClass;
    }

    public RecordKey createCompressedKey(List<LabelUnit> compressedLabels) {
        return new RecordKey(new RecordName(compressedLabels), recordType, recordClass);
    }

	public boolean isType(RecordType type) {return type.equals(this.recordType); }

	public RecordType getRecordType() { return recordType; }

    public RecordName getRecordName() { return recordName; }

	public String getDomainName() { return recordName.getDomainName(); }

	public List<LabelUnit> getLabels() { return recordName.getLabels(); }

	public int length() {
		return recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH;
	}

	public byte[] bytes() {
		byte[] ret = new byte[recordName.length() + RECORD_TYPE_LENGTH + RECORD_CLASS_LENGTH];
		System.arraycopy( recordName.bytes(), 0, ret,                                        0, recordName.length());
		System.arraycopy( recordType.bytes(), 0, ret, recordName.length()                     , RECORD_TYPE_LENGTH );
		System.arraycopy(recordClass.bytes(), 0, ret, recordName.length() + RECORD_TYPE_LENGTH, RECORD_CLASS_LENGTH);
		return ret;
	}

    @Override
    public String toString() {
        return  "domainName=" + recordName.getDomainName() +
                ", recordType=" + recordType +
                ", recordClass=" + recordClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordKey recordKey = (RecordKey) o;

        if (!recordName.equals(recordKey.recordName)) return false;
        if (!recordType.equals(recordKey.recordType)) return false;
        return recordClass.equals(recordKey.recordClass);

    }

    @Override
    public int hashCode() {
        int result = recordName.hashCode();
        result = 31 * result + recordType.hashCode();
        result = 31 * result + recordClass.hashCode();
        return result;
    }
}

package kikuko72.app.model.record;

import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.value.RData;
import kikuko72.app.model.record.value.RecordValue;

import java.util.Arrays;

public class ResourceRecord {

	private final RecordKey recordKey;
    private final RecordValue recordValue;

	public ResourceRecord(RecordKey recordKey, RecordValue recordValue) {
        assert recordKey.getRecordType().equals(recordValue.getRecordType());
        this.recordKey = recordKey;
        this.recordValue = recordValue;
    }

    public ResourceRecord createCompressedRecord(RecordKey compressedKey) {
        return new ResourceRecord(compressedKey, recordValue);
    }

	public int length() {
		return recordKey.length() + recordValue.length();
	}

    public RecordKey getRecordKey() { return  recordKey; }

    public RecordValue getRecordValue() { return recordValue; }

	public RecordType getType() { return recordKey.getRecordType();	}

    public byte[] getTtl() { return recordValue.getTtl(); }

    public byte[] getRdLength() { return recordValue.getRdLength(); }

	public RData getRData() { return recordValue.getRData(); }

	public byte[] bytes() {
		byte[] ret = new byte[length()];
		System.arraycopy(  recordKey.bytes(), 0, ret,                  0, recordKey.length()  );
        System.arraycopy(recordValue.bytes(), 0, ret, recordKey.length(), recordValue.length());
		return ret;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceRecord that = (ResourceRecord) o;

        if (!recordKey.equals(that.recordKey)) return false;
        return recordValue.equals(that.recordValue);

    }

    @Override
    public int hashCode() {
        int result = recordKey.hashCode();
        result = 31 * result + recordValue.hashCode();
        return result;
    }
}

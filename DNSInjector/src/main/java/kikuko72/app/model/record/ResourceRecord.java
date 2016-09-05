package kikuko72.app.model.record;

import kikuko72.app.model.record.identifier.RecordKey;

public class ResourceRecord {

	private final RecordKey recordKey;
    private final RecordValue recordValue;

	public ResourceRecord(RecordKey recordKey, RecordValue recordValue) {
        this.recordKey = recordKey;
        this.recordValue = recordValue;
    }

    public ResourceRecord createCompressedRecord(RecordKey compressedKey) {
        return new ResourceRecord(compressedKey, recordValue);
    }

    /**
     * バイト配列の指定の位置からリソースレコード1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されますが、
     * 開始位置より前の情報を参照することがあるため、入力にはDNSメッセージ全体を必要とします。
     * @param message DNSメッセージ全体のバイト配列
     * @param startOffset 読み取り開始位置
     * @return ResourceRecordのインスタンス
     */
	public static ResourceRecord scanStart(byte[] message, int startOffset) {
		RecordKey recordKey = RecordKey.scanStart(message, startOffset);
        RecordValue recordValue = RecordValue.scanStart(message, startOffset + recordKey.length());
        return new ResourceRecord(recordKey, recordValue);
	}

	public int length() {
		return recordKey.length() + recordValue.length();
	}

    public RecordKey getRecordKey() { return  recordKey; }

    public RecordValue getRecordValue() { return recordValue; }

	public byte[] getType() { return recordKey.getRecordType();	}

    public byte[] getTtl() { return recordValue.getTtl(); }

    public byte[] getRdLength() { return recordValue.getRdLength(); }

	public byte[] getRData() { return recordValue.getRData(); }

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

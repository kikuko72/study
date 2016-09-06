package kikuko72.app.model.record.value;

import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.Type;
import kikuko72.app.model.record.identifier.name.RecordName;

import java.net.Inet4Address;
import java.util.Arrays;

/**
 * DNS問い合わせで返されるリソースレコードのTTL、RDataとその長さを値部としてまとめたものです。
 * Created by User on 2016/09/05.
 */
public class RecordValue {
    private final RecordType recordType;
    private final byte[] ttl; // 32bit
    private final RData rData;

    public RecordValue(RecordType recordType, byte[] ttl, RData rData) {
        this.recordType = recordType;
        this.ttl = ttl;
        this.rData = rData;
    }

    public RecordValue(byte[] ttl, Inet4Address address) {
        this.recordType = Type.A;
        this.ttl = ttl;
        this.rData = new RDataTypeA(address);
    }

    public RecordValue(byte[] ttl, RecordName recordName) {
        this.recordType = Type.C_NAME;
        this.ttl = ttl;
        this.rData = new RDataTypeC(recordName);
    }

    public RecordName getCNameData() {
        if(!(rData instanceof  RDataTypeC)) {
            return null;
        }
        return ((RDataTypeC) rData).getRecordName();
    }

    public int length() {
        return 4 + 2 + rData.length();
    }

    public RecordType getRecordType() { return recordType; }

    public byte[] getTtl() { return Arrays.copyOf(ttl, ttl.length); }

    public byte[] getRdLength() { return rData.rdLength(); }

    public byte[] getBinaryRData() { return rData.bytes(); }

    public byte[] bytes() {
        byte[] ret = new byte[length()];
        System.arraycopy(              ttl, 0, ret, 0,              4);
        System.arraycopy( rData.rdLength(), 0, ret, 4,              2);
        System.arraycopy(    rData.bytes(), 0, ret, 6, rData.length());
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordValue that = (RecordValue) o;

        if (!recordType.equals(that.recordType)) return false;
        if (!Arrays.equals(ttl, that.ttl)) return false;
        return rData.equals(that.rData);

    }

    @Override
    public int hashCode() {
        int result = recordType.hashCode();
        result = 31 * result + Arrays.hashCode(ttl);
        result = 31 * result + rData.hashCode();
        return result;
    }
}

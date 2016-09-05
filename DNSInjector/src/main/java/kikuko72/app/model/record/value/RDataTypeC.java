package kikuko72.app.model.record.value;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.identifier.name.RecordName;

/**
 * タイプCのRDataクラスです
 * Created by User on 2016/09/05.
 */
class RDataTypeC implements RData {
    private final RecordName recordName;

    RDataTypeC(RecordName recordName) {
        this.recordName = recordName;
    }

    RecordName getRecordName() { return  recordName; }

    @Override
    public int length() {
        return recordName.length();
    }

    @Override
    public byte[] rdLength() {
        return BytesTranslator.intToTwoBytes(length());
    }

    @Override
    public byte[] bytes() {
        return recordName.bytes();
    }
}

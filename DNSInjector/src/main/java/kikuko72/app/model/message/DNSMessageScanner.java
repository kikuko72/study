package kikuko72.app.model.message;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.RecordTypeConverter;
import kikuko72.app.model.record.identifier.name.*;
import kikuko72.app.model.record.value.RData;
import kikuko72.app.model.record.value.RecordValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kikuko72.app.model.record.identifier.RecordClassConverter.convert;

/**
 * Created by User on 2016/09/11.
 */
class DNSMessageScanner {

    /**
     * バイト配列の先頭からDNSメッセージ1つ分として解釈できる範囲までを読み取り、
     * 新しいインスタンスを生成します。残りの情報は無視されます。
     * @param input 入力となるバイト配列
     * @return DNSMessageのインスタンス
     */
    public static DNSMessage scan(byte[] input) {
        Header header = scanHeader(input);

        int cursor = Header.DEFINITE_LENGTH;
        List<RecordKey> queries = new ArrayList<RecordKey>();
        for (int i = 0; i < header.getQdCount(); i++) {
            ScanResult<RecordKey> scanResult =  scanRecordKey(input, cursor);
            queries.add(scanResult.getElement());
            cursor = scanResult.getCursor();
        }

        ScanResult<ResponseRecords> recordsScanResult = scanAsRecords(input, cursor, header);

        return new DNSMessage(header, queries, recordsScanResult.getElement());
    }

    private static Header scanHeader(byte[] input) {
        int id      = BytesTranslator.twoBytesToInt(input      );
        Flag flag   =                     Flag.scan(input, +  2);
        int qdCount = BytesTranslator.twoBytesToInt(input, +  4);
        int anCount = BytesTranslator.twoBytesToInt(input, +  6);
        int nsCount = BytesTranslator.twoBytesToInt(input, +  8);
        int arCount = BytesTranslator.twoBytesToInt(input, + 10);
        return new Header(id, flag, qdCount, anCount, nsCount, arCount);
    }

    private static ScanResult<ResponseRecords> scanAsRecords(byte[] input, int cursor, Header header) {
        ScanResult<List<ResourceRecord>> anResult = scanAsRecords(input, cursor, header.getAnCount());
        cursor = anResult.getCursor();
        ScanResult<List<ResourceRecord>> nsResult = scanAsRecords(input, cursor, header.getNsCount());
        cursor = nsResult.getCursor();
        ScanResult<List<ResourceRecord>> arResult = scanAsRecords(input, cursor, header.getArCount());
        cursor = arResult.getCursor();

        return new ScanResult<ResponseRecords>(new ResponseRecords(anResult.getElement(), nsResult.getElement(), arResult.getElement()), cursor);
    }
    private static ScanResult<List<ResourceRecord>> scanAsRecords(byte[] input, int cursor, int recordsCount) {
        List<ResourceRecord> records = new ArrayList<ResourceRecord>();
        for (int i = 0; i < recordsCount; i++) {
            ScanResult<ResourceRecord> scanResult = scanARecord(input, cursor);
            records.add(scanResult.getElement());
            cursor = scanResult.getCursor();
        }
        return new ScanResult<List<ResourceRecord>>(records, cursor);
    }

    private static ScanResult<ResourceRecord> scanARecord(byte[] message, int cursor) {
        ScanResult<RecordKey> keyScanResult = scanRecordKey(message, cursor);
        RecordKey recordKey = keyScanResult.getElement();
        cursor = keyScanResult.getCursor();
        ScanResult<RecordValue> valueScanResult = scanRecordValue(recordKey.getRecordType(), message, cursor);
        cursor = valueScanResult.getCursor();
        return new ScanResult<ResourceRecord>(new ResourceRecord(recordKey, valueScanResult.getElement()), cursor);
    }

    private static ScanResult<RecordKey> scanRecordKey(byte[] message, int cursor) {
        final int RECORD_TYPE_LENGTH = 2; // 質問タイプ: 16bit
        final int RECORD_CLASS_LENGTH = 2; // 質問クラス: 16bit
        ScanResult<RecordName> nameScanResult = scanRecordName(message, cursor);
        RecordName recordName = nameScanResult.getElement();
        cursor = nameScanResult.getCursor();
        byte[] recordType  = Arrays.copyOfRange(message, cursor, cursor + RECORD_TYPE_LENGTH);
        cursor += RECORD_TYPE_LENGTH;
        byte[] recordClass = Arrays.copyOfRange(message, cursor, cursor + RECORD_CLASS_LENGTH);
        cursor += RECORD_CLASS_LENGTH;
        RecordKey result = new RecordKey(recordName, RecordTypeConverter.convert(recordType), convert(recordClass));
        return new ScanResult<RecordKey>(result, cursor);
    }

    private static ScanResult<RecordName> scanRecordName(byte[] message, int cursor) {
        ScanResult<StringBuilder> scanResult = scanLabel(message, cursor);
        return new ScanResult<RecordName>(new RecordName(scanResult.getElement().toString()), scanResult.getCursor());
    }

    static ScanResult<StringBuilder> scanLabel(byte[] message, int cursor) {
        byte headValue = message[cursor];
        StringBuilder sb = new StringBuilder();
        while(headValue != 0) {
            if (BytesTranslator.unSign(headValue) >= PointerLabel.MINIMUM_POINTER_HEAD) {
                int pointOffset = (BytesTranslator.unSign(headValue) - PointerLabel.MINIMUM_POINTER_HEAD) * 0x100
                        + BytesTranslator.unSign(message[cursor + 1]);
                sb.append(scanLabel(message, pointOffset).getElement());
                return new ScanResult<StringBuilder>(sb, cursor + 2);
            }
            cursor++;
            for(int i = 0; i < headValue; i++) {
                sb.append((char)message[cursor]);
                cursor++;
            }
            headValue = message[cursor];
            sb.append(".");
        }
        cursor++;
        return new ScanResult<StringBuilder>(sb, cursor);
    }


    private static ScanResult<RecordValue> scanRecordValue(RecordType recordType, byte[] message, int cursor) {
        byte[] ttl      = Arrays.copyOfRange(message, cursor, cursor += 4);
        byte[] rdLength = Arrays.copyOfRange(message, cursor, cursor += 2);
        RData rData     = RDataFactory.scanRData(recordType, message, cursor, cursor + BytesTranslator.twoBytesToInt(rdLength));
        return new ScanResult<RecordValue>(new RecordValue(recordType, ttl, rData), cursor + BytesTranslator.twoBytesToInt(rdLength));
    }
}

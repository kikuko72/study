package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.message.ResponseRecords;
import kikuko72.app.model.record.identifier.RecordClass;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


class Injector implements  Resolver{

    private Map<RecordKey, RecordValue> recordStore;

    Injector(Map<RecordKey, RecordValue> recordStore) {
        this.recordStore = recordStore;
    }

	public DNSMessage resolve(DNSMessage request) throws IOException {
        // ひとまず複数の質問のあるメッセージへの対応は保留
        RecordKey query = request.getQueries().get(0);

        // まだAレコードにしか回答できない
        assert query.isType(RecordType.A_RECORD);
        RecordValue answerData = recordStore.get(query);
        if (answerData != null) {
            RecordKey answerKey = new RecordKey(query.getRecordName(), answerData.getRecordType(), RecordClass.INTERNET.bytes());
            List<ResourceRecord> answerRecords = new ArrayList<ResourceRecord>();
            answerRecords.add(new ResourceRecord(answerKey, answerData));

            if(RecordType.CNAME_RECORD.isMatch(answerData.getRecordType())) {
                RecordKey cNameKey = new RecordKey(answerData.getCNameData(), query.getRecordType(), RecordClass.INTERNET.bytes());
                answerRecords.add(new ResourceRecord(cNameKey, recordStore.get(cNameKey)));
            }

            ResponseRecords records = new ResponseRecords(answerRecords, Collections.<ResourceRecord>emptyList(), Collections.<ResourceRecord>emptyList());
            return request.createAnswerMessage(records);
        }
        return null;
	}

    void cache(List<ResourceRecord> records) {
        if (records.size() == 0) {
            return;
        }
        for(ResourceRecord record : records) {
            recordStore.put(record.getRecordKey(), record.getRecordValue());
        }
    }
}

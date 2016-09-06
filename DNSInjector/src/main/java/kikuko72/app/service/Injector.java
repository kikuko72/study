package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.message.ResponseRecords;
import kikuko72.app.model.record.identifier.Class;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;

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

        // AレコードとCレコードに回答可能
        RecordValue answerData = recordStore.get(query);
        if (answerData != null) {
            // 応答リソースレコードのタイプがanswerDataと一致するようRecordKeyを再作成
            RecordKey answerKey = new RecordKey(query.getRecordName(), answerData.getRecordType(), Class.INTERNET);
            List<ResourceRecord> answerRecords = new ArrayList<ResourceRecord>();
            answerRecords.add(new ResourceRecord(answerKey, answerData));

            if(Type.C_NAME == answerData.getRecordType()) {
                // CNameをキーにしてあらためて要求レコードタイプでレコードを取得する
                RecordKey cNameKey = new RecordKey(answerData.getCNameData(), query.getRecordType(), Class.INTERNET);
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

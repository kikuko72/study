package kikuko72.app.service;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.message.ResponseRecords;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;


public class Injector implements  Resolver{

	public DNSMessage resolve(DNSMessage request) throws IOException {
        RecordKey query = request.getQueries().get(0);

        // まだAレコードにしか回答できない
        assert query.isType(RecordType.A_RECORD);
        ResourceRecord localhostRecord = new ResourceRecord(query, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        ResponseRecords records = new ResponseRecords(Collections.singletonList(localhostRecord), Collections.<ResourceRecord>emptyList(), Collections.<ResourceRecord>emptyList());
		return request.createAnswerMessage(records);
	}
}

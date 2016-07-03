package kikuko72.app.service;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.net.InetAddress;


public class Injector implements  Resolver{

	public DNSMessage resolve(DNSMessage request) throws IOException {
        RecordKey query = request.getQueries().get(0);

        // まだAレコードにしか回答できない
        assert query.isType(RecordType.A_RECORD);
        ResourceRecord localhostRecord = new ResourceRecord(query, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
		DNSMessage answerMessage = request.createAnswerMessage(localhostRecord);
		byte [] preAns = answerMessage.bytes();
		byte[] answer = BytesTranslator.trim(preAns);
		for (byte b : answer) {
			System.out.print(Integer.toHexString(b & 0xFF) + ", ");
		}
		System.out.println();
		return answerMessage;
	}
}

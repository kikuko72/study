package kikuko72.app.service;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.ResourceRecord;

import java.io.IOException;
import java.net.InetAddress;


public class Injector implements  Resolver{

	public DNSMessage resolve(DNSMessage request) throws IOException {
        ResourceRecord localhostRecord = new ResourceRecord(request.getQueries().get(0).bytes(), InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
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

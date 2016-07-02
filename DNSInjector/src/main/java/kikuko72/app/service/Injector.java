package kikuko72.app.service;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.ResourceRecord;

import java.io.IOException;
import java.net.InetAddress;


public class Injector implements  Resolver{
	private static final byte[] DOMAIN_REPEAT_OFFSET = new byte[] {(byte)0xc0, 0x0c};

	public DNSMessage resolve(DNSMessage request) throws IOException {
        ResourceRecord localhostRecord = new ResourceRecord(DOMAIN_REPEAT_OFFSET, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
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

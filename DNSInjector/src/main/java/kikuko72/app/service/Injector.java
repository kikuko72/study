package kikuko72.app.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.logic.util.BytesTranslator;


public class Injector implements  Resolver{
	public DNSMessage resolve(DNSMessage request) throws IOException {
		DNSMessage ansMes = request.createAnswerMessage();
		byte [] preAns = ansMes.bytes();
		byte[] answer = BytesTranslator.trim(preAns);
		for (byte b : answer) {
			System.out.print(Integer.toHexString(b & 0xFF) + ", ");
		}
		System.out.println();
		return ansMes;
	}
}

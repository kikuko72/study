package kikuko72.app.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.logic.util.BytesTranslator;


public class Injector {
	public static DatagramPacket resolve(DatagramPacket request) throws IOException {
		DNSMessage requestMessage = new DNSMessage(request.getData());
		DNSMessage ansMes = requestMessage.createAnswerMessage();
		byte [] preAns = ansMes.bytes();
		byte[] answer = BytesTranslator.trim(preAns);
		SocketAddress dist = request.getSocketAddress();
		for (byte b : answer) {
			System.out.print(Integer.toHexString(b & 0xFF) + ", ");
		}
		System.out.println();
		return new DatagramPacket(answer, answer.length, dist);
	}
}

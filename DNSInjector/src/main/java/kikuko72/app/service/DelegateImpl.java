package kikuko72.app.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;


class DelegateImpl implements Delegate {
	private InetAddress nextDns;

	public DelegateImpl(byte[] ipAddress) throws UnknownHostException {
		nextDns = InetAddress.getByAddress(ipAddress);
	}

	public DNSMessage resolve(DNSMessage request) throws IOException {
		DatagramSocket querySocket = new DatagramSocket();
		querySocket.send(DNS.createQueryPacket(request, nextDns));
		DatagramPacket answer = DNS.createReceivePacket();
		querySocket.receive(answer);
		querySocket.close();
		return DNSMessage.scan(BytesTranslator.trim(answer.getData()));
	}
}

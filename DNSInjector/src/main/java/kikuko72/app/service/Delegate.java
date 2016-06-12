package kikuko72.app.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.main.DNSInjector;
import kikuko72.app.model.message.DNSMessage;


public class Delegate implements  Resolver {
	private InetAddress nextDns;

	public Delegate(byte[] ipAddress) throws UnknownHostException {
		nextDns = InetAddress.getByAddress(ipAddress);
	}

	public DNSMessage resolve(DNSMessage request) throws IOException {
		byte[] query = request.bytes();

		DatagramSocket querySocket = new DatagramSocket();
		querySocket.send(new DatagramPacket(query,query.length, nextDns, DNSInjector.DNS_PORT_NUMBER));
		DatagramPacket packet = new DatagramPacket(new byte[512], 512);
		querySocket.receive(packet);
		querySocket.close();
		byte[] answer = BytesTranslator.trim(packet.getData());
		return new DNSMessage(packet.getData());
	}
}

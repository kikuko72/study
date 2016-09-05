package kikuko72.app.main;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.service.DNS;
import kikuko72.app.service.Delegate;
import kikuko72.app.service.Resolver;
import kikuko72.app.service.ResolverImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


class DNSInjector {
    private static final String DELEGATE_HOST_KEY = "delegate";

    private static Resolver resolver;

	public static void main(String[] args) throws IOException {
        // 委譲するDNSサーバーを指定
        String delegateHost = System.getProperty(DELEGATE_HOST_KEY);
        InetAddress delegateHostAddress = InetAddress.getByName(delegateHost);
        Delegate delegate = new Delegate(delegateHostAddress.getAddress());
        resolver = new ResolverImpl(delegate);

		while (true) {
            serve();
		}
	}

    public static void serve() throws IOException {
        DatagramSocket serviceSocket = new DatagramSocket(DNS.DNS_PORT_NUMBER);

        DatagramPacket request = DNS.createReceivePacket();
        serviceSocket.receive(request);
        DNSMessage message = DNSMessage.scan(request.getData());
        DNSMessage response = resolver.resolve(message);
        byte[] answer = BytesTranslator.trim(response.bytes());
        DatagramPacket responsePacket = new DatagramPacket(answer, answer.length, request.getSocketAddress());
        serviceSocket.send(responsePacket);
        serviceSocket.close();
    }
}

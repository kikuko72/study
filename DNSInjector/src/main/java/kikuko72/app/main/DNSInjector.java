package kikuko72.app.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import kikuko72.app.logic.message.model.DNSMessage;
import kikuko72.app.service.Delegate;
import kikuko72.app.service.Resolver;


public class DNSInjector {
	public static final int DNS_PORT_NUMBER = 53;
	public static final int DNS_UDP_MAX_BYTES = 512;
    public static final String DELEGATE_HOST_KEY = "delegate";

	public static void main(String[] args) throws IOException {
		while (true) {
			DatagramSocket serviceSocket = new DatagramSocket(DNS_PORT_NUMBER);

			DatagramPacket request = new DatagramPacket(new byte[DNS_UDP_MAX_BYTES], DNS_UDP_MAX_BYTES);
			serviceSocket.receive(request);
			DNSMessage message = new DNSMessage(request.getData());
			String dn = message.getDomainName();
			DatagramPacket responce;
			if ("hoge".equals(dn)) {
				responce = Resolver.resolve(request);
			} else {
                // 委譲するDNSサーバーを指定
                String delegateHost = System.getProperty(DELEGATE_HOST_KEY);
                InetAddress delegateHostAddress = InetAddress.getByName(delegateHost);
				Delegate delegate = new Delegate(delegateHostAddress.getAddress());
				responce = delegate.resolve(request);
			}
			serviceSocket.send(responce);
			serviceSocket.close();
		}
	}
}

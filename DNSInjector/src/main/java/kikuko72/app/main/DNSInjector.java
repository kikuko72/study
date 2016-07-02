package kikuko72.app.main;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.message.Query;
import kikuko72.app.model.record.RecordType;
import kikuko72.app.service.DNS;
import kikuko72.app.service.Delegate;
import kikuko72.app.service.Injector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;


class DNSInjector {
    private static final String DELEGATE_HOST_KEY = "delegate";

	public static void main(String[] args) throws IOException {
		while (true) {
            serve();
		}
	}

    public static void serve() throws IOException {
        DatagramSocket serviceSocket = new DatagramSocket(DNS.DNS_PORT_NUMBER);

        DatagramPacket request = DNS.createReceivePacket();
        serviceSocket.receive(request);
        DNSMessage message = DNSMessage.scan(request.getData());
        List<Query> queries = message.getQueries();
        DNSMessage response;
        if (canResolve(queries.get(0))) { // ひとまず複数の質問のあるメッセージへの対応は保留
            Injector injector = new Injector();
            response = injector.resolve(message);
        } else {
            // 委譲するDNSサーバーを指定
            String delegateHost = System.getProperty(DELEGATE_HOST_KEY);
            InetAddress delegateHostAddress = InetAddress.getByName(delegateHost);
            Delegate delegate = new Delegate(delegateHostAddress.getAddress());
            response = delegate.resolve(message);
        }
        byte[] answer = BytesTranslator.trim(response.bytes());
        DatagramPacket responsePacket = new DatagramPacket(answer, answer.length, request.getSocketAddress());
        serviceSocket.send(responsePacket);
        serviceSocket.close();
    }

    private static boolean canResolve(Query query) {
        return "hoge.".equals(query.getDomainName()) && query.isType(RecordType.A_RECORD);
    }
}

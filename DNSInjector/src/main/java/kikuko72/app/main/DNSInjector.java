package kikuko72.app.main;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.logic.util.HostsReader;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.service.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DNSInjector {
    private static final String DELEGATE_HOST_KEY = "delegate";
    private static final String HOSTS_PATH_KEY = "hosts";

    // 試験用なので短めにする
    public static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

    private static Resolver resolver;

	public static void main(String[] args) throws IOException {
        // 委譲するDNSサーバーを指定
        String delegateHost = System.getProperty(DELEGATE_HOST_KEY);
        InetAddress delegateHostAddress = InetAddress.getByName(delegateHost);
        Delegate delegate = new DelegateImpl(delegateHostAddress.getAddress());
        Map<RecordKey, RecordValue> recordStore;
        String hostsFilePath = System.getProperty(HOSTS_PATH_KEY);
        try {
            if (hostsFilePath != null) {
                File hosts = new File(hostsFilePath);
                recordStore = HostsReader.parseHosts(hosts.toURI().toURL().openStream());
            } else {
                recordStore = new HashMap<RecordKey, RecordValue>();
            }

        } catch (IOException e) {
            e.printStackTrace();
            recordStore = new HashMap<RecordKey, RecordValue>();
        }
        resolver = new ResolverImpl(delegate, recordStore);

		while (true) {
            serve();
		}
	}

    public static void serve() throws IOException {
        DatagramSocket serviceSocket = new DatagramSocket(DNS.DNS_PORT_NUMBER);

        DatagramPacket request = DNS.createReceivePacket();
        serviceSocket.receive(request);
        DNSMessage query = DNSMessage.scan(request.getData());
        DNSMessage response = resolver.resolve(query);
        byte[] answer = response.bytes();
        SocketAddress clientAddress = request.getSocketAddress();
        DatagramPacket responsePacket = new DatagramPacket(answer, answer.length, clientAddress);
        serviceSocket.send(responsePacket);
        serviceSocket.close();

        //log
        System.out.println("client : " + clientAddress);
        queryLog(query.getQueries());
        answerLog(response.getAllResourceRecords());
    }

    private static void queryLog(List<RecordKey> queries) {
        System.out.println("queries : ");
        for(RecordKey query : queries) {
            System.out.println(" " + query);
        }
    }

    private static void answerLog(List<ResourceRecord> answers) {
        System.out.println("answers : ");
        for(ResourceRecord answer : answers) {
            int ttl = BytesTranslator.twoBytesToInt(answer.getTtl()) * 0x100 * 0x100
                    + BytesTranslator.twoBytesToInt(answer.getTtl(), 2);
            System.out.println(" RData: " + Arrays.toString(BytesTranslator.toUnsignedArray(answer.getRData())) + ", recordType=" + Arrays.toString(answer.getType()) + ", TTL: " + ttl);
        }
        System.out.println();
    }
}

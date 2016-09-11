package kikuko72.app.main;

import kikuko72.app.logic.util.BytesTranslator;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.service.DNS;
import kikuko72.app.service.Resolver;
import kikuko72.app.service.ResolverImpl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DNSInjector {
    private static final String DELEGATE_HOST_KEY = "delegate";
    private static final String HOSTS_PATH_KEY = "hosts";

    // 試験用なので短めにする
    public static final byte[] DEFAULT_TTL = new byte[] {0, 0, 0, 60};

    private static DatagramSocket serviceSocket;
    private static Resolver resolver;
    private  static ExecutorService pool = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
        serviceSocket = new DatagramSocket(DNS.DNS_PORT_NUMBER);
        String delegateHost = System.getProperty(DELEGATE_HOST_KEY);
        String hostsFilePath = System.getProperty(HOSTS_PATH_KEY);
        resolver = new ResolverImpl(delegateHost, hostsFilePath);


		while (true) {
            serve();
		}
	}

    public static void serve() throws IOException {

        DatagramPacket request = DNS.createReceivePacket();
        serviceSocket.receive(request);
        pool.execute(new Service(request));
    }

    private static class Service implements Runnable {
        private final DatagramPacket request;

        public Service(DatagramPacket request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                DNSMessage query = DNSMessage.scan(request.getData());
                DNSMessage response = resolver.resolve(query);
                byte[] answer = response.bytes();
                SocketAddress clientAddress = request.getSocketAddress();
                DatagramPacket responsePacket = new DatagramPacket(answer, answer.length, clientAddress);
                serviceSocket.send(responsePacket);

                synchronized (System.out) {
                    //log
                    System.out.println("client : " + clientAddress);
                    queryLog(query.getQueries());
                    answerLog(response.getAllResourceRecords());
                }
            } catch (IOException e) {
                synchronized (System.err) {
                    e.printStackTrace();
                }
            }
        }

        private void queryLog(List<RecordKey> queries) {
            System.out.println("queries : ");
            for(RecordKey query : queries) {
                System.out.println(" " + query);
            }
        }

        private void answerLog(List<ResourceRecord> answers) {
            System.out.println("answers : ");
            for(ResourceRecord answer : answers) {
                int ttl = BytesTranslator.twoBytesToInt(answer.getTtl()) * 0x100 * 0x100
                        + BytesTranslator.twoBytesToInt(answer.getTtl(), 2);
                System.out.println(" RData: " + answer.getRData() + ", recordType=" + answer.getType() + ", TTL: " + ttl);
            }
            System.out.println();
        }
    }
}

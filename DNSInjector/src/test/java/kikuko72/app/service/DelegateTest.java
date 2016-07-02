package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.RecordType;
import kikuko72.app.model.record.ResourceRecord;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

/**
 * Created by User on 2016/06/12.
 */
public class DelegateTest {
    public static final String DELEGATE_HOST_KEY = "delegate";

    @Test
    public void resolve() throws IOException {
        byte[] input = new byte[]{
                0x0, 0x1, // ID
                0x1, 0x0, // Flags: Standard query
                0x0, 0x1, // Questions: 1
                0x0, 0x0, // Answer RRs: 0
                0x0, 0x0, // Authority RRs: 0
                0x0, 0x0, // Additional RRs: 0
                0x3, 0x77, 0x77, 0x77, // Name: www
                0x3, 0x06e, 0x69, 0x63, // Name: nic
                0x2, 0x61, 0x64, // Name: ad
                0x2, 0x6a, 0x70, // Name: jp
                0x0,
                0x0, 0x1, // Type: A
                0x0, 0x1 // Class: IN
        };
        DNSMessage queryMessage = DNSMessage.scan(input);

        InetAddress delegateHostAddress = InetAddress.getByName(System.getProperty(DELEGATE_HOST_KEY));
        Resolver delegate = new Delegate(delegateHostAddress.getAddress());
        DNSMessage answer = delegate.resolve(queryMessage);

        InetAddress expected;
        try {
            expected = InetAddress.getByName("www.nic.ad.jp");
        } catch(UnknownHostException e) {
            assertEquals(0, answer.getAllResourceRecords().size());
            System.out.println("WARN: UnknownHostException");
            return;
        }

        ResourceRecord result;
        for(ResourceRecord record : answer.getAllResourceRecords()) {
            if (RecordType.A_RECORD.isMatch(record.getType())) {
                result = record;
                InetAddress actual = InetAddress.getByAddress(result.getRData());
                assertEquals(expected, actual);
                return;
            }
            fail("Resolver couldn't resolve.");
        }
    }

}
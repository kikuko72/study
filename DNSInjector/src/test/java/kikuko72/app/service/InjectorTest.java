package kikuko72.app.service;

import kikuko72.app.main.DNSInjector;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.model.record.identifier.RecordClass;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by User on 2016/06/12.
 */
public class InjectorTest {

    @Test
    public void resolve() throws IOException {
        byte[] input = new byte[]{
                (byte)0xff, (byte)0xff, // ID
                0x1, 0x0, // Flags: Standard query
                0x0, 0x1, // Questions: 1
                0x0, 0x0, // Answer RRs: 0
                0x0, 0x0, // Authority RRs: 0
                0x0, 0x0, // Additional RRs: 0
                0x4, 0x68, 0x6f, 0x67, 0x65, 0x0, // Name: hoge
                0x0, 0x1, // Type: A
                0x0, 0x1 // Class: IN
        };
        DNSMessage queryMessage = DNSMessage.scan(input);

        Map<RecordKey, RecordValue> recordStore = new HashMap<RecordKey, RecordValue>();
        RecordKey hogeIpv4 = new RecordKey("hoge.", RecordType.A_RECORD, RecordClass.INTERNET);
        RecordValue localhostData = new RecordValue(RecordType.A_RECORD, DNSInjector.DEFAULT_TTL, new byte[]{127, 0, 0, 1});
        recordStore.put(hogeIpv4, localhostData);
        Resolver injector = new Injector(recordStore);
        DNSMessage actual = injector.resolve(queryMessage);
        byte[] expectedBytes = new byte[]{
                (byte)0xff, (byte)0xff, // ID
                (byte)0x85, (byte)0x80, // Flags: Standard query response, No error
                0x0, 0x1, // Questions: 1
                0x0, 0x1, // Answer RRs: 1
                0x0, 0x0, // Authority RRs: 0
                0x0, 0x0, // Additional RRs: 0
                0x4, 0x68, 0x6f, 0x67, 0x65, 0x0, // Name: hoge
                0x0, 0x1, // Type: A
                0x0, 0x1, // Class: IN
                (byte)0xc0, (byte)0xc, // Name: hoge
                0x0, 0x1, // Type: A
                0x0, 0x1, // Class: IN
                0x0, 0x0, 0x0, 0x3c, // Time to live: 60
                0x0, 0x4, // Data length: 4
                0x7f, 0x0, 0x0, 0x1 // 127.0.0.1
        };

        assertArrayEquals(expectedBytes, actual.bytes());

    }

}
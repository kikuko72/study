package kikuko72.app.service;

import kikuko72.app.main.DNSInjector;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.model.record.identifier.Class;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
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
        RecordKey hogeIpv4 = new RecordKey("hoge.", Type.A, Class.INTERNET);
        Inet4Address localhost = (Inet4Address)InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
        RecordValue localhostData = new RecordValue(DNSInjector.DEFAULT_TTL, localhost);
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
    @Test
    public void resolveCName() throws IOException {
        byte[] input = new byte[]{
                (byte)0xff, (byte)0xff, // ID
                0x1, 0x0, // Flags: Standard query
                0x0, 0x1, // Questions: 1
                0x0, 0x0, // Answer RRs: 0
                0x0, 0x0, // Authority RRs: 0
                0x0, 0x0, // Additional RRs: 0
                0x3, 0x62, 0x61, 0x72, 0x2, 0x6a, 0x70, 0x0, // Name: bar.jp
                0x0, 0x1, // Type: A
                0x0, 0x1 // Class: IN
        };
        DNSMessage queryMessage = DNSMessage.scan(input);

        Map<RecordKey, RecordValue> recordStore = new HashMap<RecordKey, RecordValue>();
        RecordKey fooIpv4 = new RecordKey("foo.jp.", Type.A, Class.INTERNET);
        Inet4Address localhost = (Inet4Address)InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
        RecordValue localhostData = new RecordValue(DNSInjector.DEFAULT_TTL, localhost);
        recordStore.put(fooIpv4, localhostData);

        RecordKey barKey = new RecordKey("bar.jp.", Type.A, Class.INTERNET);
        RecordValue fooValue = new RecordValue(DNSInjector.DEFAULT_TTL, new RecordName("foo.jp."));
        recordStore.put(barKey, fooValue);

        Resolver injector = new Injector(recordStore);
        DNSMessage actual = injector.resolve(queryMessage);
        byte[] expectedBytes = new byte[]{
                (byte)0xff, (byte)0xff, // ID
                (byte)0x85, (byte)0x80, // Flags: Standard query response, No error
                0x0, 0x1, // Questions: 1
                0x0, 0x2, // Answer RRs: 2
                0x0, 0x0, // Authority RRs: 0
                0x0, 0x0, // Additional RRs: 0
                0x3, 0x62, 0x61, 0x72, 0x2, 0x6a, 0x70, 0x0, // Name: bar.jp
                0x0, 0x1, // Type: A
                0x0, 0x1, // Class: IN
                (byte)0xc0, 0xc, // Name: bar.jp
                0x0, 0x5, // Type: C
                0x0, 0x1, // Class: IN
                0x0, 0x0, 0x0, 0x3c, // Time to live: 60
                0x0, 0x6, // Data length: 6
                0x3, 0x66, 0x6f, 0x6f, (byte)0xc0, 0x10,  // Name: foo.jp
                (byte)0xc0, 0x24, // Name: foo.jp
                0x0, 0x1, // Type: A
                0x0, 0x1, // Class: IN
                0x0, 0x0, 0x0, 0x3c, // Time to live: 60
                0x0, 0x4, // Data length: 4
                0x7f, 0x0, 0x0, 0x1 // 127.0.0.1
        };
        assertArrayEquals(expectedBytes, actual.bytes());
    }

}
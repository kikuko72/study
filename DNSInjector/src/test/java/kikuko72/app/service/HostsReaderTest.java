package kikuko72.app.service;

import kikuko72.app.model.record.identifier.Class;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.service.HostsReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by User on 2016/09/06.
 */
public class HostsReaderTest {

    private static final byte[] LOCALHOST_ADDRESS = new byte[]{127, 0, 0, 1};
    @Test
    public void parseHosts() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("kikuko72/app/logic/util/hosts");
        Map<RecordKey, RecordValue> recordStore = HostsReader.parseHosts(stream);

        Inet4Address localhost = (Inet4Address)InetAddress.getByAddress(LOCALHOST_ADDRESS);
        RecordKey fooKey = new RecordKey("foo.", Type.A, Class.INTERNET);
        RecordValue fooValue = new RecordValue(HostsReader.DEFAULT_TTL, localhost);
        Assert.assertEquals(fooValue, recordStore.get(fooKey));

        RecordKey fizzKey = new RecordKey("fizz.", Type.A, Class.INTERNET);
        RecordValue fizzValue = new RecordValue(HostsReader.DEFAULT_TTL, localhost);
        Assert.assertEquals(fizzValue, recordStore.get(fizzKey));

        RecordKey buzzKey = new RecordKey("buzz.", Type.A, Class.INTERNET);
        RecordValue buzzValue = new RecordValue(HostsReader.DEFAULT_TTL, new RecordName("fizz."));
        Assert.assertEquals(buzzValue, recordStore.get(buzzKey));
    }

}
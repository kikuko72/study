package kikuko72.app.logic.util;

import kikuko72.app.model.record.identifier.RecordClass;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;
import kikuko72.app.model.record.identifier.name.RecordName;
import kikuko72.app.model.record.value.RecordValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by User on 2016/09/06.
 */
public class HostsReaderTest {

    private static final byte[] LOCALHOST_ADDRESS = new byte[]{127, 0, 0, 1};
    @Test
    public void parseHosts() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("kikuko72/app/logic/util/hosts");
        Map<RecordKey, RecordValue> recordStore = HostsReader.parseHosts(stream);

        RecordKey fooKey = new RecordKey("foo.", RecordType.A_RECORD, RecordClass.INTERNET);
        RecordValue fooValue = new RecordValue(RecordType.A_RECORD.bytes(), HostsReader.DEFAULT_TTL, LOCALHOST_ADDRESS);
        Assert.assertEquals(fooValue, recordStore.get(fooKey));

        RecordKey fizzKey = new RecordKey("fizz.", RecordType.A_RECORD, RecordClass.INTERNET);
        RecordValue fizzValue = new RecordValue(RecordType.A_RECORD.bytes(), HostsReader.DEFAULT_TTL, LOCALHOST_ADDRESS);
        Assert.assertEquals(fizzValue, recordStore.get(fizzKey));

        RecordKey buzzKey = new RecordKey("buzz.", RecordType.A_RECORD, RecordClass.INTERNET);
        RecordValue buzzValue = new RecordValue(RecordType.CNAME_RECORD.bytes(), HostsReader.DEFAULT_TTL, new RecordName("fizz.").bytes());
        Assert.assertEquals(buzzValue, recordStore.get(buzzKey));
    }

}
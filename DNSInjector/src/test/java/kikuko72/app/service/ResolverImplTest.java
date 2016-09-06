package kikuko72.app.service;

import kikuko72.app.main.DNSInjector;
import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.value.RecordValue;
import kikuko72.app.model.record.ResourceRecord;
import kikuko72.app.model.record.identifier.Class;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.Type;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/09/05.
 */
public class ResolverImplTest {

    /**
     * 問い合わせ結果がキャッシュされることを確認する
     * キャッシュする理由はDelegate先に何度も問い合わせて負荷をかけないため。
     * そのためDelegateのモックを用意し、2回目のリクエストがモックに飛ばなければよい
     * ちゃんと名前解決できることと名前解決した結果が等しいことも確認する
     * @throws IOException
     */
    @Test
    public void cacheTest() throws IOException {
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

        RecordKey hogeIpv4 = new RecordKey("hoge.", Type.A, Class.INTERNET);
        Inet4Address localhost = (Inet4Address)InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
        RecordValue localhostData = new RecordValue(DNSInjector.DEFAULT_TTL, localhost);
        Map<RecordKey, RecordValue> recordStore = new HashMap<RecordKey, RecordValue>();
        Resolver resolver = new ResolverImpl(createMockDelegate(hogeIpv4, localhostData), recordStore);

        DNSMessage actual1 = resolver.resolve(queryMessage);
        DNSMessage actual2 = resolver.resolve(queryMessage);

        Assert.assertTrue("cannot resolve.", actual1.getAllResourceRecords().size() > 0);
        Assert.assertEquals(actual1, actual2);

        ResourceRecord actualRecord = actual1.getAllResourceRecords().get(0);
        RecordKey actualKey = actualRecord.getRecordKey();
        RecordValue actualValue = actualRecord.getRecordValue();

        Assert.assertEquals(hogeIpv4, actualKey);
        Assert.assertEquals(localhostData, actualValue);
    }

    private Delegate createMockDelegate(RecordKey hogeIpv4, RecordValue localhostData) {
        final Map<RecordKey, RecordValue> recordStore = new HashMap<RecordKey, RecordValue>();
        recordStore.put(hogeIpv4, localhostData);
        final Injector injector = new Injector(recordStore);
        return new Delegate() {
            boolean isFirst = true;
            @Override
            public DNSMessage resolve(DNSMessage request) throws IOException {
                if (isFirst) {
                    this.isFirst = false;
                } else {
                    Assert.fail();
                }
                return injector.resolve(request);
            }
        };
    }
}
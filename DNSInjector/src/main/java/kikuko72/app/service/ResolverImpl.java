package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.RecordValue;
import kikuko72.app.model.record.identifier.RecordKey;

import java.io.IOException;
import java.util.Map;

/**
 * Created by User on 2016/09/05.
 */
public class ResolverImpl implements Resolver {
    private Delegate delegate;
    private Injector injector;

    public ResolverImpl(Delegate delegate, Map<RecordKey, RecordValue> recordStore) {
        this.delegate = delegate;
        this.injector = new Injector(recordStore);
    }

    public DNSMessage resolve(DNSMessage request) throws IOException {
        DNSMessage response = injector.resolve(request);
        if (response != null) {
            return response;
        }

        response = delegate.resolve(request);
        injector.cache(response.getAllResourceRecords());
        return response;

    }
}

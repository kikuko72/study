package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by User on 2016/09/05.
 */
public class ResolverImpl implements Resolver {
    private Delegate delegate;
    private Injector injector;

    public ResolverImpl(String delegateHost, String hostsFilePath) throws IOException {
        InetAddress delegateHostAddress = InetAddress.getByName(delegateHost);
        this.delegate = new DelegateImpl(delegateHostAddress.getAddress());
        this.injector = new Injector(new RecordStore(hostsFilePath));
    }

    ResolverImpl(Delegate delegate, Injector injector) {
        this.delegate = delegate;
        this.injector = injector;
    }

    public DNSMessage resolve(DNSMessage request) throws IOException {
        DNSMessage response = injector.resolve(request);
        if (response != null) {
            return response;
        }

        response = delegate.resolve(request);
        // FIXME 現状DNSラウンドロビンを無効化してしまう問題がある
        injector.cache(response.getAllResourceRecords());
        return response;

    }
}

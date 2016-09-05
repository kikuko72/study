package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;
import kikuko72.app.model.record.identifier.RecordKey;
import kikuko72.app.model.record.identifier.RecordType;

import java.io.IOException;
import java.util.List;

/**
 * Created by User on 2016/09/05.
 */
public class ResolverImpl implements Resolver {
    private Delegate delegate;

    public ResolverImpl(Delegate delegate) {
        this.delegate = delegate;
    }

    public DNSMessage resolve(DNSMessage request) throws IOException {
        List<RecordKey> queries = request.getQueries();
        if (canResolve(queries.get(0))) { // ひとまず複数の質問のあるメッセージへの対応は保留
            Injector injector = new Injector();
            return injector.resolve(request);
        } else {
            return delegate.resolve(request);
        }
    }

    private static boolean canResolve(RecordKey query) {
        return "hoge.".equals(query.getDomainName()) && query.isType(RecordType.A_RECORD);
    }
}

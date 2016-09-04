package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;

import java.io.IOException;

/**
 * Created by User on 2016/06/12.
 */
public interface Resolver {
    DNSMessage resolve(DNSMessage request) throws IOException;
}

package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by User on 2016/06/12.
 */
public interface Resolver {
    public DNSMessage resolve(DNSMessage request) throws IOException;
}

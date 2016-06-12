package kikuko72.app.service;

import kikuko72.app.model.message.DNSMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created by User on 2016/06/12.
 */
public class DNS {
    public static final int DNS_PORT_NUMBER = 53;
    public static final int DNS_UDP_MAX_BYTES = 512;

    public static DatagramPacket createReceivePacket() {
        return  new DatagramPacket(new byte[DNS_UDP_MAX_BYTES], DNS_UDP_MAX_BYTES);
    }

    public static DatagramPacket createQueryPacket(DNSMessage query, InetAddress destination) {
        return new DatagramPacket(query.bytes(), query.bytes().length, destination, DNS_PORT_NUMBER);
    }
}

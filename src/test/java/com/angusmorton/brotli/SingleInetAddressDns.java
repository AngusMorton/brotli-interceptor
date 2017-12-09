package com.angusmorton.brotli;

import okhttp3.Dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class SingleInetAddressDns implements Dns {
  @Override public List<InetAddress> lookup(String hostname) throws UnknownHostException {
    List<InetAddress> addresses = Dns.SYSTEM.lookup(hostname);
    return Collections.singletonList(addresses.get(0));
  }
}
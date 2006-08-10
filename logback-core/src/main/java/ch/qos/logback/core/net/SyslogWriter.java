package ch.qos.logback.core.net;

import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * SyslogWriter is a wrapper around the {@link DatagramSocket} class so that it
 * behaves like a {@link Writer}.
 */
class SyslogWriter extends Writer {

  /**
   * The maximum length after which we discard the existing string buffer and
   * start anew.
   */
  private static final int MAX_LEN = 1024;

  private InetAddress address;
  private DatagramSocket ds;
  private StringBuffer buf = new StringBuffer();
  final private int port;

  public SyslogWriter(String syslogHost, int port) throws UnknownHostException,
      SocketException {
    this.address = InetAddress.getByName(syslogHost);
    this.port = port;
    this.ds = new DatagramSocket();
  }

  public void write(char[] charArray, int offset, int len) throws IOException {
    buf.append(charArray, offset, len);
  }

  public void write(String str) throws IOException {
    buf.append(str);

  }

  public void flush() throws IOException {
    byte[] bytes = buf.toString().getBytes();
    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address,
        port);

    if (this.ds != null) {
      ds.send(packet);
    }
    // clean up for next round
    if (buf.length() > MAX_LEN) {
      buf = new StringBuffer();
    } else {
      buf.setLength(0);
    }
  }

  public void close() {
    address = null;
    ds = null;
  }

  public int getPort() {
    return port;
  }

}

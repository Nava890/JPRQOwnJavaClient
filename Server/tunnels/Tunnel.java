package tunnels;

import events.ConnectionReceived;
import events.Event;
import server.TCPServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Tunnel implements TunnelInterface {
  public String hostName;
  public int maxConLimit;
  public OutputStream eventWriter;
  public static final ReentrantLock eventWriterMx = new ReentrantLock();;
  public TCPServer privateServer;
  public Map<Short, Socket> publicCons;
  public Map<Short, byte[]> initialBuffer;

  Tunnel(String hostName, OutputStream eventWriter, int maxConLimit) {
    this.hostName = hostName;
    this.maxConLimit = maxConLimit;
    this.eventWriter = eventWriter;
    this.publicCons = new HashMap<Short, Socket>();
    this.initialBuffer = new HashMap<Short, byte[]>();
    this.privateServer = new TCPServer();
  }

  @Override
  public void open() {

  }

  @Override
  public void close() {
    try {
      this.privateServer.close();
    } catch (IOException e) {
      System.out.println("Error closing Tunnel");
    }
    for (Map.Entry<Short, Socket> entry : publicCons.entrySet()) {
      Socket con = entry.getValue(); // Get the connection
      try {
        if (con != null) {
          con.close(); // Close the connection
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      short port = entry.getKey();
      publicCons.remove(port);
      initialBuffer.remove(port);
    }
  }

  @Override
  public String hostName() {
    return hostName;
  }

  @Override
  public String protocol() {
    return "http";
  }

  @Override
  public int publicServerport(Socket publicCon) {
    return this.privateServer.port();
  }

  public boolean publicConnectionHandler(Socket publicCon) {
    InetAddress inetAddress = publicCon.getInetAddress();
    String ip = inetAddress.getHostAddress();
    int port = publicCon.getPort();
    short portAsShort = (short) port;
    eventWriterMx.lock();
    if (publicCons.size() >= maxConLimit) {
      ConnectionReceived connectionReceived = new ConnectionReceived(ip, true);
      Event<ConnectionReceived> event = new Event<>(connectionReceived);
      closeTheSocket(publicCon);
      event.write(this.eventWriter);
      System.out.println("Connection limit reached");
      return false;
    }
    ConnectionReceived connectionReceived = new ConnectionReceived(ip, portAsShort, false);
    Event<ConnectionReceived> event = new Event<>(connectionReceived);
    if (!event.write(this.eventWriter)) {
      System.out.println("cannot write event");
      closeTheSocket(publicCon);
      return false;
    }
    this.publicCons.put(portAsShort, publicCon);
    return true;

  }

  @Override
  public int privateServerport(Socket publicCon) {
    return 0;
  }

  public void closeTheSocket(Socket publicCon) {
    try {
      publicCon.close();
    } catch (IOException e) {
      System.out.println("Error closing Tunnel");
    }
  }

  public void privateConnectionHandler(Socket listener) {
    byte[] buffer = new byte[2];

    try {
      InputStream inputStream = listener.getInputStream();
      int read = inputStream.read(buffer);
    } catch (IOException e) {
      System.out.println("Unable to read the port from the client");
      return;
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    short port = byteBuffer.getShort();
    if (!publicCons.containsKey(port)) {
      System.out.println("public connection is not found. cannot pair");
      return;
    }
    Socket publicCon = publicCons.get(port);
    publicCons.remove(port);
    if (initialBuffer.get(port).length > 0) {
      try {
        OutputStream outputbuffer = listener.getOutputStream();
        outputbuffer.write(initialBuffer.get(port));
      } catch (IOException e) {
        System.out.println("unable to write the port to output buffer");
      }
    }
    try {
      bind(listener, publicCon);
      bind(publicCon, listener);
    } catch (IOException e) {
      System.out.println("Error while binding public con and private con");
      return;
    }

  }

  public static void bind(Socket src, Socket dst) throws IOException {
    try (src;
        dst;
        InputStream srcIn = src.getInputStream();
        OutputStream dstOut = dst.getOutputStream()) {

      byte[] buf = new byte[4096];
      while (true) {
        int bytesRead;
        try {
          src.setSoTimeout(1000);
          bytesRead = srcIn.read(buf);
          if (bytesRead == -1) {
            break;
          }
        } catch (IOException e) {
          break;
        }

        try {
          dst.setSoTimeout(1000);
          dstOut.write(buf, 0, bytesRead);
        } catch (IOException e) {
          throw new IOException("Error writing to destination socket", e);
        }

        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt(); // Restore interrupt flag
          throw new IOException("Thread interrupted", e);
        }
      }
    }
  }
}

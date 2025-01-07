package events;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Event<EventType> {
  public static final String HTTP = "http";
  private EventType data;

  public Event(EventType data) {
    this.data = data;
  }

  public Event() {

  }

  public EventType getData() {
    return data;
  }

  public void setData(EventType data) {
    this.data = data;
  }

  public boolean read(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[2];
    int read = inputStream.read(buffer);
    if (read != 2) {
      throw new IOException("Failed to read length bytes from the stream.");
    }
    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    short length = byteBuffer.getShort();

    buffer = new byte[length];
    read = inputStream.read(buffer);
    if (read != length) {
      throw new IOException("Failed to read the full data based on the specified length.");
    }
    return decode(buffer);
  }

  @SuppressWarnings("unchecked")
  public boolean decode(byte[] dataBytes) {
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
      // Deserialize the object and assign it to 'data'
      this.data = (EventType) objectInputStream.readObject();
      return true;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  public byte[] encode() {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

      objectOutputStream.writeObject(data);

      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      System.out.println("Unable to encode the data");
      return null;
    }
  }

  public boolean write(OutputStream outputStream) {
    byte[] data = encode();

    ByteBuffer byteBuffer = ByteBuffer.allocate(2);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort((short) data.length);
    try {
      outputStream.write(data);
    } catch (IOException e) {
      System.out.println("Unable to write the data");
      return false;
    }
    return true;
  }
}

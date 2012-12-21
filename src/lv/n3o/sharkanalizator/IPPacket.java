package lv.n3o.sharkanalizator;

import java.net.InetAddress;

public class IPPacket extends Packet
{
  private byte[] data;
  public InetAddress dst_ip;
  public InetAddress src_ip;

  public byte[] getData()
  {
    if (this.data == null);
    for (byte[] arrayOfByte = new byte[0]; ; arrayOfByte = this.data)
      return arrayOfByte;
  }

  public void setData(byte[] paramArrayOfByte)
  {
    this.data = paramArrayOfByte;
  }

  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(this.src_ip.toString())).append("\n").append(this.dst_ip.toString()).append("\n").append("Size: ");
    //TODO
    return localStringBuilder.toString();
  }
}

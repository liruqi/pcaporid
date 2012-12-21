package lv.n3o.sharkanalizator;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public class PcapParser
{
  public static final int capLenOffset = 8;
  public static final int etherHeaderLength = 14;
  public static final int etherTypeIP = 2048;
  public static final int etherTypeOffset = 12;
  public static final int globalHeaderSize = 24;
  public static final int ipDstOffset = 30;
  public static final int ipProtoOffset = 23;
  public static final int ipProtoTCP = 6;
  public static final int ipProtoUDP = 17;
  public static final int ipSrcOffset = 26;
  public static final long pcapMagicNumber = -1582119980L;
  public static final int pcapPacketHeaderSize = 16;
  public static final int udpHeaderLength = 8;
  public static final int verIHLOffset = 14;
  private FileInputStream fis;

 


  private long convertInt(byte[] paramArrayOfByte)
  {
    return (0xFF & paramArrayOfByte[3]) << 24 | (0xFF & paramArrayOfByte[2]) << 16 | (0xFF & paramArrayOfByte[1]) << 8 | 0xFF & paramArrayOfByte[0];
  }

  private long convertInt(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    System.arraycopy(paramArrayOfByte, paramInt, arrayOfByte, 0, arrayOfByte.length);
    return convertInt(arrayOfByte);
  }

  private int convertShort(byte[] paramArrayOfByte)
  {
    return (0xFF & paramArrayOfByte[0]) << 8 | 0xFF & paramArrayOfByte[1];
  }

  private int convertShort(byte[] paramArrayOfByte, int paramInt)
  {
    byte[] arrayOfByte = new byte[2];
    System.arraycopy(paramArrayOfByte, paramInt, arrayOfByte, 0, arrayOfByte.length);
    return convertShort(arrayOfByte);
  }

  private int getIPHeaderLength(byte[] paramArrayOfByte)
  {
    return 4 * (0xF & paramArrayOfByte[14]);
  }

  private int getTCPHeaderLength(byte[] paramArrayOfByte)
  {
    return 4 * (0xF & paramArrayOfByte[(12 + (14 + getIPHeaderLength(paramArrayOfByte)))] >> 4);
  }

  private boolean isIPPacket(byte[] paramArrayOfByte)
  {
    return (convertShort(paramArrayOfByte, 12) == 2048);
   
  }

  private boolean isTCPPacket(byte[] paramArrayOfByte)
  {
    boolean bool;
    if (!isIPPacket(paramArrayOfByte))
      bool = false;
    
      try
      {
        int i = paramArrayOfByte[23];
        if (i == 6)
          bool = true;
        else
          bool = false;
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        bool = false;
      }
    
    return bool;
  }

  private boolean isUDPPacket(byte[] paramArrayOfByte)
  {
    boolean bool;
    if (!isIPPacket(paramArrayOfByte))
      bool = false;

      
      
        int i = paramArrayOfByte[23];
        if (i == 17)
          bool = true;
        else
          bool = false;
     
    return bool;
  }

  private int readBytes(byte[] paramArrayOfByte)
  {
        try
        {
          int m = this.fis.read(paramArrayOfByte, 0, paramArrayOfByte.length);
          
          if (m == paramArrayOfByte.length)
            return m;
        }
        catch (Exception localException)
        {
        	return -1;
        }
    
    return -1;
  }

  private int readGlobalHeader()
  {
    byte[] arrayOfByte = new byte[24];
    int i;
    if (readBytes(arrayOfByte) == -1)
      i = -1;
    
      else
        i = 0;
      return i;
  }

  public void closeFile()
  {
    try
    {
      this.fis.close();
    }
    catch (Exception localException)
    {
      Log.i("PcapParser", "closeFile error");
    }
  }

  public Packet getPacket()
  {
    byte[] arrayOfByte1 = new byte[16];
    Packet localObject;
    if (readBytes(arrayOfByte1) < 0)
      return Packet.EOF;
    
    byte[] arrayOfByte2 = new byte[(int)convertInt(arrayOfByte1, 8)];
      if (readBytes(arrayOfByte2) < 0)
        localObject = Packet.EOF;
      else {
    	  Log.i("PcapParser", " "+ arrayOfByte2);
          localObject = new Packet(arrayOfByte2);
      }
    return localObject;
  }

  public int openFile(String paramString)
  {
    try
    {
      this.fis = new FileInputStream(new File(paramString));
      if (readGlobalHeader() < 0)
      {
        return -1;
      }
    }
    catch (Exception localException)
    {
      return -2;
    }
    return 0;
  }
  
  private IPPacket buildIPPacket(byte[] paramArrayOfByte)
  {
    IPPacket localIPPacket1 = new IPPacket();
    byte[] arrayOfByte1 = new byte[4], arrayOfByte2 = new byte[4];
    try
    {
      System.arraycopy(paramArrayOfByte, 26, arrayOfByte1, 0, arrayOfByte1.length);
      localIPPacket1.src_ip = InetAddress.getByAddress(arrayOfByte1);
     
      System.arraycopy(paramArrayOfByte, 30, arrayOfByte2, 0, arrayOfByte2.length);
      localIPPacket1.dst_ip = InetAddress.getByAddress(arrayOfByte2);
    }
    catch (Exception localException1)
    {
      
    }
    return localIPPacket1;
  }


}
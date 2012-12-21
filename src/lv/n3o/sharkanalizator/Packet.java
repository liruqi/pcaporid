package lv.n3o.sharkanalizator;

import java.util.ArrayList;

import android.util.Log;

public class Packet
{
  public static final Packet EOF = new Packet();
  public ArrayList<String> tags;
  public byte[]  srcmac,dstmac;
  public Packet () {
  }
  public Packet (byte[] b) {
	  srcmac = new byte[6];
	  System.arraycopy(b, 0, srcmac, 0, 6);
	  dstmac = new byte[6];
	  System.arraycopy(b, 6, dstmac, 0, 6);
	  Log.i("Packet", srcmac.toString() + " - " + dstmac.toString());
  }
  public String toString()
  {
    return "Packet";
  }
  
}
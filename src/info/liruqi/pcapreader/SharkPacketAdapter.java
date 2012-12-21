package info.liruqi.pcapreader;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import lv.n3o.sharkanalizator.Packet;
// import lv.n3o.sharkanalizator.PatternMatcher;

public class SharkPacketAdapter extends BaseAdapter
implements Runnable
{
    Context c;
    String[] filters = new String[0];
    ArrayList<Packet> incoming = new ArrayList();
    Messager messager = new Messager();
    ArrayList<Packet> packets = new ArrayList();
    // PatternMatcher pm;
    boolean show = true;
    Thread worker = null;

    public SharkPacketAdapter(Context paramContext)
    {
        this.c = paramContext;
    }

    private Packet getIncomingPacket()
    {

        Packet localPacket = (Packet)this.incoming.remove(0);
        return localPacket;

    }

    public void append(Packet paramPacket)
    {
        try
        {
            this.incoming.add(paramPacket);
            startWorker();
            return;
        }
        finally
        {
            // localObject = finally;
            // throw localObject;
        }
    }

    public int getCount()
    {
        return this.packets.size();
    }

    public Object getItem(int paramInt)
    {
        return this.packets.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
        View localView = paramView;

        if (localView == null)
            localView = ((LayoutInflater)this.c.getSystemService("layout_inflater")).inflate(R.layout.packetrow, null);
        TextView localTextView1;
        TextView localTextView2;
        //Iterator localIterator;
        Packet p = this.packets.get(paramInt);
        if (p != null)
        {
            localTextView1 = (TextView)localView.findViewById(R.id.text1);
            localTextView2 = (TextView)localView.findViewById(R.id.text2);

           
            localTextView1.setText(PcapReaderViewer.getHex( p.srcmac ));
            //localTextView2.setText(PcapReaderViewer.getHex( p.dstmac));
            localTextView2.setText(PcapReaderViewer.getHex(p.dstmac).subSequence(0, 2));
            
            
        }

        return localView;
    }

	public int progress()
    {
        int i = 0;
        try
        {
            int j = this.packets.size() + this.incoming.size();
            i = 100 * this.packets.size() / j;
            return i;
        }
        catch (ArithmeticException localArithmeticException)
        {
            return i;
        }
    }

    public void run()
    {
        while (true)
        {
            Packet localPacket;
            try
            {
                Thread.sleep(500L);
                if (this.incoming.isEmpty())
                {
                    this.worker = null;
                    return;
                }

                localPacket = getIncomingPacket();

                this.messager.addPacket(localPacket);

            }
            catch (InterruptedException localInterruptedException)
            {
                localInterruptedException.printStackTrace();
                continue;

            }

        }
    }


    public void setFilters(boolean paramBoolean, String paramString)
    {
        this.show = true;
        this.filters = paramString.split(" ");
    }

    void startWorker()
    {
        if (this.worker == null)
        {
            this.worker = new Thread(this);
            this.worker.start();
        }
    }

    public class Messager extends Handler
    {
        public Messager()
        {
        }

        public void addPacket(Packet paramPacket)
        {
            Message localMessage = obtainMessage();
            localMessage.obj = paramPacket;
            sendMessage(localMessage);
        }

        public void handleMessage(Message paramMessage)
        {
            SharkPacketAdapter.this.packets.add((Packet)paramMessage.obj);
            SharkPacketAdapter.this.notifyDataSetChanged();
        }
    }
}

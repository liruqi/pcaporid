package info.liruqi.pcapreader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import lv.n3o.sharkanalizator.IPPacket;
import lv.n3o.sharkanalizator.Packet;
import lv.n3o.sharkanalizator.PcapParser;


public class PcapReaderViewer extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, Runnable
{
    static final String HEXES = "0123456789ABCDEF";
    String filters = "";
    ListView mainView;
    Button okbutton;
    SharkPacketAdapter pa;
    ArrayList<Packet> packets;
    int progress = 0;
    private Runnable progressCheck = new Runnable()
    {
        public void run()
        {
            PcapReaderViewer.this.progress = PcapReaderViewer.this.pa.progress();
            if (PcapReaderViewer.this.progress == -1)
            {
                PcapReaderViewer.this.toast("Empty file opened");
                return;
            }
            ProgressBar localProgressBar = (ProgressBar)PcapReaderViewer.this.findViewById(R.id.Progress);
            if (PcapReaderViewer.this.progress == 100) {
                localProgressBar.setVisibility(8);

                localProgressBar.setProgress(PcapReaderViewer.this.progress);
            } else {
                localProgressBar.setVisibility(0);
            }
        }
    };

    private Handler progressIndicator = new Handler();
    boolean show = true;

    public static String getHex(byte[] paramArrayOfByte)
    {
        if (paramArrayOfByte == null) return "";
        StringBuilder localStringBuilder;
        int i;
        int k;

        localStringBuilder = new StringBuilder(3 * paramArrayOfByte.length);
        for( i = 0; i< paramArrayOfByte.length; i ++) {

            int m = paramArrayOfByte[i];

            localStringBuilder.append("0123456789ABCDEF".charAt((m & 0xF0) >> 4)).append("0123456789ABCDEF".charAt(m & 0xF));
            localStringBuilder.append(" ");

        }
        return localStringBuilder.toString();
    }

    private void populateList()
    {
        this.pa = new SharkPacketAdapter(this);
        this.mainView = ((ListView)findViewById(R.id.PacketViewer));
        this.mainView.setOnItemClickListener(this);
        this.mainView.setAdapter(this.pa);
        this.pa.setFilters(this.show, this.filters);
        boolean found = false;
        for(Packet p : this.packets) {
        	this.pa.append(p);
        	String src=PcapReaderViewer.getHex( p.srcmac );
        	if (src.startsWith("68 A8 6D 59 29 B1") || src.startsWith("10 40 F3 8E B3")) {
        		found = true;
            }
        }
        if (found) {
        	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        	r.play();
        }
    }

    private void toast(String paramString)
    {
        Toast localToast = Toast.makeText(this, "Shark error:\n" + paramString, 1);
        localToast.setGravity(17, localToast.getXOffset() / 2, localToast.getYOffset() / 2);
        localToast.show();
    }

    public void onClick(View paramView)
    {
        Log.i("PcapReaderViewer","onClick");
        if (paramView.equals(this.okbutton))
        {
            this.show = true;
            this.filters = ((EditText)findViewById(R.id.FilterText)).getText().toString();
            populateList();
        }
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.viewer);

        this.okbutton = ((Button)findViewById(R.id.Button01));
        this.okbutton.setOnClickListener(this);
        this.packets = new ArrayList<Packet>();
        PcapParser localPcapParser = new PcapParser();
        Log.d("SharkR", getIntent().getDataString().replace("file://", ""));
        localPcapParser.openFile(getIntent().getDataString().replace("file://", ""));
        while (true)
        {
            Packet localPacket = localPcapParser.getPacket();
            if (localPacket == Packet.EOF)
            {
                populateList();
                new Thread(this).start();
                return;
            }
            if (localPacket != null)
                this.packets.add(localPacket);
        }
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        onItemClick((ListView)paramAdapterView, paramView, paramInt, paramLong);
    }

    protected void onItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
    {
        Dialog localDialog = new Dialog(this);
        localDialog.setContentView(R.layout.popupdialog);
        localDialog.show();
        localDialog.setCancelable(true);
        localDialog.setTitle("Packet #" + paramInt);
        Packet localPacket = (Packet)this.pa.getItem(paramInt);
        ((TextView)localDialog.findViewById(R.id.tview)).setText("From:" +getHex((localPacket).srcmac)+ "\n\nTo:" + getHex((localPacket).dstmac));
    }

    public void run()
    {
        while (true)
        {
            Log.i("run thread", " "+ this.progressCheck);
            try
            {
                Thread.sleep(1000L);
                this.progressIndicator.post(this.progressCheck);
                if (this.progress == 100)
                    return;
            }
            catch (InterruptedException localInterruptedException)
            {
                localInterruptedException.printStackTrace();
                continue;
            }
            if (this.progress == -1)
                finish();
        }
    }
}

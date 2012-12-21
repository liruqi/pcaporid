package info.liruqi.pcapreader;

import java.io.File;
import java.io.FilenameFilter;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PcapReaderActivity extends ListActivity {

	  String[] mFiles;

	  private void toast(String paramString)
	  {
	    Toast localToast = Toast.makeText(this, "Shark error:\n" + paramString, 1);
	    localToast.setGravity(17, localToast.getXOffset() / 2, localToast.getYOffset() / 2);
	    localToast.show();
	  }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pcap_reader);

	    //Mobclix.onCreate(this);
	    File[] arrayOfFile = Environment.getExternalStorageDirectory().listFiles(new FilenameFilter()
	    {
	      public boolean accept(File paramAnonymousFile, String paramAnonymousString)
	      {
	        return paramAnonymousString.endsWith(".pcap");
	      }
	    });
	    if (arrayOfFile == null) return;
	    try
	    {
	      if (arrayOfFile.length == 0)
	      {
	        toast("No pcap files found.");
	        finish();
	      }
	      this.mFiles = new String[arrayOfFile.length];
	      for (int i = 0; ; i++)
	      {
	        if (i >= arrayOfFile.length)
	        {
	          ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, this.mFiles);
	          setListAdapter(arrayAdapter);
	          break;
	        }
	        this.mFiles[i] = (arrayOfFile[i].getAbsolutePath() + "\n" + arrayOfFile[i].length() + " bytes");
	      }
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	      toast("No pcap files found.");
	      finish();
	    }
	  }


	  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
	  {
	    File localFile = new File(this.mFiles[paramInt].split("\n")[0]);
	    Intent localIntent;
	    if (!localFile.exists()) return;
	    
	      Uri localUri = Uri.fromFile(localFile);
	      localIntent = new Intent("android.intent.action.VIEW");
	      localIntent.setDataAndType(localUri, "application/x-pcap");
	      localIntent.setFlags(67108864);
	    
	    try
	    {
	      startActivity(localIntent);
	      return;
	    }
	    catch (ActivityNotFoundException localActivityNotFoundException)
	    {
	        toast("No Application Available to view pcap files. You may install SharkReader for reading pcap files.");
	    }
	  }


}

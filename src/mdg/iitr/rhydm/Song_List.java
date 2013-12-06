package mdg.iitr.rhydm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ListView;

public class Song_List extends Activity {
	
	ListView list;
	Cursor cs;
	
	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
	String order = MediaStore.Audio.Media.TITLE;
	String[] projection = {	MediaStore.Audio.Media._ID,
							MediaStore.Audio.Media.TITLE,
							MediaStore.Audio.Media.ARTIST,
							MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.DURATION
							};
		
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_list);
		
		cs = this.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, order+" COLLATE NOCASE");
		
		List<String> description = new ArrayList<String>();
		List<String> refernce = new ArrayList<String>();
		while(cs.moveToNext())
		{
			description.add(cs.getString(1) + "||" + cs.getString(2));
			refernce.add(cs.getString(1));
		}
		
		Globals.all_list = description;
		
		List_Adapter adaptor = new List_Adapter(Song_List.this, description, refernce);
		
		list = (ListView) findViewById(R.id.Music_List);
		try{
		list.setAdapter(adaptor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}

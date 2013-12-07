package mdg.iitr.rhydm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Song_List extends Activity {
	
	ListView list;
	Cursor cs;
	
	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
	String order = MediaStore.Audio.Media.TITLE;
	/*String[] projection = {	MediaStore.Audio.Media._ID,
							MediaStore.Audio.Media.TITLE,
							MediaStore.Audio.Media.ARTIST,
							MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.DURATION
							};
*/		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_list);
		
		//cs = this.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, order+" COLLATE NOCASE");
		cs = Globals.cursor;
		cs.moveToFirst();
		
		
		List<String> description = new ArrayList<String>();
		List<String> refernce = new ArrayList<String>();
		do
		{
			description.add(cs.getString(1) + "||" + cs.getString(2));
			refernce.add(cs.getString(1));
		}while(cs.moveToNext());
		
		
		List_Adapter adaptor = new List_Adapter(Song_List.this, description, refernce);
		
		list = (ListView) findViewById(R.id.Music_List);
		try{
		list.setAdapter(adaptor);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Globals.c_pos = position;
				Globals.list_sel = true;
				finish();
			}
		});
		
	}
}

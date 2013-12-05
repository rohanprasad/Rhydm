package mdg.iitr.rhydm;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SongList extends ListActivity {

	// List of songs in an array
	public ArrayList<HashMap<String,String>> song_list = new ArrayList<HashMap<String,String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.songlist);
		
		ArrayList<HashMap<String,String>> data_list = new ArrayList<HashMap<String,String>>();
		
		// get all the details from the sdcard 
		Music_Manager M_man = new Music_Manager();
		this.song_list = M_man.getList();
		
		//adding the data to another array
		for(int i=0; i<song_list.size() ; i++){
			HashMap<String,String> song = song_list.get(i);
			data_list.add(song);
		}
		
		// Adding items via adapter
		ListAdapter adaptor = new SimpleAdapter(this, data_list, R.layout.items_songlist, new String[] {"Title"}, new int[] {R.id.song_title});
		setListAdapter(adaptor);
		
		//Song Selection
		ListView lv = getListView();
		//on Item click Listener
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long id) {
				// TODO Auto-generated method stub
				
				/*Intent i = new Intent(getApplicationContext(), Player.class);
				i.putExtra("Index",position);
				startActivityForResult(i,100);
				finish();*/
				Globals.Songid = position;
				finish();
			}
		});
	}
	
	
	
}

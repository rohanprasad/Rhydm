package mdg.iitr.rhydm;


import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class List_Adapter extends ArrayAdapter<String> {

	private final Activity context;
	private final List<String> Description;
	
	//constructor
	public List_Adapter(Activity context, List<String> data, List<String> id) {
		super(context, R.layout.items_songlist, id);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.Description = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflator = context.getLayoutInflater();
		
		View my_view = inflator.inflate(R.layout.items_songlist, null, false);
		
		TextView song_t = (TextView) my_view.findViewById(R.id.song_title);
		TextView song_a = (TextView) my_view.findViewById(R.id.song_art);
		
		try{
		String temp = Description.get(position);
		String temp1 = temp.substring(0, temp.indexOf("||")-4);
		String temp2 = temp.substring((temp.indexOf("||")) + 2 , temp.length());
		song_t.setText(temp1);
		song_a.setText(temp2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return my_view;
	}
}

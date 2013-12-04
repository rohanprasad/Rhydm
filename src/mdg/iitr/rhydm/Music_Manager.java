package mdg.iitr.rhydm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Environment;

public class Music_Manager {
	
		private ArrayList<HashMap<String,String>> Songs = new ArrayList<HashMap<String,String>> ();
		
		public Music_Manager(){
		};
		
		public ArrayList<HashMap<String,String>> getList(){
			
			File f = Environment.getExternalStorageDirectory();
			traversals(f);				
			return Songs;
		}
		
		public void traversals(File fi)
		{
			File subs[] = fi.listFiles();
			
			if(subs != null)
			{
				 for (int i = 0; i < subs.length; i++) 
				 {
					 if (subs[i].isDirectory())
					 {
						 traversals(subs[i]);
					 }
				     else if (subs[i].getName().endsWith(".mp3"))
			         {
			    	    HashMap<String,String> song = new HashMap<String,String>();
						song.put("Title", subs[i].getName().substring(0 , subs[i].getName().length()-4));
						song.put("Path", subs[i].getPath());
						Songs.add(song);
			         }
				 }
			}
		};
		
		
	}
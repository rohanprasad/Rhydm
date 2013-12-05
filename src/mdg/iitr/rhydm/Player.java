package mdg.iitr.rhydm;

import java.util.ArrayList;
import java.util.HashMap;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Player extends Activity {

	private ImageButton b_play;
	private ImageButton b_stop;
	private Button s_list;
	private Button b_next;
	private Button b_prev;
	private MediaPlayer m_player = new MediaPlayer();;
	private Music_Manager m_manager;
	private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private TextView tv;
	static int is = 0;
	static int max_songs = 0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        
        
        
        // To prevent Screen Rotation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        
        
        
        b_play = (ImageButton) findViewById(R.id.btn_play);
        b_stop = (ImageButton) findViewById(R.id.btn_stop);
        
        s_list = (Button) findViewById(R.id.song_list);
        b_next = (Button) findViewById(R.id.btn_next);
        b_prev = (Button) findViewById(R.id.btn_prev);
        
        tv = (TextView) findViewById(R.id.textView1);
        
        
        m_manager= new Music_Manager();
        
        // get the list of songs
        songsList = m_manager.getList();
        
        max_songs=songsList.size()-1;
        
        playSong(is);
        m_player.pause();
                
        //play if paused
        b_play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!(m_player.isPlaying()))
				{
					m_player.start();
					b_play.setImageResource(R.drawable.pause_btn);
				}
				else {
					m_player.pause();
					b_play.setImageResource(R.drawable.play_btn);					
				}
			}
		});
        
        
        b_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				m_player.stop();
				b_play.setImageResource(R.drawable.play_btn);
				playSong(is);
				m_player.pause();
			}
		});
        
        // show song List (NOT WORKING)
        s_list.setOnClickListener(new View.OnClickListener() {
        	 
            @Override
            public void onClick(View arg0) {
            	
               Intent i = new Intent(getApplicationContext(), SongList.class);
                startActivity(i);
            }
        });
        
        b_next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(is==max_songs)
					is=0;
				else
					is++;
				m_player.stop();
				playSong(is);
				b_play.setImageResource(R.drawable.pause_btn);
				
			}
		});
        
        b_prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(is==0)
					is= max_songs;
				else
					is--;
				
				playSong(is);
				b_play.setImageResource(R.drawable.pause_btn);
			}
		});
        
    }

    
    
    //Function to play a song
    public void playSong(int index)
    {
    	try{
    		m_player.reset();
    		m_player.setDataSource(songsList.get(index).get("Path"));
    		m_player.prepare();
    		m_player.start();
    		
    		String titles = songsList.get(index).get("Title");
    		tv.setText(titles);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    
}

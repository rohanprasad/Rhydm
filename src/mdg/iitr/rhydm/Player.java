package mdg.iitr.rhydm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Player extends Activity {

	private ImageButton b_play;
	private ImageButton b_stop;
	private Button s_list;
	private Button b_next;
	private Button b_prev;
	private MediaPlayer m_player = new MediaPlayer();
	private SeekBar s_bar;
	private Music_Manager m_manager;
	private static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private TextView tv;
	private TextView song_length;
	private TextView time_elap;
	Handler s_bar_handler = new Handler();
	static int max_length;
	static int is = 0;
	static int max_songs = 0;
	static int new_pos = 0;
	boolean wait = false;
	static int wait_pos = 0;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
                
        // To prevent Screen Rotation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        song_length = (TextView) findViewById(R.id.tv_duration);
        time_elap = (TextView) findViewById(R.id.tv_time_elapsed);
        
        s_bar = (SeekBar) findViewById(R.id.progress_bar);
        
        
        m_manager= new Music_Manager();
        
        // get the list of songs
        songsList = m_manager.getList();
        
        max_songs=songsList.size()-1;
        
        playSong(is);
        m_player.pause();
        
             
        s_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				m_player.seekTo(new_pos);
				wait = false;
				seekUpdate();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				wait = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser)
				{
					new_pos = progress;
				}
			}
		});
        
        
        
        
        
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
            	
               Intent i = new Intent(getApplicationContext(), Song_List.class);
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
    		max_length = m_player.getDuration();
    		max_length /= 1000;
    		int mins = max_length / 60;
    		int secs = max_length % 60;
    		//Toast.makeText(getApplicationContext(),""+ max_lengths, Toast.LENGTH_SHORT).show();
    		song_length.setText(mins+":"+secs);
    		
    		seekb();
    		seekUpdate();
    		String titles = songsList.get(index).get("Title");
    		tv.setText(titles);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}    	
    }
    
    public void seekb()
    {
    	
    	s_bar.setMax(m_player.getDuration());
    }
    
    
    Runnable runner = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			seekUpdate();
		}
	};
	
	public void seekUpdate(){
		int c_pos = m_player.getCurrentPosition();
		if(!wait)
		{
			s_bar.setProgress(c_pos);
		}
		c_pos /= 1000;
		int mins = c_pos/60;
		int secs = c_pos%60;
		if(secs<10)
			time_elap.setText(mins+" : 0"+secs);
		else
			time_elap.setText(mins+" : "+secs);
		s_bar_handler.postDelayed(runner, 1000);
	}
}

package mdg.iitr.rhydm;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class Player extends Activity {

	private ImageButton b_play;
	private ImageButton b_next;
	private ImageButton b_prev;
	private TextView tv;
	private TextView song_length;
	private TextView time_elap;
	private Button s_list;
	private MediaPlayer m_player = new MediaPlayer();
	private SeekBar s_bar;
	
	private static boolean first_time = true;
	Handler s_bar_handler = new Handler();
	static int max_length;
	static int is = 0;
	static int max_songs = 0;
	static int new_pos = 0;
	boolean wait = false;
	static int wait_pos = 0;

	String paths;
	String[] projection = {	MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.DURATION
			};
	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
	String order = MediaStore.Audio.Media.TITLE;
	Cursor c;

	
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        b_play = (ImageButton) findViewById(R.id.btn_play);
        b_next = (ImageButton) findViewById(R.id.btn_next);
        b_prev = (ImageButton) findViewById(R.id.btn_prev);
        
        s_list = (Button) findViewById(R.id.song_list);

        
        tv = (TextView) findViewById(R.id.textView1);
        tv.setSelected(true);
        song_length = (TextView) findViewById(R.id.tv_duration);
        time_elap = (TextView) findViewById(R.id.tv_time_elapsed);
        
        s_bar = (SeekBar) findViewById(R.id.progress_bar);
        

        
        if(first_time)
        {
        	c = this.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,  order+" COLLATE NOCASE");
        	Globals.cursor = c;
        	first_time = false;
        }
        else
        	c = Globals.cursor;
        
        c.moveToFirst();
        paths = c.getString(3);
        listplay(c);
        m_player.pause();
        
        
        

        
        max_songs=c.getCount();
        
       // playSong(is);
        //m_player.pause();
        
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
				{
					c.moveToFirst();
					is=0;
				}
				else
				{	
					c.moveToNext();
					is++;
				}
				m_player.stop();
				listplay(c);
				//playSong(is);
				b_play.setImageResource(R.drawable.pause_btn);
				
			}
		});
        
        b_prev.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(is==0)
				{
					c.moveToLast();
					is = max_songs;
				}
				else
				{
					c.moveToPrevious();
					is--;
				}
					
				listplay(c);
				b_play.setImageResource(R.drawable.pause_btn);
			}
		});
        
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				c.moveToNext();
				m_player.pause();
				listplay(c);
			}
		});
        
    }

    
    

    
    public void listplay(Cursor cs)
    {
    	try {
			m_player.reset();
			m_player.setDataSource(cs.getString(3));
			m_player.prepare();
			m_player.start();
			max_length = m_player.getDuration();
    		max_length /= 1000;
    		int mins = max_length / 60;
    		int secs = max_length % 60;
    		song_length.setText(mins+":"+secs);
    		tv.setText(c.getString(1));
    		
    		seekb();
    		seekUpdate();

		} catch (Exception e) {
			// TODO Auto-generated catch block
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



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Globals.c_pos = c.getPosition();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		c.moveToPosition(Globals.c_pos);
		if(Globals.list_sel)
		{
			m_player.pause();
			listplay(c);
			Globals.list_sel = false;
		}
	}
	
}

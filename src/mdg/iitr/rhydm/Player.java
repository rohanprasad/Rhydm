package mdg.iitr.rhydm;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.text.TextUtils;

public class Player extends Activity {

	private ImageButton b_play;
	private ImageButton b_next;
	private ImageButton b_prev;
	private ImageButton s_list;
	private TextView tv;
	private TextView song_length;
	private TextView time_elap;
	private MediaPlayer m_player = new MediaPlayer();
	private SeekBar s_bar;
	private SharedPreferences rating_list;
	private SharedPreferences.Editor rating_list_editor;
	private RatingBar rating_bar;
	AudioManager my_audiomanager;
	
	private static boolean first_time = true;
	Handler s_bar_handler = new Handler();
	static int max_length;
	static int is;
	static int max_songs = 0;
	static int new_pos = 0;
	boolean wait = false;
	static int wait_pos = 0;
	static int perm;

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
        
        s_list = (ImageButton) findViewById(R.id.song_list);

        
        tv = (TextView) findViewById(R.id.textView1);
        tv.setSelected(true);
        song_length = (TextView) findViewById(R.id.tv_duration);
        time_elap = (TextView) findViewById(R.id.tv_time_elapsed);
        
        s_bar = (SeekBar) findViewById(R.id.progress_bar);
        
        rating_bar = (RatingBar) findViewById(R.id.ratingBar1);
        
        

        OnAudioFocusChangeListener afchange = new OnAudioFocusChangeListener() {
			
			@Override
			public void onAudioFocusChange(int focusChange) {
				// TODO Auto-generated method stub
			
				if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
				{
					m_player.stop();
					finish();
				}
				if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
				{
					m_player.pause();
				}
				if(focusChange == AudioManager.AUDIOFOCUS_GAIN)
				{
					m_player.start();
				}
			}
		};
        
		Context app_c = getApplicationContext();
		my_audiomanager = (AudioManager) app_c.getSystemService(Context.AUDIO_SERVICE);
        perm =  my_audiomanager.requestAudioFocus(afchange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);



        
     	c = this.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,  order+" COLLATE NOCASE");
      	Globals.cursor = c;
       	first_time = false;
        
        c.moveToFirst();
        is = c.getPosition();
        paths = c.getString(3);
        if(perm == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
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
					is=0;
				}
				else
				{	
					is++;
				}
				c.moveToPosition(is);
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
					is = max_songs;
				}
				else
				{
					is--;
				}
				c.moveToPosition(is);
				listplay(c);
				b_play.setImageResource(R.drawable.pause_btn);
			}
		});
        
        rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// TODO Auto-generated method stub
				if(fromUser)
				{
					String name = c.getString(0).toString();
					Context context = getApplicationContext();
			        rating_list = context.getSharedPreferences("rating_file", Context.MODE_PRIVATE);
			        rating_list_editor = rating_list.edit();
			        if(rating_list.contains(name))
			        {
			        	rating_list_editor.remove(name);
			        	rating_list_editor.putFloat(name, rating);
			        }
			        else{
			        	rating_list_editor.putFloat(name, rating);
			        }
			        rating_list_editor.commit();					
				}
			}
		});
        
        m_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				is++;
				c.moveToPosition(is);
				m_player.pause();
				listplay(c);
			}
		});
        
    }

    
    

    
    public void listplay(Cursor cs)
    {
    	if(perm == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
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
	    		if(secs<10)
	    			song_length.setText(mins+" : 0"+secs);
	    		else
	    			song_length.setText(mins+" : "+secs);
	    		tv.setText(c.getString(1));
	    		tv.setSelected(true);
	    		tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	    		Context context = getApplicationContext();
		        rating_list = context.getSharedPreferences("rating_file", Context.MODE_PRIVATE);
		        String song_id = c.getString(0);
	    		Float ratings = rating_list.getFloat(song_id, (float) 0);
	    		rating_bar.setRating(ratings);
	    		b_play.setImageResource(R.drawable.pause_btn);
	    		seekb();
	    		seekUpdate();
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		is = c.getPosition();
		if(Globals.list_sel)
		{
			m_player.pause();
			listplay(c);
			Globals.list_sel = false;
		}
	}





	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder exit_dialogs = new AlertDialog.Builder(Player.this);
		exit_dialogs.setTitle("Exit");
		exit_dialogs.setMessage("Are you sure you wanna exit Rhydm ?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							m_player.stop();
							Player.super.onBackPressed();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
				
		AlertDialog exit_dialog = exit_dialogs.create();
		exit_dialog.show();
		
	}
	
	
	
}

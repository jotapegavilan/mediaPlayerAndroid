package com.gavilan.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO;

public class MainActivity extends AppCompatActivity {
    private Button button;
    MediaPlayer mediaPlayer;
    boolean conectado;
    ProgressBar progressBar;
    IcyStreamMeta streamMeta;
    MetadataTask2 metadataTask2;
    String title_artist;
    String url = "http://stm3.miradio.com.es:12036/"; // your URL here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setEnabled(false);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        conectado = conectar();
        streamMeta = new IcyStreamMeta();

        try {
            streamMeta.setStreamUrl(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        metadataTask2 =new MetadataTask2();
        try {
            metadataTask2.execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        MyTimerTask task = new MyTimerTask();
        timer.schedule(task,100, 10000);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conectado) {
                    if (button.getText().equals("PLAY")) {
                        button.setText("PAUSA");
                        mediaPlayer.start();
                        MediaPlayer.TrackInfo info[] = mediaPlayer.getTrackInfo();

                    } else {
                        mediaPlayer.pause();
                        button.setText("PLAY");
                    }

                }else{
                    conectado = conectar();
                }
            }
        });



    }

    public boolean conectar(){
        Toast.makeText(getApplicationContext(),"Intentado conectar",Toast.LENGTH_LONG).show();



        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Toast.makeText(getApplicationContext(),"Conectado",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    button.setEnabled(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected class MetadataTask2 extends AsyncTask<URL, Void, IcyStreamMeta>
    {
        @Override
        protected IcyStreamMeta doInBackground(URL... urls)
        {
            try
            {
                streamMeta.refreshMeta();
                Log.e("Retrieving MetaData","Refreshed Metadata");
            }
            catch (IOException e)
            {
                Log.e(MetadataTask2.class.toString(), e.getMessage());
            }
            return streamMeta;
        }

        @Override
        protected void onPostExecute(IcyStreamMeta result)
        {
            try
            {
                title_artist=streamMeta.getStreamTitle();
                Log.e("Retrieved title_artist", title_artist);
                if(title_artist.length()>0)
                {
                    Toast.makeText(MainActivity.this,title_artist,Toast.LENGTH_LONG).show();
                }
            }
            catch (IOException e)
            {
                Log.e(MetadataTask2.class.toString(), e.getMessage());
            }
        }
    }

    class MyTimerTask extends TimerTask {
        public void run() {
            try {
                streamMeta.refreshMeta();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String title_artist=streamMeta.getStreamTitle();
                Log.i("ARTIST TITLE", title_artist);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
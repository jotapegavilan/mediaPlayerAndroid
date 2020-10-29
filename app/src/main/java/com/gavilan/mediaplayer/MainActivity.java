package com.gavilan.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button button;
    MediaPlayer mediaPlayer;
    boolean conectado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setEnabled(false);

        conectado = conectar();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conectado) {
                    if (button.getText().equals("PLAY")) {
                        button.setText("PAUSA");
                        mediaPlayer.start();
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
        String url = "http://stm3.miradio.com.es:12036/"; // your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();// might take long! (for buffering, etc)
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Toast.makeText(getApplicationContext(),"Conectado",Toast.LENGTH_LONG).show();
                    button.setEnabled(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
package com.example.musicplayer;

import android.Manifest;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    RecyclerView listView;
    TextView txtName , txtAlbum ;
    ImageView iv_prev , iv_pause , iv_next;
    MusicAdapter musicAdapter;
    static MediaPlayer mp;
    int currentPosition =-1;
    ArrayList<File> mySongs;
    SessionManagement sessionManagement;
    String lastsong;
    Uri urilast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        txtAlbum = findViewById(R.id.txtAlbumName);
        txtName = findViewById(R.id.txtSongName);
        iv_next = findViewById(R.id.next);
        iv_pause = findViewById(R.id.play);
        iv_prev = findViewById(R.id.previous);
        sessionManagement = new SessionManagement(this);
        lastsong = sessionManagement.getLastPlay();
        listView.setLayoutManager(new LinearLayoutManager(this));
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp!=null)
                {
                    if(mp.isPlaying()){
                        iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                        mp.pause();

                    }
                    else {
                        iv_pause.setBackgroundResource(R.drawable.pause);
                        mp.start();

                    }
                }
                else
                {
                    playSong(currentPosition,"aa",mySongs);
                }


            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.release();
                currentPosition=((currentPosition+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get( currentPosition).toString());
                // songNameText.setText(getSongName);
                mp = MediaPlayer.create(getApplicationContext(),u);
                updateData(mySongs.get(currentPosition));

                try{
                    mp.start();
                    iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }catch(Exception e){}

            }
        });
        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songNameText.setText(getSongName);
                mp.stop();
                mp.release();
                currentPosition=((currentPosition-1)<0)?(mySongs.size()-1):(currentPosition-1);
                Uri u = Uri.parse(mySongs.get(currentPosition).toString());
                mp = MediaPlayer.create(getApplicationContext(),u);
                updateData(mySongs.get(currentPosition));
                mp.start();
                iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            }
        });

    }

    public ArrayList<File> findSong(File root){
        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        for(File singleFile : files){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                at.addAll(findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") ||
                        singleFile.getName().endsWith(".wav")){
                    at.add(singleFile);
                }
            }
        }
        return at;
    }
    void display(){
        mySongs = findSong(Environment.getExternalStorageDirectory());
        if (lastsong!=null)
        {
            urilast = Uri.parse(lastsong);
            File file = new File(urilast.getPath());
            updateData(file);
            for (int i=0;i<mySongs.size();i++
            ) {
                if (mySongs.get(i).equals(file))
                {
                    currentPosition=i;
                }
            }
        }
        else
        {
            updateData(mySongs.get(0));
            currentPosition=0;
        }
        musicAdapter = new MusicAdapter(this,mySongs);
        listView.setAdapter(musicAdapter);
        listView.addOnItemTouchListener(new RecyclerTouchListener(this,
                listView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                String songName = mySongs.get(position).getName().toString();
                playSong(position,songName,mySongs);
                currentPosition = position;
                sessionManagement.addLastPlay(Uri.parse(mySongs.get(currentPosition).toString()));
                updateData(mySongs.get(currentPosition));
            }

            @Override
            public void onLongClick(View view, int position) {
                return;
            }
        }));
    }

    public void playSong(int position,String songname,ArrayList<File> songList)
    {
        if(mp != null){
            mp.stop();
            mp.release();
        }
        Uri u = Uri.parse(songList.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        iv_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));

    }

    private void updateData(File file)
    {
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        String fname=file.getName();
        String fpath=file.getAbsolutePath();
        if (file.isFile() && fname.contains("."))
        {
            String ext=fname.substring(fname.lastIndexOf("."));
            if (ext.equals(".mp3") || ext.equals(".MP3")||ext.equalsIgnoreCase(".wav"))
            {
                mmr.setDataSource(fpath);
                String title=mmr.extractMetadata(7);
                String artist=mmr.extractMetadata(2);
                String genre=mmr.extractMetadata(6);
                String album=mmr.extractMetadata(1);

                txtName.setText(file.getName().toString().replace(".mp3","").replace(".wav",""));
                if (album!=null)
                {
                   txtAlbum.setText(album);
                }
                else if(artist!=null)
                {
                    txtAlbum.setText(artist);
                }
                else if(genre!=null)
                {
                    txtAlbum.setText(genre);
                }
                else
                {
                    txtAlbum.setText("Unknown Album");
                }
            }
        }
    }

}

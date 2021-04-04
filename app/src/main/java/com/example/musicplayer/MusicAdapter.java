package com.example.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    Context context;
    ArrayList<File> songlist;

    public MusicAdapter(Context context, ArrayList<File> list) {
        this.context = context;
        this.songlist = list;

    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_songs,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, int position) {
        File file = songlist.get(position);
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();
        String fname=file.getName();
        String fpath=file.getAbsolutePath();
        if (file.isFile() && fname.contains("."))
        {
            String ext=fname.substring(fname.lastIndexOf("."));
            if (ext.equals(".mp3") || ext.equals(".MP3"))
            {
                mmr.setDataSource(fpath);
                String title=mmr.extractMetadata(7);
                String artist=mmr.extractMetadata(2);
                String genre=mmr.extractMetadata(6);
                String album=mmr.extractMetadata(1);

                holder.tv_name.setText(file.getName().toString().replace(".mp3","").replace(".wav",""));
                if (album!=null)
                {
                    holder.tv_album.setText(album);
                }
                else if(artist!=null)
                {
                    holder.tv_album.setText(artist);
                }
                else if(genre!=null)
                {
                    holder.tv_album.setText(genre);
                }
                else
                {
                    holder.tv_album.setText("Unknown Album");
                }
                Log.e("DATA",title+" "+album+" "+artist+" "+genre);
            }
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return songlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView more;
        TextView tv_name , tv_album;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            more = itemView.findViewById(R.id.more);
            tv_name = itemView.findViewById(R.id.tv_song_name);
            tv_album = itemView.findViewById(R.id.tv_album_name);
        }
    }
}

package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SessionManagement {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManagement(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("MusicYAsh", context.MODE_PRIVATE);
        editor = prefs.edit();

    }

    public void addtoLiked(Uri uri)
    {
        Set<String> set = prefs.getStringSet("liked_music", null);
        if (set==null)
        {
            set = new HashSet<String>();
        }
        set.add(uri.toString());
        editor.putStringSet("liked_music",set);
        editor.commit();
    }

    public ArrayList<String> getLiked()
    {
        Set<String> set = prefs.getStringSet("liked_music", null);
        ArrayList<String> likedlist = new ArrayList<>(set);
        return likedlist;
    }

    public void addLastPlay(Uri uri)
    {
        editor.putString("last_song",uri.toString());
        editor.commit();
    }

    public String getLastPlay()
    {
        String last = prefs.getString("last_song",null);
        return last;
    }
}

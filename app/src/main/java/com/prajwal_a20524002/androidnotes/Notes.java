package com.prajwal_a20524002.androidnotes;

import android.util.JsonWriter;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.StringWriter;

public class Notes {

    private static final String TAG = "Notes";
    private String noteTitle;
    private String noteContent;
    private long noteLastUpdatedAt;

    Notes() {
        this.noteTitle = "";
        this.noteContent = "";
        this.noteLastUpdatedAt = System.currentTimeMillis();
    }

    Notes(String title, String content){
        this.noteTitle = title;
        this.noteContent = content;
        this.noteLastUpdatedAt = System.currentTimeMillis();
    }

    Notes(String title, String content, Long lastUpdatedAt){
        this.noteTitle = title;
        this.noteContent = content;
        this.noteLastUpdatedAt = lastUpdatedAt;
    }

    public String getNoteTitle() {
        Log.d(TAG, "getNoteTitle: ");
        return noteTitle;
    }

    public String getNoteContent() {
        Log.d(TAG, "getNoteContent: ");
        return noteContent;
    }

    public long getNoteLastUpdatedAt() {
        Log.d(TAG, "getNoteLastUpdatedAt: ");
        return noteLastUpdatedAt;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteLastUpdatedAt(Long timestamp){
        this.noteLastUpdatedAt = timestamp;
    }

    @NonNull
    @Override
    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();

            jsonWriter.name("title").value(getNoteTitle());
            jsonWriter.name("content").value(getNoteContent());
            jsonWriter.name("lastUpdatedAt").value(getNoteLastUpdatedAt());

            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
}
}

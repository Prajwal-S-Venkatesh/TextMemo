package com.prajwal_a20524002.androidnotes;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NotesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "NotesViewHolder";
    TextView notesTitle;
    TextView notesContent;
    TextView notesLastUpdatedAt;

    NotesViewHolder(View view){
        super(view);
        Log.d(TAG, "NotesViewHolder: ");

        notesTitle = view.findViewById(R.id.note_title);
        notesContent = view.findViewById(R.id.note_content);
        notesLastUpdatedAt = view.findViewById(R.id.note_lastUpdatedAt);
    }
}

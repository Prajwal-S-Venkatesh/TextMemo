package com.prajwal_a20524002.androidnotes;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    private static final String TAG = "NotesAdapter";
    private final List<Notes> notesList;
    private final MainActivity mainActivity;

    public NotesAdapter(List<Notes> notesList, MainActivity mainActivity) {
        this.notesList = notesList;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_entry, parent, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new NotesViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        Notes note = notesList.get(position);
        holder.notesTitle.setText(getFormattedString(note.getNoteTitle()));
        holder.notesContent.setText(getFormattedString(note.getNoteContent()));
        holder.notesLastUpdatedAt.setText(getFormattedTime(note.getNoteLastUpdatedAt()));
    }

    public static String getFormattedTime(long currentTime) {
        SimpleDateFormat timeZoneDate = new SimpleDateFormat("EEE MMM dd,  HH:mm aa", Locale.getDefault());
        return timeZoneDate.format(currentTime);
    }

    public String getFormattedString(String s){
        if(s.length() > 80) {
            return s.substring(0,80) + "...";
        }
        return s;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return notesList.size();
    }
}

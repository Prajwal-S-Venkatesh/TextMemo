package com.prajwal_a20524002.androidnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private final List<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private LinearLayoutManager linearLayoutManager;

    TextView noFilesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1f1f1f"));
        if(actionBar != null){
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResultFromCreateOrEditActivity);

        noFilesText = findViewById(R.id.noNotes);
        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadJSONContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.title_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        if(item.getItemId() == R.id.about_menu_item){
            Intent intent = new Intent(this, AboutNotes.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.add_note_menu_item){
            Intent intent = new Intent(MainActivity.this, CreateOrEditNotes.class);
            activityResultLauncher.launch(intent);
        }else{
            Toast.makeText(this, "Invalid Menu Item!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onOptionsItemSelected: Invalid Menu Item Reference!");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int pos = recyclerView.getChildLayoutPosition(v);
        Notes note = notesList.get(pos);

        Intent intent = new Intent(this, CreateOrEditNotes.class);
        intent.putExtra("ID", "" + pos);
        intent.putExtra("TITLE", note.getNoteTitle());
        intent.putExtra("CONTENT", note.getNoteContent());
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick: ");
        int pos = recyclerView.getChildLayoutPosition(v);
        Notes note = notesList.get(pos);
        String titleData = note.getNoteTitle();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", (dialog, id) -> {
            notesList.remove(pos);
            notesAdapter.notifyItemRemoved(pos);
            linearLayoutManager.scrollToPosition(pos);
            saveNotes(notesList);

            if(notesList.size() > 0){
                setTitle("Android Notes ("+ notesList.size() +")");
            }else{
                setTitle("Android Notes");
                noFilesText.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("NO", (dialog, id) -> {});

        builder.setTitle("Delete note '" + titleData + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        Toast.makeText(this, "Bye!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void handleResultFromCreateOrEditActivity(ActivityResult result) {
        Log.d(TAG, "handleResult: ");
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            String title = data.getStringExtra("TITLE");
            String content = data.getStringExtra("CONTENT");
            String ids = data.getStringExtra("ID");
            int id = ids == null  ? -1 : Integer.parseInt(data.getStringExtra("ID"));

            if (title == null) {
                Toast.makeText(this, "Title is Required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(id > -1){
                Notes note = notesList.get(id);
                note.setNoteTitle(title);
                note.setNoteContent(content);
                note.setNoteLastUpdatedAt(System.currentTimeMillis());

                notesAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPosition(0);
                sortNotesList();
            }else{
                notesList.add(0, new Notes(title, content, System.currentTimeMillis()));
                notesAdapter.notifyItemInserted(0);
                linearLayoutManager.scrollToPosition(0);
            }
            saveNotes(notesList);

            if(notesList.size() > 0){
                setTitle("Android Notes ("+ notesList.size()+")");
                noFilesText.setVisibility(View.INVISIBLE);
            }else{
                noFilesText.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "onActivityResult: User Text: " + title + " " + content);
        } else {
            Log.d(TAG, "onActivityResult: result Code: " + result.getResultCode());
        }
    }

    public void loadJSONContent()
    {
        try
        {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            if(jsonArray.length() == 0){
                return;
            }
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                long lastUpdatedAt = jsonObject.getLong("lastUpdatedAt");

                Notes newNote = new Notes(title, content, lastUpdatedAt);
                notesList.add(newNote);
                if(notesList.size() > 0){
                    setTitle("Android Notes ("+ notesList.size()+")");
                }
                notesAdapter.notifyItemInserted(notesList.size());
            }
            sortNotesList();
            noFilesText.setVisibility(View.INVISIBLE);
        }
        catch (FileNotFoundException e)
        {
//            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            notesList.clear();
            noFilesText.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            noFilesText.setVisibility(View.VISIBLE);
            notesList.clear();
        }
    }

    public void sortNotesList(){
        notesList.sort((a, b) -> (int) (b.getNoteLastUpdatedAt() - a.getNoteLastUpdatedAt()));
    }

    public void saveNotes(List<Notes> notes)
    {
        try
        {
            FileOutputStream fOutputStream = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fOutputStream);
            String str = notes.toString();
            Log.d(TAG, "saveNotes: " + str);
            printWriter.print(str);
            printWriter.close();
            fOutputStream.close();

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }
}
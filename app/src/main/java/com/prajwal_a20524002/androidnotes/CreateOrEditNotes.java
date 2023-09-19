package com.prajwal_a20524002.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class CreateOrEditNotes extends AppCompatActivity {

    private static final String TAG = "CreateOrEditNotes";
    EditText title;
    EditText content;

    int id = -1;
    String prevTitle = "";
    String prevContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit_notes);

        ActionBar actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1f1f1f"));
        if(actionBar != null){
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        title = findViewById(R.id.editTextTitle);
        content = findViewById(R.id.editTextContent);

        content.setMovementMethod(new ScrollingMovementMethod());
        content.setTextIsSelectable(true);

        if (getIntent().hasExtra("TITLE")) {
            String name = getIntent().getStringExtra("TITLE");
            title.setText(name);
            prevTitle = name;
        }

        if (getIntent().hasExtra("CONTENT")) {
            String name = getIntent().getStringExtra("CONTENT");
            content.setText(name);
            prevContent = name;
        }

        if (getIntent().hasExtra("ID") && getIntent().getStringExtra("ID") != null) {

            id = Integer.parseInt(getIntent().getStringExtra("ID"));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_layout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if(item.getItemId() == R.id.save_menu_item){
            Log.d(TAG, "onOptionsItemSelected: SaveMenuItem Clicked");
            if (isDirty()) {
                String titleData = title.getText().toString();
                String contentData = content.getText().toString();

                if (titleData.isEmpty()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setPositiveButton("YES", (dialog, id) -> finish());
                    builder.setNegativeButton("NO", (dialog, id) -> {
                    });

                    builder.setMessage("The note won't be saved if no tile is provided, Do you wish to proceed?");
                    builder.setTitle("Confirm");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return super.onOptionsItemSelected(item);
                } else {
                    Intent data = new Intent();
                    data.putExtra("TITLE", titleData);
                    data.putExtra("CONTENT", contentData);
                    if (id > -1) {
                        data.putExtra("ID", "" + id);
                    }
                    Log.d(TAG, "onOptionsItemSelected: " + titleData + " " + contentData);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
            finish();
        }else{
            Toast.makeText(this, "Invalid Menu Item!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDirty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String titleData = title.getText().toString();
            String contentData = content.getText().toString();

            builder.setPositiveButton("YES", (dialog, idx) -> {
                Intent data = new Intent();
                data.putExtra("TITLE", titleData);
                data.putExtra("CONTENT", contentData);
                if (id > -1) {
                    data.putExtra("ID", "" + id);
                }
                setResult(RESULT_OK, data);
                super.onBackPressed();
            });
            builder.setNegativeButton("NO", (dialog, id) -> {
                super.onBackPressed();
            });

            builder.setMessage("Save note '" + titleData + "'?");
            builder.setTitle("Your note is not saved!");
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            super.onBackPressed();
        }
    }


    public boolean isDirty(){
        return title.getText().toString().compareTo(prevTitle) != 0 || content.getText().toString().compareTo(prevContent) != 0;
    }
}
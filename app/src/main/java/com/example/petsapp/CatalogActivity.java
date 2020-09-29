package com.example.petsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.petsapp.data.PetContract;
import com.example.petsapp.data.PetDbHelper;
import com.example.petsapp.data.PetContract.PetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {

    public static final String LOG_TAG = CatalogActivity.class.getName();

    private PetDbHelper mDbHelper;

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
            + PetEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
            + PetEntry.COLUMN_PET_BREED + " TEXT, "
            + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
            + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new PetDbHelper(this);
         displayDatabaseInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @SuppressLint("SetTextI18n")
    public void displayDatabaseInfo() {

        String[] projection = {
                PetEntry.COLUMN_ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT,
        };

        Cursor cursor = getContentResolver().query(
                PetContract.CONTENT_URI,
                projection,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_pet);
        try {
            assert cursor != null;
            displayView.setText("The Pet table contains " + cursor.getCount() + " pets.\n");

            displayView.append("\n"
                    + PetEntry.COLUMN_ID + " - "
                    + PetEntry.COLUMN_PET_NAME + " - "
                    + PetEntry.COLUMN_PET_BREED + " - "
                    + PetEntry.COLUMN_PET_GENDER + " - "
                    + PetEntry.COLUMN_PET_WEIGHT + "\n"
            );

            int idColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentPetName = cursor.getString(nameColumnIndex);
                String currentPetBreed = cursor.getString(breedColumnIndex);
                int currentPetGender = cursor.getInt(genderColumnIndex);
                int currentPetWeight = cursor.getInt(weightColumnIndex);

                displayView.append(("\n"
                        + currentId + " - "
                        + currentPetName + " - "
                        + currentPetBreed + " - "
                        + currentPetGender + " - "
                        + currentPetWeight
                        ));
            }

        } finally {
            assert cursor != null;
            cursor.close();
        }
    }

    private void insertPet() {
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri newUri = getContentResolver().insert(PetContract.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;

            case R.id.action_delete_all_entries:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
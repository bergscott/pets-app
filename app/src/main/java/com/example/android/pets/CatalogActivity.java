package com.example.android.pets;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);

        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        // Perform this SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.

        // Setup the projection for the query
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };

        // use the content resolver to query the database for all pets
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection,
                null, null, null);

        // Get the list view from the xml layout
        ListView listView = (ListView) findViewById(R.id.list_view_pet);

        // Create a new PetCursorAdapter using the cursor retrieved above
        PetCursorAdapter mPetCursorAdapter = new PetCursorAdapter(this, cursor);

        // Set the adapter to the list view
        listView.setAdapter(mPetCursorAdapter);

    }

    /**
     * Insert a dummy pet, "Toto" into the db
     */
    private void insertPet() {

        // create a new map of values for the "dummy" pet
        ContentValues petValues = new ContentValues();
        petValues.put(PetEntry.COLUMN_PET_NAME, "Toto");
        petValues.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        petValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        petValues.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // insert the new row using the content resolver
        getContentResolver().insert(PetEntry.CONTENT_URI, petValues);

        // show a Toast message to confirm for the user that a pet was created
        Toast.makeText(this, getString(R.string.insert_pet_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
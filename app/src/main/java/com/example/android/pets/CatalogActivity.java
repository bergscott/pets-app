package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /** List View containing pets in the database */
    ListView mPetListView;

    /** Cursor Adapter for Pet Information from Database */
    PetCursorAdapter mPetCursorAdapter;

    /** Pet Cursor Loader ID */
    private static final int PET_LOADER = 0;

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

        // find the list view and set it's empty state view
        mPetListView = (ListView) findViewById(R.id.list_view_pet);
        View emptyView = findViewById(R.id.empty_view);
        mPetListView.setEmptyView(emptyView);

        // setup the cursor adapter and set it to the list view
        mPetCursorAdapter = new PetCursorAdapter(this, null);
        mPetListView.setAdapter(mPetCursorAdapter);

        // set the onItemClickListener for the list view to launch the editor activity to edit the
        // clicked item
        mPetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(PetEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        // initialize the cursor loader for pet data
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */

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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // get the URI for the pets table in the database
        Uri uri = PetEntry.CONTENT_URI;

        // setup the projection retrieving only the ID, Name, and Breed of the pets
        String[] projection = { PetEntry._ID, PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED };

        // return the new cursor loader
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // swap the adapter's cursor to the newly loaded cursor
        mPetCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // adapter's data needs to be cleared, so swap in null and drop all references to the
        // previous cursor to prevent memory leaks
        mPetCursorAdapter.swapCursor(null);
    }
}
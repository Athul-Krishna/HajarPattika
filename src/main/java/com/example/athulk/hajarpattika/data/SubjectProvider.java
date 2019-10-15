package com.example.athulk.hajarpattika.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class SubjectProvider extends ContentProvider {

    private static final int SUBJECTS = 100;
    private static final int SUBJECT_ID = 101;
    public static final String LOG_TAG = SubjectProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(SubjectContract.CONTENT_AUTHORITY, SubjectContract.PATH_SUBJECT, SUBJECTS);
        sUriMatcher.addURI(SubjectContract.CONTENT_AUTHORITY, SubjectContract.PATH_SUBJECT+"/#", SUBJECT_ID);
    }
    private SubjectDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SubjectDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case SUBJECTS:
                cursor =  database.query(SubjectContract.SubjectEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SUBJECT_ID:
                selection = SubjectContract.SubjectEntry._ID+"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor =  database.query(SubjectContract.SubjectEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Error");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBJECTS:
                return SubjectContract.SubjectEntry.CONTENT_LIST_TYPE;
            case SUBJECT_ID:
                return SubjectContract.SubjectEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBJECTS:
                return insertSubject(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertSubject(Uri uri, ContentValues values){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(SubjectContract.SubjectEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBJECTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(SubjectContract.SubjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBJECT_ID:
                // Delete a single row given by the ID in the URI
                selection = SubjectContract.SubjectEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(SubjectContract.SubjectEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SUBJECTS:
                return updateSubject(uri, values, selection, selectionArgs);
            case SUBJECT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = SubjectContract.SubjectEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateSubject(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSubject(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(SubjectContract.SubjectEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

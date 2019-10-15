package com.example.athulk.hajarpattika.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class SubjectContract {

    private SubjectContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.athulk.hajarpattika";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_SUBJECT = "subjects";

    public static final class SubjectEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUBJECT;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SUBJECT);

        public final static String TABLE_NAME = "subjects";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ATTEND = "attended";
        public static final String COLUMN_BUNK = "bunked";
        public static final String COLUMN_UPDATE = "updated";
    }
}

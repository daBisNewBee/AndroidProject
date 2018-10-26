package com.exa.contProd;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *
 * 通过ContentProvider多进程共享SharedPreferences数据:
 * s
 * https://www.jianshu.com/p/bdebf741221e
 *
 * Created by user on 2018/8/13.
 */

public class MyContentProvider extends ContentProvider {

    private static final String TGT_ID = "11111111222222222233333333333";

    private static final String TICKET_SER_URL = "http://10.0.90.188:80";

    public static final String AUTHORITY = "com.exa.MyContentProvider";

    public static final String TABLE_SSO = "sso";
    public static final String TABLE_TICKET = "ticket";
    public static final String TABLE_ALL = "all";

    private static final Map<Long, String> DUMP_TICKET = new HashMap<>();
    static {
        DUMP_TICKET.put(1L,"tick-aa");
        DUMP_TICKET.put(2L,"tick-bb");
        DUMP_TICKET.put(3L,"tick-cc");
    }

    private static final String SEPARATOR = "/";

    public static final Uri URI_MYPROVIDER = Uri.parse(
            "content://" + AUTHORITY + SEPARATOR);

    private static final int CODE_SSO = 1;
    private static final int CODE_TICKET = 2;
    private static final int CODE_TICKETES = 3;
    private static final int CODE_ALL = 4;

    public static final String CURSOR_COLUMN_NAME = "cursor_name";
    public static final String CURSOR_COLUMN_VALUE = "cursor_value";

    private static UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_SSO, CODE_SSO);
        MATCHER.addURI(AUTHORITY, TABLE_TICKET, CODE_TICKET);
        // #表示任意数字, *表示任意字符
        MATCHER.addURI(AUTHORITY, TABLE_TICKET + "/#" , CODE_TICKETES);
        MATCHER.addURI(AUTHORITY, TABLE_ALL, CODE_ALL);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        System.out.println("\nquery -----> "+uri);

        MatrixCursor cursor = new MatrixCursor(new String[]{CURSOR_COLUMN_NAME,CURSOR_COLUMN_VALUE});
        Set<Map.Entry<Long,String>> entrySet;

        final int code = MATCHER.match(uri);
        switch (code){
            case CODE_SSO:
                cursor.addRow(new Object[]{TABLE_SSO, TGT_ID});
                break;
            case CODE_TICKET:
                entrySet = DUMP_TICKET.entrySet();
                for (Map.Entry<Long, String> entry : entrySet) {
                    cursor.addRow(new Object[]{entry.getKey(),entry.getValue()});
                }
                break;
            case CODE_TICKETES:
                long id = ContentUris.parseId(uri);
                if (DUMP_TICKET.containsKey(id)){
                        cursor.addRow(new Object[]{id, DUMP_TICKET.get(id)});
                    }else {
                        cursor.addRow(new Object[]{id,"this ticket dynamic generated:"+id});
                    }
                break;
            case CODE_ALL:
                cursor.addRow(new Object[]{TABLE_SSO, TGT_ID});
                entrySet = DUMP_TICKET.entrySet();
                for (Map.Entry<Long, String> entry : entrySet) {
                    cursor.addRow(new Object[]{entry.getKey(),entry.getValue()});
                }
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        System.out.println("getType ---------> "+uri);
        String[] elems = uri.getPath().split(SEPARATOR);
        for (int i = 0; i < elems.length; i++) {
            System.out.println(i+" = " + elems[i]);
        }
        if (elems[1].equals(TABLE_SSO))
            return TGT_ID;

        if (elems[1].equals(TABLE_TICKET))
            return TICKET_SER_URL;

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        System.out.println("\ninsert -----> "+uri);
        getContext().getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

package com.linhleeproject.mymessage.messengeros10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.linhleeproject.mymessage.messengeros10.models.ContactObject;
import com.linhleeproject.mymessage.messengeros10.models.MessageObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/29/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MessageDB";

    private static final String TABLE_MESSAGE = "message";
    private static final String KEY_MESSAGE_ID = "message_id";
    private static final String KEY_THREAD_ID = "thread_id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PERSON = "person";
    private static final String KEY_DATE = "date";
    private static final String KEY_BODY = "body";
    private static final String KEY_READ = "read";
    private static final String KEY_TYPE = "type";
    private static final String KEY_THUMBNAIL = "thumbnail";

    private static final String TABLE_CONTACT = "contact";
    private static final String KEY_CONTACT_NAME = "contact_name";
    private static final String KEY_CONTACT_NUMBER = "contact_number";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE + " ("
                + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY, "
                + KEY_THREAD_ID + " INTEGER, "
                + KEY_ADDRESS + " TEXT, "
                + KEY_PERSON + " TEXT, "
                + KEY_DATE + " BIGINT, "
                + KEY_BODY + " TEXT, "
                + KEY_READ + " INTEGER, "
                + KEY_TYPE + " INTEGER, "
                + KEY_THUMBNAIL + " TEXT)";

        String CREATE_CONTACT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT + " ("
                + KEY_CONTACT_NAME + " TEXT, "
                + KEY_CONTACT_NUMBER + " TEXT)";

        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);

        this.onCreate(db);
    }

    public void addMessage(MessageObject message) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_ID, message.getMessageId());
        values.put(KEY_THREAD_ID, message.getThreadId());
        values.put(KEY_ADDRESS, message.getAddress());
        values.put(KEY_PERSON, message.getPerson());
        values.put(KEY_DATE, message.getDate());
        values.put(KEY_BODY, message.getBody());
        values.put(KEY_READ, message.getRead());
        values.put(KEY_TYPE, message.getType());
        values.put(KEY_THUMBNAIL, message.getThumbnailBase64());

        db.insert(TABLE_MESSAGE, null, values);

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void addAllMessage(ArrayList<MessageObject> listMessage) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        for (MessageObject message : listMessage) {
            ContentValues values = new ContentValues();
            values.put(KEY_MESSAGE_ID, message.getMessageId());
            values.put(KEY_THREAD_ID, message.getThreadId());
            values.put(KEY_ADDRESS, message.getAddress());
            values.put(KEY_PERSON, message.getPerson());
            values.put(KEY_DATE, message.getDate());
            values.put(KEY_BODY, message.getBody());
            values.put(KEY_READ, message.getRead());
            values.put(KEY_TYPE, message.getType());
            values.put(KEY_THUMBNAIL, message.getThumbnailBase64());

            db.insert(TABLE_MESSAGE, null, values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void addAllContact(ArrayList<ContactObject> listContact) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        for (ContactObject contact : listContact) {
            ContentValues values = new ContentValues();
            values.put(KEY_CONTACT_NAME, contact.getName());
            values.put(KEY_CONTACT_NUMBER, contact.getPhoneNumber());

            db.insert(TABLE_CONTACT, null, values);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public ArrayList<MessageObject> getAllMessage() {
        ArrayList<MessageObject> listMessage = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MESSAGE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageObject message = new MessageObject();
                message.setMessageId(cursor.getInt(0));
                message.setThreadId(cursor.getInt(1));
                message.setAddress(cursor.getString(2));
                message.setPerson(cursor.getString(3));
                message.setDate(cursor.getLong(4));
                message.setBody(cursor.getString(5));
                message.setRead(cursor.getInt(6));
                message.setType(cursor.getInt(7));
                message.setThumbnailBase64(cursor.getString(8));

                listMessage.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listMessage;
    }

    public ArrayList<MessageObject> getMessageByThreadId(int threadId) {
        ArrayList<MessageObject> listMessage = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MESSAGE
                + " WHERE " + KEY_THREAD_ID + " = " + threadId
                + " ORDER BY " + KEY_DATE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageObject message = new MessageObject();
                message.setMessageId(cursor.getInt(0));
                message.setThreadId(cursor.getInt(1));
                message.setAddress(cursor.getString(2));
                message.setPerson(cursor.getString(3));
                message.setDate(cursor.getLong(4));
                message.setBody(cursor.getString(5));
                message.setRead(cursor.getInt(6));
                message.setType(cursor.getInt(7));
                message.setThumbnailBase64(cursor.getString(8));

                listMessage.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listMessage;
    }

    public MessageObject getFirstMessageByThreadId(int threadId) {
        ArrayList<MessageObject> listMessage = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MESSAGE
                + " WHERE " + KEY_THREAD_ID + " = " + threadId
                + " ORDER BY " + KEY_DATE + " DESC"
                + " LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageObject message = new MessageObject();
                message.setMessageId(cursor.getInt(0));
                message.setThreadId(cursor.getInt(1));
                message.setAddress(cursor.getString(2));
                message.setPerson(cursor.getString(3));
                message.setDate(cursor.getLong(4));
                message.setBody(cursor.getString(5));
                message.setRead(cursor.getInt(6));
                message.setType(cursor.getInt(7));
                message.setThumbnailBase64(cursor.getString(8));

                listMessage.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listMessage.get(0);
    }

    public ArrayList<MessageObject> getMessageGroupByThread() {
        ArrayList<MessageObject> listMessage = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_MESSAGE
                + " GROUP BY " + KEY_THREAD_ID
                + " ORDER BY " + KEY_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageObject message = new MessageObject();
                message.setMessageId(cursor.getInt(0));
                message.setThreadId(cursor.getInt(1));
                message.setAddress(cursor.getString(2));
                message.setPerson(cursor.getString(3));
                message.setDate(cursor.getLong(4));
                message.setBody(cursor.getString(5));
                message.setRead(cursor.getInt(6));
                message.setType(cursor.getInt(7));
                message.setThumbnailBase64(cursor.getString(8));

                listMessage.add(message);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listMessage;
    }

    public ArrayList<ContactObject> getAllContact() {
        ArrayList<ContactObject> listContact = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_CONTACT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ContactObject contact = new ContactObject();
                contact.setName(cursor.getString(0));
                contact.setPhoneNumber(cursor.getString(1));

                listContact.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listContact;
    }

    public void deleteAllMessage() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MESSAGE, null, null);
    }

    public void deleteAllContact() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CONTACT, null, null);
    }

    public void deleteAllMessageByThreadId(int threadId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MESSAGE, KEY_THREAD_ID + " = ?",
                new String[]{String.valueOf(threadId)});
    }

    public void deleteMessageById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MESSAGE, KEY_MESSAGE_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int updateMessage(MessageObject message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGE_ID, message.getMessageId());
        values.put(KEY_THREAD_ID, message.getThreadId());
        values.put(KEY_ADDRESS, message.getAddress());
        values.put(KEY_PERSON, message.getPerson());
        values.put(KEY_DATE, message.getDate());
        values.put(KEY_BODY, message.getBody());
        values.put(KEY_READ, message.getRead());
        values.put(KEY_TYPE, message.getType());
        values.put(KEY_THUMBNAIL, message.getThumbnailBase64());

        int i = db.update(TABLE_MESSAGE, values, KEY_MESSAGE_ID + " = ?",
                new String[]{String.valueOf(message.getMessageId())});

        return i;
    }

    public int updateListMessage(ArrayList<MessageObject> listMessage) {
        SQLiteDatabase db = this.getWritableDatabase();

        int i = 0;

        db.beginTransaction();

        for (MessageObject message: listMessage) {
            ContentValues values = new ContentValues();
            values.put(KEY_MESSAGE_ID, message.getMessageId());
            values.put(KEY_THREAD_ID, message.getThreadId());
            values.put(KEY_ADDRESS, message.getAddress());
            values.put(KEY_PERSON, message.getPerson());
            values.put(KEY_DATE, message.getDate());
            values.put(KEY_BODY, message.getBody());
            values.put(KEY_READ, message.getRead());
            values.put(KEY_TYPE, message.getType());
            values.put(KEY_THUMBNAIL, message.getThumbnailBase64());

            i = db.update(TABLE_MESSAGE, values, KEY_MESSAGE_ID + " = ?",
                    new String[]{String.valueOf(message.getMessageId())});
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return i;
    }
}

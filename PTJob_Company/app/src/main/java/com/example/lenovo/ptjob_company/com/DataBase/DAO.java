package com.example.lenovo.ptjob_company.com.DataBase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.lenovo.ptjob_company.com.App;
import com.example.lenovo.ptjob_company.com.Model.ChatRecord;
import com.example.lenovo.ptjob_company.com.Model.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DAO {

    /**
     * 保存朋友记录
     */
    public static void saveContacts(Context context, ArrayList<Contact> contacts) {

        if (contacts != null) {
            DBHelper dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.version);
            String myId = App.ID;
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //删除以前的数据
            String sql = "delete from t_friend where my_id='" + myId + "'";
            db.execSQL(sql);

            //保存新数据
            for (int i = 0; i < contacts.size(); i++) {
                ContentValues values = new ContentValues();

                values.put("friend_id", contacts.get(i).getId());
                values.put("friend_name", contacts.get(i).getName());
                values.put("my_id", myId);
                values.put("image_name",contacts.get(i).getImageName());

                db.insertOrThrow(DBHelper.T_FRIEND, null, values);
            }
            db.close();
            dbHelper.close();
        }

    }

    /**
     * 聊天记录
     */
    public static void saveChatRecords(Context context, HashMap<String, ArrayList<ChatRecord>> chatRecordsMap) {

        if (chatRecordsMap != null) {

            DBHelper dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.version);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String _myId = App.ID;
            //删除以前的数据
            String sql = "delete from t_chat where _my_id='" + _myId + "'";
            db.execSQL(sql);

            //保存新数据
            ContentValues values;
            String friendId;
            String myId;
            String message;
            String time;

            Set set = chatRecordsMap.keySet();
            for (Object s : set) {
                String _friendId = (String) s;
                ArrayList<ChatRecord> records = chatRecordsMap.get(_friendId);

                for (ChatRecord record : records) {

                    friendId = record.getFriendId();
                    myId = record.getMyId();
                    message = record.getMessage();
                    time = record.getTime();

                    values = new ContentValues();
                    values.put("_my_id", _myId);
                    values.put("_friend_id", _friendId);
                    values.put("friend_id", friendId);
                    values.put("my_id", myId);
                    values.put("message", message);
                    values.put("time", time);

                    db.insertOrThrow(DBHelper.T_CHAT, null, values);
                }
            }

            db.close();
            dbHelper.close();
        }


    }


    /**
     * 从数据库中获取数据
     */
    public static ArrayList<Contact> getContexts(Context context, String myId) {

        ArrayList<Contact> contacts = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.version);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String friendId;
        String friendName;
        String imageName;
        Contact contact;
        Cursor cursor;

        String where = "my_id=?";
        String[] args = new String[]{myId};
        cursor = db.query(DBHelper.T_FRIEND, null, where, args, null, null, null, null);
        while (cursor.moveToNext()) {

            contact = new Contact();

            friendId = cursor.getString(2);
            friendName = cursor.getString(3);
            imageName=cursor.getString(4);


            contact.setId(friendId);
            contact.setImageName(imageName);
            contact.setName(friendName);
            contacts.add(contact);
        }


        cursor.close();
        db.close();
        dbHelper.close();

        Log.i("DAO", "getContexts: contacts " + contacts.toString());
        return contacts;
    }

    /**
     * 从数据库中获取聊天记录
     */
    public static HashMap<String, ArrayList<ChatRecord>> getChatRecordMap(Context context, ArrayList<String> friends) {

        HashMap<String, ArrayList<ChatRecord>> chatRecordsMap = new HashMap<>();
        DBHelper dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.version);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String _friendId;
        String _myId = App.ID;
        String friendId;
        String myId;
        String message;
        String time;

        Cursor cursor = null;
        ArrayList<ChatRecord> records;
        ChatRecord record;
        String where = "_friend_id=? and _my_id=?";

        for (int i = 0; i < friends.size(); i++) {
            _friendId = friends.get(i);
            records = new ArrayList<>();

            String[] args = new String[]{_friendId, _myId};

            cursor = db.query(DBHelper.T_CHAT, null, where, args, null, null, null);

            while (cursor.moveToNext()) {

                myId = cursor.getString(3);
                friendId = cursor.getString(4);
                message = cursor.getString(5);
                time = cursor.getString(6);

                record = new ChatRecord();
                record.setMyId(myId);
                record.setFriendId(friendId);
                record.setMessage(message);
                record.setTime(time);

                records.add(record);
            }

            chatRecordsMap.put(_friendId, records);
        }
        Log.i("DAO", "getChatRecordMap: chatRecordsMap " + chatRecordsMap.toString());

        if (cursor != null) {
            cursor.close();
        }

        return chatRecordsMap;
    }


    /**
     * 删除好友信息
     */
    public static void deleteFriends(Context context, String myId, String friendId) {

        DBHelper dbHelper = new DBHelper(context, DBHelper.DB_NAME, null, DBHelper.version);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //删除以前的数据
        String sql = "delete from t_chat where _my_id='" + myId + "'";
        db.execSQL(sql);

        sql = "delete from t_friend where my_id='" + myId + "'";
        db.execSQL(sql);
    }
}

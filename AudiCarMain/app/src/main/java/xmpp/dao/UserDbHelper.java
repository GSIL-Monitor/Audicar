package xmpp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.beautyyan.beautyyanapp.http.bean.User;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.LogUtil;

import java.io.Serializable;
import java.lang.reflect.Field;


public class UserDbHelper {
    private static UserDbHelper instance = null;

    private SqlLiteHelper helper;
    private SQLiteDatabase db;  //

    public UserDbHelper(Context context) {
        helper = new SqlLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public void closeDb(){
        db.close();
        helper.close();
    }
    public static UserDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserDbHelper(context);
        }
        return instance;
    }

    private class SqlLiteHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;
        private static final String USER_NAME = "user";


        public SqlLiteHelper(Context context) {
            super(context, USER_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + USER_NAME + "( id INTEGER PRIMARY KEY AUTOINCREMENT,phone text,userId LONG)");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropTable(db);
            onCreate(db);
        }

        private void dropTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_NAME);
        }

        /**
         * 游标 --转换为 --> 给定类型的对象
         *
         * @param clazz
         * @param c
         * @return
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public <T extends Serializable> T getObject(Class<T> clazz, Cursor c) throws InstantiationException, IllegalAccessException {
            T t = (T) clazz.newInstance();
            setObject(t, c);
            return t;
        }
        /**
         * 游标 --转换为 --> Object
         *
         * @param bean
         * @param c
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         */
        public void setObject(Object bean, Cursor c) throws IllegalArgumentException, IllegalAccessException {
            for (Field f : bean.getClass().getDeclaredFields())
            {
                f.setAccessible(true);
                String columnName = f.getName();
                int columnIndex = c.getColumnIndex(columnName);
                if (columnIndex == -1)
                {
                    continue;
                }
                if (f.getGenericType() == Long.class || f.getGenericType() == long.class)
                {
                    f.set(bean, c.getLong(columnIndex));
                }
                else if (f.getGenericType() == String.class)
                {
                    String s = c.getString(columnIndex);
                    f.set(bean, TextUtils.isEmpty(s) ? "" : s);
                }
                else if (f.getGenericType() == Double.class || f.getGenericType() == double.class)
                {
                    f.set(bean, c.getDouble(columnIndex));
                }
                else if (f.getGenericType() == Integer.class || f.getGenericType() == int.class)
                {
                    f.set(bean, c.getInt(columnIndex));
                }
                else if (f.getGenericType() == Float.class || f.getGenericType() == float.class)
                {
                    f.set(bean, c.getFloat(columnIndex));
                }
                else if (f.getGenericType() == Short.class || f.getGenericType() == short.class)
                {
                    f.set(bean, c.getShort(columnIndex));
                }
                else if (f.getGenericType() == Byte[].class || f.getGenericType() == byte[].class)
                {
                    f.set(bean, c.getBlob(columnIndex));
                }
                else
                { //TODO 未知类型
                    f.set(bean, c.getString(columnIndex));
                }
            }
        }

        /** 关闭游标 */
        public void closeCursor(Cursor c) {
            if (c != null)
            {
                c.close();
                c = null;
            }
        }

    }

    /** 添加用户 */
    public boolean addUser(User user) {
        if (user == null) return false;
        if ("-1".equals(Constant.getInstance().getUser().getUserId()))
        { //如果不存在就添加用户
            try
            {
                ContentValues values = new ContentValues();
                values.put("userId", user.getUserId());
                values.put("phone", user.getPhone());
                db.insert(helper.USER_NAME, null, values);
                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        { //TODO ? 如果存在就更新用户（是否需要此操作待确认）
            LogUtil.i("update");
            return updateUser(user);
        }
        return false;
    }

    public boolean updateUser(User user) {
        String selection = "phone" + " = ?";
        String[] args = new String[] {
                user.getPhone()
        };
        ContentValues values = new ContentValues();
        values.put("userId", user.getUserId());
        values.put("phone", user.getPhone());
        db.update(helper.USER_NAME, values, selection, args);
        return true;
    }

    public User getUser() {
        User user = Constant.getInstance().getUser();
        Cursor c = null;
        try
        {
            c = db.rawQuery("select * from user where userId=? and phone=?", new String[]{String.valueOf(user.getUserId()), user.getPhone()});
            if (c != null && c.getCount() > 0)
            {
                c.moveToFirst();
                user = helper.getObject(User.class, c);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            helper.closeCursor(c);
        }
        return user;
    }


}

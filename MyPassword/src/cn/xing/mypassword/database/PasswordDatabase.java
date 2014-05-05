package cn.xing.mypassword.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.xing.mypassword.model.Password;

public class PasswordDatabase extends SQLiteOpenHelper
{
	private static final int version = 2;

	public PasswordDatabase(Context context)
	{
		super(context, "password", null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "create table password(" + "id integer primary key autoincrement, " + "create_date integer, "
				+ "title text, " + "user_name text, " + "password text, " + "is_top integer default 0, " + "note text)";
		db.execSQL(sql);
	}

	/**
	 * 1 ---> ��ʼ�汾������һ����password��id�� create_date�� title�� user_name��password��
	 * note��<br />
	 * 2 ---> password�����is_top�ֶΣ�Ĭ��ֵΪ�㣬��ʾ���ö�
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (oldVersion < 2)
		{
			String sql = "alter table password add is_top integer default 0";
			db.execSQL(sql);
		}
	}

	/**
	 * ����һ������
	 * 
	 * @param password
	 *            Ҫ�����ֵ
	 * @return �����������ݵ��������� �������ʧ�ܣ�����-1
	 */
	public long insertPassword(Password password)
	{
		long id = -1;
		try
		{
			SQLiteDatabase sqLiteDatabase = getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put("create_date", password.getCreateDate());
			contentValues.put("title", password.getTitle());
			contentValues.put("user_name", password.getUserName());
			contentValues.put("password", password.getPassword());
			contentValues.put("note", password.getNote());
			contentValues.put("is_top", password.isTop() ? 1 : 0);
			id = sqLiteDatabase.insert("password", null, contentValues);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * ��������
	 * 
	 * <pre>
	 * // ��������
	 * <code>
	 * Password password = new Password();
	 * password.setId(123);
	 * password.setPassword(&quot;�µ�����&quot;);
	 * passwordDatabase.updatePassword(password);
	 * </code>
	 * @param password
	 *            ���µ����ݣ�ֻ��Ҫ������Ӧ�ĸ�����,������id����
	 * @return Ӱ������� the number of rows affected
	 */
	public int updatePassword(Password password)
	{
		int result = 0;
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		try
		{
			ContentValues contentValues = new ContentValues();
			if (password.getCreateDate() != 0)
				contentValues.put("create_date", password.getCreateDate());
			if (password.getTitle() != null)
				contentValues.put("title", password.getTitle());
			if (password.getUserName() != null)
				contentValues.put("user_name", password.getUserName());
			if (password.getPassword() != null)
				contentValues.put("password", password.getPassword());
			if (password.getNote() != null)
				contentValues.put("note", password.getNote());
			contentValues.put("is_top", password.isTop() ? 1 : 0);
			result = sqLiteDatabase.update("password", contentValues, "id = ?",
					new String[] { String.valueOf(password.getId()) });
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * ����id��ѯ���ݿ��е�������Ϣ
	 * 
	 * @param id
	 * @return ��ѯ��������Ϣ�����û�и����ݣ�����null
	 */
	public Password getPassword(int id)
	{
		Password password = null;

		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = sqLiteDatabase.query("password", null, "id = ?", new String[] { String.valueOf(id) }, null, null,
					null);

			if (cursor.moveToNext())
			{
				password = new Password();
				password.setId(cursor.getInt(cursor.getColumnIndex("id")));
				password.setCreateDate(cursor.getLong(cursor.getColumnIndex("create_date")));
				password.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				password.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
				password.setPassword(cursor.getString(cursor.getColumnIndex("password")));
				password.setNote(cursor.getString(cursor.getColumnIndex("note")));
				password.setTop(cursor.getInt(cursor.getColumnIndex("is_top")) == 1 ? true : false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}

		return password;
	}

	/**
	 * ������ݿ��б��������������Ϣ
	 * 
	 * @return �������ݣ�Ϊһ������
	 */
	public List<Password> getAllPassword()
	{
		List<Password> passwords = new ArrayList<Password>();
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();

		Cursor cursor = null;

		try
		{
			cursor = sqLiteDatabase.query("password", null, null, null, null, null, null);

			while (cursor.moveToNext())
			{
				Password password = new Password();
				password.setId(cursor.getInt(cursor.getColumnIndex("id")));
				password.setCreateDate(cursor.getLong(cursor.getColumnIndex("create_date")));
				password.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				password.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
				password.setPassword(cursor.getString(cursor.getColumnIndex("password")));
				password.setNote(cursor.getString(cursor.getColumnIndex("note")));
				password.setTop(cursor.getInt(cursor.getColumnIndex("is_top")) == 1 ? true : false);

				passwords.add(password);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}

		return passwords;

	}

	/**
	 * ɾ��һ������
	 * 
	 * @param id
	 *            ɾ����id
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise. To remove all rows and get a count pass "1" as the
	 *         whereClause.
	 */
	public int deletePasssword(int id)
	{
		int result = -1;
		SQLiteDatabase sqLiteDatabase = getWritableDatabase();
		result = sqLiteDatabase.delete("password", "id = ?", new String[] { String.valueOf(id) });
		return result;
	}
}

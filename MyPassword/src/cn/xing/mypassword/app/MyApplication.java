package cn.xing.mypassword.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Looper;
import cn.xing.mypassword.model.SettingKey;

public class MyApplication extends Application implements OnSharedPreferenceChangeListener
{
	/** �����ļ� */
	private SharedPreferences sharedPreferences;
	private Map<SettingKey, List<OnSettingChangeListener>> onSettingChangeListenerMap = new HashMap<>();

	@Override
	public void onCreate()
	{
		super.onCreate();
		loadSettings();
	}

	private void loadSettings()
	{
		sharedPreferences = getSharedPreferences("settings", Context.MODE_MULTI_PROCESS);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	/**
	 * ��ȡ����
	 * 
	 * @param key
	 *            ����key
	 * @param defValue
	 *            û�и����ý�Ҫ���ص�Ĭ��ֵ
	 * @return
	 */
	public String getString(SettingKey key, String defValue)
	{
		return sharedPreferences.getString(key.name(), defValue);
	}

	/**
	 * �������ã����ø÷���������
	 * {@link OnSettingChangeListener#onSettingChange(SettingKey)}�ص���
	 * 
	 * @param key
	 *            ���ñ���key
	 * @param value
	 *            ��Ҫ�����ֵ
	 */
	public void putString(SettingKey key, String value)
	{
		sharedPreferences.edit().putString(key.name(), value).commit();
	}

	/**
	 * ע�����øı���������÷������������߳��е��ã��Ҳ���ʱ�������
	 * {@link #unregistOnSettingChangeListener(SettingKey, OnSettingChangeListener)}
	 * 
	 * @param key
	 *            ��Ҫ������������
	 * @param onSettingChangeListener
	 *            �����仯�Ļص�
	 */
	public void registOnSettingChangeListener(SettingKey key, OnSettingChangeListener onSettingChangeListener)
	{
		checkUIThread();

		List<OnSettingChangeListener> onSettingChangeListeners;
		if (onSettingChangeListenerMap.containsKey(key))
		{
			onSettingChangeListeners = onSettingChangeListenerMap.get(key);
		}
		else
		{
			onSettingChangeListeners = new ArrayList<OnSettingChangeListener>();
			onSettingChangeListenerMap.put(key, onSettingChangeListeners);
		}
		onSettingChangeListeners.add(onSettingChangeListener);
	}

	/**
	 * ע�����ñ仯�������÷�����
	 * {@link #registOnSettingChangeListener(SettingKey, OnSettingChangeListener)}
	 * ����ʹ��
	 * 
	 * @param key
	 *            ��Ҫע��������ѡ��
	 * @param onSettingChangeListener
	 *            ������
	 */
	public void unregistOnSettingChangeListener(SettingKey key, OnSettingChangeListener onSettingChangeListener)
	{
		checkUIThread();
		if (onSettingChangeListenerMap.containsKey(key))
		{
			List<OnSettingChangeListener> onSettingChangeListeners = onSettingChangeListenerMap.get(key);
			onSettingChangeListeners.remove(onSettingChangeListener);
			if (onSettingChangeListeners.size() == 0)
			{
				onSettingChangeListenerMap.remove(key);
			}
		}
	}

	private void checkUIThread()
	{
		if (!isRunOnUIThread())
			throw new RuntimeException("����ֻ�������̵߳��ã�");
	}

	/**
	 * �жϵ�ǰ�߳��Ƿ������߳�
	 * 
	 * @return
	 */
	private boolean isRunOnUIThread()
	{
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		SettingKey settingKey = SettingKey.valueOf(SettingKey.class, key);
		List<OnSettingChangeListener> onSettingChangeListeners = onSettingChangeListenerMap.get(settingKey);
		if (onSettingChangeListeners != null)
		{
			for (OnSettingChangeListener onSettingChangeListener : onSettingChangeListeners)
			{
				onSettingChangeListener.onSettingChange(settingKey);
			}
		}
	}
}

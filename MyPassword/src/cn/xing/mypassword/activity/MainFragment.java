package cn.xing.mypassword.activity;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.xing.mypassword.R;
import cn.xing.mypassword.adapter.MainAdapter;
import cn.xing.mypassword.adapter.MainAdapter.PasswordItem;
import cn.xing.mypassword.app.BaseFragment;
import cn.xing.mypassword.app.OnSettingChangeListener;
import cn.xing.mypassword.model.Password;
import cn.xing.mypassword.model.SettingKey;
import cn.xing.mypassword.service.Mainbinder;
import cn.xing.mypassword.service.OnGetAllPasswordCallback;
import cn.xing.mypassword.service.OnPasswordListener;

import com.twotoasters.jazzylistview.JazzyEffect;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

/**
 * �����棬�����б�չʾ����
 * 
 * @author zengdexing
 * 
 */
public class MainFragment extends BaseFragment implements OnGetAllPasswordCallback, OnItemLongClickListener,
		OnPasswordListener, OnItemClickListener, OnSettingChangeListener, android.view.View.OnClickListener
{
	/** ���� */
	private MainAdapter mainAdapter;

	/** ����Դ */
	private Mainbinder mainbinder;

	private JazzyListView listView;
	/** û�����ݵ���ʾ�� */
	private View noDataView;

	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			unregistOnPasswordListener();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mainbinder = (Mainbinder) service;
			mainbinder.getAllPassword(MainFragment.this);
			mainbinder.registOnPasswordListener(MainFragment.this);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mainAdapter = new MainAdapter(getActivity());

		getBaseActivity().getMyApplication().registOnSettingChangeListener(SettingKey.JAZZY_EFFECT, this);

		Intent intent = new Intent("cn.xing.mypassword");
		getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * ��ñ��ر������Ч���û�����
	 * 
	 * @return
	 */
	private JazzyEffect getJazzyEffect()
	{
		String strKey = getBaseActivity().getSetting(SettingKey.JAZZY_EFFECT, JazzyHelper.TILT + "");
		JazzyEffect jazzyEffect = JazzyHelper.valueOf(Integer.valueOf(strKey));
		return jazzyEffect;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregistOnPasswordListener();
		getActivity().unbindService(serviceConnection);
		getBaseActivity().getMyApplication().unregistOnSettingChangeListener(SettingKey.JAZZY_EFFECT, this);
	}

	private void unregistOnPasswordListener()
	{
		if (mainbinder != null)
		{
			mainbinder.unregistOnPasswordListener(this);
			mainbinder = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		listView = (JazzyListView) rootView.findViewById(R.id.main_listview);
		listView.setAdapter(mainAdapter);
		listView.setTransitionEffect(getJazzyEffect());
		listView.setOnItemLongClickListener(this);
		listView.setOnItemClickListener(this);

		noDataView = rootView.findViewById(R.id.main_no_passsword);
		noDataView.setOnClickListener(this);
		if (mainbinder == null)
		{
			noDataView.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
		else
		{
			initView();
		}

		return rootView;
	}

	private void initView()
	{
		if (noDataView != null)
		{
			if (mainAdapter.getCount() == 0)
			{
				noDataView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			}
			else
			{
				noDataView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		listView = null;
		noDataView = null;
	}

	@Override
	public void onGetAllPassword(List<Password> passwords)
	{
		mainAdapter.setData(passwords);
		initView();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		final PasswordItem passwordItem = (PasswordItem) parent.getItemAtPosition(position);

		Builder builder = new Builder(getActivity());

		String[] item = new String[] { getString(R.string.updata), getString(R.string.delete),
				getString(R.string.copy_name), getString(R.string.copy_password) };

		builder.setItems(item, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
					case 0:
						// �޸�
						modifyPassword(passwordItem.password.getId());
						break;

					case 1:
						onDelete(passwordItem.password);
						break;
					case 2:
						// ��������
						ClipboardManager cmbName = (ClipboardManager) getActivity().getSystemService(
								Context.CLIPBOARD_SERVICE);
						ClipData clipDataName = ClipData.newPlainText(null, passwordItem.password.getUserName());
						cmbName.setPrimaryClip(clipDataName);
						break;
					case 3:
						// ��������
						ClipboardManager cmbPassword = (ClipboardManager) getActivity().getSystemService(
								Context.CLIPBOARD_SERVICE);
						ClipData clipData = ClipData.newPlainText(null, passwordItem.password.getPassword());
						cmbPassword.setPrimaryClip(clipData);
						break;
					default:
						break;
				}
			}
		});
		builder.show();
		return true;
	}

	/**
	 * ɾ��
	 * 
	 * @param password
	 *            ��ǰѡ�е�����
	 */
	private void onDelete(final Password password)
	{
		Builder builder = new Builder(getActivity());
		builder.setMessage(R.string.alert_delete_message);
		builder.setTitle(password.getTitle());
		builder.setNeutralButton(R.string.yes, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				mainbinder.deletePassword(password.getId());
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		PasswordItem passwordItem = (PasswordItem) parent.getItemAtPosition(position);
		modifyPassword(passwordItem.password.getId());
	}

	private void modifyPassword(int id)
	{
		Intent intent = new Intent(getActivity(), EditPasswordActivity.class);
		intent.putExtra(EditPasswordActivity.ID, id);
		getActivity().startActivity(intent);
	}

	@Override
	public void onSettingChange(SettingKey key)
	{
		if (listView != null && key == SettingKey.JAZZY_EFFECT)
		{
			listView.setTransitionEffect(getJazzyEffect());
		}
	}

	@Override
	public void onNewPassword(Password password)
	{
		mainAdapter.onNewPassword(password);
		initView();
	}

	@Override
	public void onDeletePassword(int id)
	{
		mainAdapter.onDeletePassword(id);
		initView();
	}

	@Override
	public void onUpdatePassword(Password newPassword)
	{
		mainAdapter.onUpdatePassword(newPassword);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.main_no_passsword:
				Intent intent = new Intent(getActivity(), EditPasswordActivity.class);
				getActivity().startActivity(intent);
				break;
			default:
				break;
		}
	}
}

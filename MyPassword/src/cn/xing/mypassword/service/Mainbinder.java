package cn.xing.mypassword.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Binder;
import cn.xing.mypassword.database.PasswordDatabase;
import cn.xing.mypassword.model.AsyncResult;
import cn.xing.mypassword.model.AsyncSingleTask;
import cn.xing.mypassword.model.Password;
import cn.xing.mypassword.model.PasswordGroup;
import cn.xing.mypassword.service.task.GetAllPasswordTask;

public class Mainbinder extends Binder {
	private PasswordDatabase passwordDatabase;
	
	/** ����仯������ */
	private List<OnPasswordListener> onPasswordListeners = new ArrayList<OnPasswordListener>();

	/** �������仯���� */
	private List<OnPasswordGroupListener> onPasswordGroupListeners = new ArrayList<OnPasswordGroupListener>();

	public Mainbinder(Context context) {
		passwordDatabase = new PasswordDatabase(context);
	}

	void onDestroy() {
		passwordDatabase.close();
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				onPasswordListeners.clear();
			}
		}.execute();
	}

	public void registOnPasswordGroupListener(final OnPasswordGroupListener onPasswordGroupListener) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				onPasswordGroupListeners.add(onPasswordGroupListener);
			}
		}.execute();
	}

	public void unregistOnPasswordGroupListener(final OnPasswordGroupListener onPasswordGroupListener) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				onPasswordGroupListeners.remove(onPasswordGroupListener);
			}
		}.execute();
	}

	public void registOnPasswordListener(final OnPasswordListener onPasswordListener) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				onPasswordListeners.add(onPasswordListener);
			}
		}.execute();
	}

	public void unregistOnPasswordListener(final OnPasswordListener onPasswordListener) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				onPasswordListeners.remove(onPasswordListener);
			}
		}.execute();
	}

	public void getAllPassword(OnGetAllPasswordCallback onGetAllPasswordCallback, String groupName) {
		GetAllPasswordTask getAllPasswordTask = new GetAllPasswordTask(passwordDatabase, onGetAllPasswordCallback,
				groupName);
		getAllPasswordTask.execute();
	}

	public void getAllPassword(OnGetAllPasswordCallback onGetAllPasswordCallback) {
		GetAllPasswordTask getAllPasswordTask = new GetAllPasswordTask(passwordDatabase, onGetAllPasswordCallback, null);
		getAllPasswordTask.execute();
	}

	/**
	 * ɾ������
	 * 
	 * @param id
	 *            ����ID
	 * @param onDeletePasswordResultListener
	 *            ���������
	 */
	public void deletePassword(final int id) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				int result = passwordDatabase.deletePasssword(id);
				asyncResult.setResult(result);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				for (OnPasswordListener onPasswordListener : onPasswordListeners) {
					onPasswordListener.onDeletePassword(id);
				}
			}
		}.execute();
	}

	public void getPassword(final int id, final OnGetPasswordCallback onGetPasswordCallback) {
		new AsyncSingleTask<Password>() {
			@Override
			protected AsyncResult<Password> doInBackground(AsyncResult<Password> asyncResult) {
				Password password = passwordDatabase.getPassword(id);
				asyncResult.setData(password);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Password> asyncResult) {
				onGetPasswordCallback.onGetPassword(asyncResult.getData());
			}
		}.execute();
	}

	public void updatePassword(final Password password) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				int result = passwordDatabase.updatePassword(password);
				asyncResult.setResult(result);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				for (OnPasswordListener onPasswordListener : onPasswordListeners) {
					onPasswordListener.onUpdatePassword(password);
				}
			}
		}.execute();
	}

	public void insertPassword(final Password password) {
		new AsyncSingleTask<Password>() {
			@Override
			protected AsyncResult<Password> doInBackground(AsyncResult<Password> asyncResult) {
				String newGroupName = password.getGroupName();

				boolean isNew = true;
				List<PasswordGroup> passwordGroups = passwordDatabase.getAllPasswordGroup();
				for (int i = 0; i < passwordGroups.size(); i++) {
					PasswordGroup passwordGroup = passwordGroups.get(i);
					if (passwordGroup.getGroupName().equals(newGroupName)) {
						isNew = false;
						break;
					}
				}

				if (isNew) {
					PasswordGroup passwordGroup = new PasswordGroup();
					passwordGroup.setGroupName(newGroupName);
					passwordDatabase.addPasswordGroup(passwordGroup);
				}
				asyncResult.getBundle().putBoolean("isNew", isNew);

				int result = (int) passwordDatabase.insertPassword(password);
				password.setId(result);
				asyncResult.setData(password);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Password> asyncResult) {
				if (asyncResult.getBundle().getBoolean("isNew")) {
					PasswordGroup passwordGroup = new PasswordGroup();
					passwordGroup.setGroupName(asyncResult.getData().getGroupName());

					for (OnPasswordGroupListener onPasswordGroupListener : onPasswordGroupListeners) {
						onPasswordGroupListener.onNewPasswordGroup(passwordGroup);
					}
				}

				for (OnPasswordListener onPasswordListener : onPasswordListeners) {
					onPasswordListener.onNewPassword(asyncResult.getData());
				}
			}
		}.execute();
	}

	public void insertPasswordGroup(final PasswordGroup passwordGroup) {
		new AsyncSingleTask<PasswordGroup>() {
			@Override
			protected AsyncResult<PasswordGroup> doInBackground(AsyncResult<PasswordGroup> asyncResult) {
				String newGroupName = passwordGroup.getGroupName();

				boolean isNew = true;
				List<PasswordGroup> passwordGroups = passwordDatabase.getAllPasswordGroup();
				for (int i = 0; i < passwordGroups.size(); i++) {
					PasswordGroup passwordGroup = passwordGroups.get(i);
					if (passwordGroup.getGroupName().equals(newGroupName)) {
						isNew = false;
						break;
					}
				}

				if (isNew) {
					PasswordGroup passwordGroup = new PasswordGroup();
					passwordGroup.setGroupName(newGroupName);
					passwordDatabase.addPasswordGroup(passwordGroup);
				}
				asyncResult.getBundle().putBoolean("isNew", isNew);
				asyncResult.setData(passwordGroup);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<PasswordGroup> asyncResult) {
				if (asyncResult.getBundle().getBoolean("isNew")) {
					for (OnPasswordGroupListener onPasswordGroupListener : onPasswordGroupListeners) {
						onPasswordGroupListener.onNewPasswordGroup(asyncResult.getData());
					}
				}
			}
		}.execute();
	}

	/**
	 * ɾ��������飬���������ס�µ��������붼�ᱻɾ��
	 * 
	 * @param passwordGroupName
	 *            ������
	 */
	public void deletePasswordgroup(final String passwordGroupName) {
		new AsyncSingleTask<Void>() {
			@Override
			protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
				int count = passwordDatabase.deletePasswordGroup(passwordGroupName);
				asyncResult.setResult(count);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<Void> asyncResult) {
				if (asyncResult.getResult() > 0) {
					for (OnPasswordGroupListener onPasswordGroupListener : onPasswordGroupListeners) {
						onPasswordGroupListener.onDeletePasswordGroup(passwordGroupName);
					}
				}
			}
		}.execute();
	}

	public void getAllPasswordGroup(final OnGetAllPasswordGroupCallback onGetAllPasswordGroupCallback) {
		new AsyncSingleTask<List<PasswordGroup>>() {
			@Override
			protected AsyncResult<List<PasswordGroup>> doInBackground(AsyncResult<List<PasswordGroup>> asyncResult) {
				List<PasswordGroup> list = passwordDatabase.getAllPasswordGroup();
				asyncResult.setData(list);
				return asyncResult;
			}

			@Override
			protected void runOnUIThread(AsyncResult<List<PasswordGroup>> asyncResult) {
				onGetAllPasswordGroupCallback.onGetAllPasswordGroup(asyncResult.getData());
			}
		}.execute();
	}
}

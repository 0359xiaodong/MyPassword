package cn.xing.mypassword.service;

import cn.xing.mypassword.model.PasswordGroup;

/**
 * ����仯������
 * 
 * @author zengdexing
 * 
 */
public interface OnPasswordGroupListener {

	/**
	 * �û��������µ�����
	 */
	public void onNewPasswordGroup(PasswordGroup passwordGroup);

	/**
	 * ���뱻ɾ����
	 */
	public void onDeletePasswordGroup(String passwordGroupName);
}

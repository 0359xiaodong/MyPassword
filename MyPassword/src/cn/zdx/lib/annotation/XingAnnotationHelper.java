/*
 * Copyright (c) 2014 zengdexing
 */
package cn.zdx.lib.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;

/**
 * ע������֧࣬���κζ���ĳ�Ա����ʹ��{@link FindViewById}���ҿؼ�������ʹ��{@link OnClick}�󶨵���¼���
 * <p>
 * ע�⣺�̳���{@link XingBaseActivity}��UI����Ҫ��ʹ�ø��࣬BaseActivity�Ѿ��ṩ�˶�
 * {@link FindViewById}�� {@link OnClick} ��֧�֣���BaseActivity��ֱ��ʹ��
 * </p>
 * 
 * @author zengdexing
 */
public class XingAnnotationHelper
{
	/**
	 * ��ʼ��Activity��ʹ����{@link FindViewById}ע��ĳ�Ա����
	 * 
	 * @param target
	 */
	public static void findView(Activity target)
	{
		findView(target, ViewFinder.create(target));
	}

	/**
	 * ��ʼ��Object��ʹ����{@link FindViewById}ע��ĳ�Ա����
	 * 
	 * @param target
	 */
	public static void findView(Object target, View view)
	{
		findView(target, ViewFinder.create(view));
	}

	/**
	 * ��ʼ��ʹ����{@link FindViewById}ע������Ա����
	 * 
	 * @param target
	 *            ��Ҫ��ʼ���Ķ��󣬸ö���ĳ�Ա����ʹ����{@link FindViewById}ע��
	 * @param viewFinder
	 *            View������
	 */
	public static void findView(Object target, ViewFinder viewFinder)
	{
		Class<?> clazz = target.getClass();

		Field[] fields = clazz.getDeclaredFields();
		if (fields != null && fields.length > 0)
		{
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				findView(target, field, viewFinder);
			}
		}
	}

	/**
	 * ��ʹ��{@link FindViewById}ע��ķ���
	 * 
	 * @param target
	 *            ��Ҫ�󶨵Ķ��󣬸ö����г�Ա����ʹ����{@link FindViewById}
	 * @param field
	 *            ��Ҫ�󶨵ı���
	 * @param viewFinder
	 *            VIew������
	 */
	public static void findView(Object target, Field field, ViewFinder viewFinder)
	{
		if (field.isAnnotationPresent(FindViewById.class))
		{
			if (!field.isAccessible())
			{
				field.setAccessible(true);
			}

			int id = field.getAnnotation(FindViewById.class).value();
			View view = viewFinder.findViewById(id);

			checkView(field.getName(), view, field.getType());

			try
			{
				field.set(target, view);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��OnClick����¼�
	 * 
	 * @param target
	 *            ʹ����{@link OnClick}ע���Activity
	 */
	public static void bindOnClick(Activity target)
	{
		bindOnClick(target, ViewFinder.create(target));
	}

	/**
	 * ��OnClick����¼�
	 * 
	 * @param target
	 *            ����ʹ����{@link OnClick}�Ķ���
	 */
	public static void bindOnClick(Object target, View view)
	{
		bindOnClick(target, ViewFinder.create(view));
	}

	/**
	 * ��ʼ��ʹ����{@link OnClick}�󶨵���¼��Ķ���
	 * 
	 * @param target
	 *            ����ʹ����{@link OnClick}�Ķ���
	 * @param viewFinder
	 *            View������
	 */
	public static void bindOnClick(Object target, ViewFinder viewFinder)
	{
		Method[] methods = target.getClass().getDeclaredMethods();
		if (methods != null && methods.length > 0)
		{
			for (Method method : methods)
			{
				bindOnClick(target, method, viewFinder);
			}
		}
	}

	/**
	 * ��OnClick�¼�
	 * 
	 * @param target
	 *            ��Ҫ�󶨵Ķ����з���ʹ����{@link OnClick}
	 * @param method
	 *            ʹ����{@link OnClick}�ķ���
	 * @param viewFinder
	 *            View������
	 */
	public static void bindOnClick(Object target, Method method, ViewFinder viewFinder)
	{
		if (method.isAnnotationPresent(OnClick.class))
		{
			if (!method.isAccessible())
			{
				method.setAccessible(true);
			}

			Class<?> parameterType = checkClickMethod(method);
			int[] ids = method.getAnnotation(OnClick.class).value();
			if (ids != null)
			{
				for (int id : ids)
				{
					View view = viewFinder.findViewById(id);
					checkView(method.getName(), view, parameterType);

					view.setOnClickListener(new OnAnnotationClickListener(target, method));
				}
			}
		}
	}

	/**
	 * ������͡�</br>
	 * <p>
	 * �������{@link NullPointerException}�쳣��ͨ����ע���ID���󣬼������ݸ�ID�Ҳ�����Ӧ�Ŀؼ���
	 * </p>
	 * <p>
	 * �������{@link ClassCastException}�쳣��ͨ����ע��ID�Ŀؼ���XML�е����ͺ���Ҫ�󶨵Ķ������Ͳ�һ�¡�
	 * </p>
	 */
	private static void checkView(String msg, View targetView, Class<?> bindType)
	{
		if (targetView == null)
		{
			throw new NullPointerException("\"" + msg + "\"Ҫ�󶨵Ŀؼ�������!!");
		}
		else if (!bindType.isInstance(targetView))
		{
			String error = "����ƥ�����\"" + msg + "\"Ҫ�󶨵�����Ϊ��" + bindType + ",��Ŀ������Ϊ��" + targetView.getClass();
			throw new ClassCastException(error);
		}
	}

	private static Class<?> checkClickMethod(Method method)
	{
		Class<?> result = null;
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes != null && parameterTypes.length == 1)
		{
			result = parameterTypes[0];
		}
		else
		{
			throw new IllegalArgumentException("ʹ��onClickע��󶨵���¼�������ֻ����һ��View������߸���,���󷽷���" + method.getName());
		}
		return result;
	}
}

/*
 * Copyright (c) 2014 zengdexing
 */
package cn.zdx.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OnClickע�⣬ʹ�ø�ע�ⷽ�����뺬����ֻ����һ��������View
 * 
 * <pre>
 * <code>
 * ʹ�÷�����
 * at OnClick({ R.id.button1, R.id.button2 }) 
 * public void onClick(View view){ 
 * 	System.out.println("����ˣ�������");
 * }</code>
 * </pre>
 * 
 * @author zengdexing
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface OnClick
{
	int[] value();
}

package com.wind.aop.runtime;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.wind.aop.annotations.StatisticsAnnotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.lang.reflect.Method;

/**
 * Created by wind on 2018/6/4.
 */
@Aspect
public class StatisticsRuntime {

    @Before("execution(@com.wind.aop.annotations.StatisticsAnnotation * *(..)) && @annotation(ann)")
    public void statistics(JoinPoint point, StatisticsAnnotation ann){
        Log.e("StatisticsRuntime","aop 拦截成功:"+ann.value());

        Context context = null;
        final Object object = point.getThis();
        if (object instanceof Context) {
            context = (Context) object;
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            context = ((android.app.Fragment) object).getActivity();
        }
        if (context==null){
            try {
                Class clazz=Class.forName("com.marryu.App");
                Method getMethod=clazz.getMethod("get");
                context= (Context) getMethod.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
           // System.out.println("com.marryu.App ->" +context);
        }
        if (context!=null){
            MobclickAgent.onEvent(context,ann.value());
        }



    }

}

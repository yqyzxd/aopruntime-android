package com.wind.aop.runtime;

import android.os.Looper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


@Aspect
public class ThreadRuntime {

    @Around("execution(@com.wind.aop.annotations.IoThread * *(..))")
    public void ioThread(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter)  {
                //Log.e("ThreadRuntime", "执行方法前:" + System.currentTimeMillis());
                try {
                    proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    //Log.e("ThreadRuntime", "异常");
                }
                //Log.e("ThreadRuntime", "执行方法后:" + System.currentTimeMillis());

            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }




    @Around("execution(@com.wind.aop.annotations.UiThread * *(..))")
    public void uiThread(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //保证在主线程
        if(Looper.myLooper()==Looper.getMainLooper()){
            try {
                proceedingJoinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }
        //如果不在 切换到主线程
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                try{
                    proceedingJoinPoint.proceed();
                }catch (Throwable throwable){
                    throwable.printStackTrace();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();

    }

}

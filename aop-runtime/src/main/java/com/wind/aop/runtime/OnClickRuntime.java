package com.wind.aop.runtime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created By wind
 * on 2019-11-25
 */
@Aspect
public class OnClickRuntime {


    private  boolean canFastClick;

    //Pointcut 表示执行切入点
    //execution(注释名   注释用的地方)
    //注解类所在包名，FastClick注解类名，* *(..) 方法名和可执行参数名
    @Pointcut("execution(@com.wind.aop.annotations.FastClick * *(..))")
    public void canFastClick(){ }

    //@Before()  在切入点之前运行
    //@After()   在切入点之后运行
    //@Around()  在切入点前后都运行
    @Before("canFastClick()")
    public void beforeClick(JoinPoint joinPoint) throws Throwable {
        canFastClick=true;
    }

    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
    public void onClickLitener(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //可以通过proceedingJoinPoint获取到方法名注解参数，反射获取方法参数等
        boolean fastClick=ClickUtils.isFastClick();
        //System.out.println("aop_click fastClick:"+fastClick+" canFastClick:"+canFastClick);
        if (!fastClick
                || canFastClick) {
            //可在此处理执行之前想要做的操作
            proceedingJoinPoint.proceed();//执行方法
            //可在此处理执行之后想要做的操作
            canFastClick=false;
        }
    }

}

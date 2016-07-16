package com.dianwoba.rha.tx.aspect;

import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class CompensableTransactionAspect2 {

    @Pointcut(value = "@annotation(transactionBase)", argNames = "transactionBase")
    private void base(TransactionBase transactionBase) {
    }

    @Around(value = "base(transactionBase)")
    public Object baseAround(final ProceedingJoinPoint joinPoint, final TransactionBase transactionBase) throws Throwable {
        System.out.println("bizNo is " + locateBizNo(joinPoint, transactionBase.types()[0].getAutoMatchBizNoName()));

        Object obj;
        System.out.println("transactionBase aspect begin");
        obj = joinPoint.proceed();
        System.out.println("transactionBase aspect end");
        return obj;
    }

    @Pointcut(value = "@annotation(transactionRemote)", argNames = "transactionRemote")
    private void remote(TransactionRemote transactionRemote) {
    }

    @Around(value = "remote(transactionRemote)")
    public Object remoteAround(final ProceedingJoinPoint joinPoint, final TransactionRemote transactionRemote) throws Throwable {
        Object obj;
        System.out.println("transactionRemote aspect begin");
        obj = joinPoint.proceed();
        System.out.println("transactionRemote aspect end");
        return obj;
    }

    /**
     * 定位到具体的bizNo
     *
     * @param joinPoint
     * @param autoMatchBizNoName
     * @return
     */
    private Object locateBizNo(JoinPoint joinPoint, String autoMatchBizNoName)  {
        Class targetClass = joinPoint.getTarget().getClass();
        Object[] args = joinPoint.getArgs();
        Class[] paramClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramClasses[i] = args[i].getClass();
        }
        Object bizNo = null;

        // 先通过参数注解DistributionalTransactionBizNo查找
        try {
            Method method = targetClass.getMethod(joinPoint.getSignature().getName(), paramClasses);
            Annotation[][] annotationses = method.getParameterAnnotations();
            for (int i = 0; i < annotationses.length; i++) {
                for (Annotation annotation : annotationses[i]) {
                    if (annotation instanceof TransactionBizNo) {
                        bizNo = args[i];
                        System.out.println("bizNo is " + bizNo);
                        break;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 然后通过参数属性去查找
        if (bizNo == null && !StringUtils.isEmpty(autoMatchBizNoName)) {
            String bizNoName = autoMatchBizNoName;
            for (Object arg : args) {
                if (arg.getClass().isPrimitive()) { // 忽略基础类型
                    continue;
                }
                try {
                    bizNo = BeanUtils.getProperty(arg, bizNoName);
                } catch (Exception e) {}

                if (bizNo == null) {
                    String maybeClass = null;
                    if (bizNoName.endsWith("Id")) {
                        maybeClass = bizNoName.substring(0, bizNoName.indexOf("Id"));
                        if (arg.getClass().getSimpleName().equalsIgnoreCase(maybeClass)) {
                            try {
                                bizNo = BeanUtils.getProperty(arg, "id");
                            } catch (Exception e) {}
                        }
                    }
                }
            }
            System.out.println("bizNo is " + bizNo);
        }
        return bizNo;
    }
}

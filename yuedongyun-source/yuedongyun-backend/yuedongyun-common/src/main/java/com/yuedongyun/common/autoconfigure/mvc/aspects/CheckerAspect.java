package com.yuedongyun.common.autoconfigure.mvc.aspects;

import com.yuedongyun.common.utils.ArrayUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.validate.Checker;
import com.yuedongyun.common.validate.annotations.ParamChecker;
import lombok.extern.slf4j.Slf4j;
import org.aspecyuedongyun.lang.JoinPoint;
import org.aspecyuedongyun.lang.annotation.Aspect;
import org.aspecyuedongyun.lang.annotation.Before;

import java.util.List;

@Aspect
@Slf4j
@SuppressWarnings("all")
public class CheckerAspect {

    @Before("@annotation(paramChecker)")
    public void before(JoinPoint joinPoint, ParamChecker paramChecker) {
        Object[] args = joinPoint.getArgs();
        if(ArrayUtils.isNotEmpty(args)){
            //遍历方法参数，参数是否实现了Checker接口
            for (Object arg : args){
                if(arg instanceof Checker) {
                    //调用check方法，校验业务逻辑
                    ((Checker)arg).check();
                }else if(arg instanceof List){
                    //如果参数是一个集合也要校验
                    CollUtils.check((List) arg);
                }
            }
        }
    }
}

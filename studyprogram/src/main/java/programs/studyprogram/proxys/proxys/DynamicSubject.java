package programs.studyprogram.proxys.proxys;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/3/31 0031.
 */

public class DynamicSubject implements InvocationHandler {

    private Object sub;
    public DynamicSubject() {}

    public DynamicSubject(Object obj) {
        sub = obj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println( " before calling "  + method);
        method.invoke(sub,args);
        System.out.println( " after calling "  + method);
        return  null ;
    }
}

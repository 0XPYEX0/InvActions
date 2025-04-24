package me.xpyex.lib.xplib.util.reflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import me.xpyex.lib.xplib.util.RootUtil;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import org.jetbrains.annotations.NotNull;

public class MethodUtil extends RootUtil {
    private static final WeakHashMap<String, Method> METHOD_CACHE = new WeakHashMap<>();
    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    private static List<String> getClassNames(Class<?>[] classes) {
        return (classes == null || classes.length == 0) ? Collections.emptyList() : Arrays.stream(classes).map(Class::getName).collect(Collectors.toList());
    }

    @NotNull
    public static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) throws ReflectiveOperationException {
        ValueUtil.notEmpty("参数不应为空值", clazz, name);
        if (paramTypes == null) paramTypes = EMPTY_CLASS_ARRAY;
        String mapKey = clazz.getName() + "." + name + "(" + String.join(", ", getClassNames(paramTypes)) + ")";
        if (METHOD_CACHE.containsKey(mapKey)) {
            return METHOD_CACHE.get(mapKey);
        }
        Method resultMethod = null;
        try {
            resultMethod = clazz.getDeclaredMethod(name, paramTypes);  //尝试直接从class里面取得这个方法
            METHOD_CACHE.put(mapKey, resultMethod);
            return resultMethod;
        } catch (NoSuchMethodException ignored) {
            checkMethod:
            for (Method currentMethod : clazz.getDeclaredMethods()) {  //遍历这个class内所有方法
                if (!name.equals(currentMethod.getName())) {  //方法名是否一致
                    continue;  //可以确定不是这个Method了
                }
                Class<?>[] currentMethodParamTypes = currentMethod.getParameterTypes();
                if (currentMethodParamTypes.length != paramTypes.length) {  //方法长度是否一致
                    continue;  //可以确定不是这个Method了
                }

                checkParams:
                for (int i = 0; i < currentMethodParamTypes.length; i++) {  //检查参数类型
                    Class<?> targetType = currentMethodParamTypes[i];
                    Class<?> paramType = paramTypes[i];

                    if (targetType.isAssignableFrom(paramType)) continue checkParams;   //参数同源，检查下一个参数
                    if (ClassUtil.classEquals(paramType, targetType)) continue checkParams;

                    continue checkMethod;
                }

                if (resultMethod != null) {  //如果参数也符合，看看有没有多个匹配的Method，找最合适的
                    //上一轮循环已经找过一个方法了
                    if (resultMethod.equals(currentMethod)) {  //同一个方法就跳过
                        continue checkMethod;
                    }
                    moreSuitable:
                    for (int i = 0; i < resultMethod.getParameterTypes().length; i++) {  //上一次找到的方法的参数类型
                        Class<?> lastParamType = resultMethod.getParameterTypes()[i];
                        Class<?> newParamType = currentMethod.getParameterTypes()[i];
                        //如果newParamType instanceof lastParamType，则newParamType更适合，替换
                        if (newParamType.isAssignableFrom(lastParamType)) {  //lastParamType更适合，继续找下一个方法
                            continue checkMethod;
                        }
                    }
                }
                resultMethod = currentMethod;  //参数长度、类型均匹配
            }
        }
        if (resultMethod != null) {
            METHOD_CACHE.put(mapKey, resultMethod);
            return resultMethod;
        }
        if (clazz.getSuperclass() != null) {
            METHOD_CACHE.put(mapKey, getMethod(clazz.getSuperclass(), name, paramTypes));
            return METHOD_CACHE.get(mapKey);
        }
        throw new NoSuchMethodException(clazz.getName() + " 类中不存在方法 " + name + "(" + String.join(", ", getClassNames(paramTypes)) + ")");
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T executeInstanceMethod(Object obj, String method, Object... parma) throws ReflectiveOperationException {
        ValueUtil.notNull("执行方法的对象或方法名为null", obj, method);
        List<Class<?>> list = (parma != null && parma.length > 0) ? Arrays.stream(parma).map(Object::getClass).collect(Collectors.toList()) : Collections.emptyList();
        Method objMethod = getMethod(obj.getClass(), method, list.toArray(EMPTY_CLASS_ARRAY));
        boolean accessible = objMethod.isAccessible();
        objMethod.setAccessible(true);
        Object result = objMethod.invoke(obj, parma);
        objMethod.setAccessible(accessible);
        return (T) result;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T executeStaticMethod(Class<?> clazz, String method, Object... parma) throws ReflectiveOperationException {
        ValueUtil.notNull("执行静态方法的目标类或方法名为null", clazz, method);
        List<Class<?>> list = (parma != null && parma.length > 0) ? Arrays.stream(parma).map(Object::getClass).collect(Collectors.toList()) : Collections.emptyList();
        Method classMethod = getMethod(clazz, method, list.toArray(EMPTY_CLASS_ARRAY));
        boolean accessible = classMethod.isAccessible();
        classMethod.setAccessible(true);
        Object result = classMethod.invoke(null, parma);
        classMethod.setAccessible(accessible);
        return (T) result;
    }

    @NotNull
    public static <T> T executeStaticMethod(String className, String method, Object... parma) throws ReflectiveOperationException {
        return executeStaticMethod(ClassUtil.getClass(className, true, false), method, parma);
        //
    }

    public static boolean exist(Class<?> clazz, String method) {
        return Arrays.stream(clazz.getMethods()).anyMatch(m -> m.getName().equals(method));
    }

    public static boolean exist(Class<?> clazz, String method, Class<?>... paramTypes) {
        try {
            getMethod(clazz, method, paramTypes);
            return true;
        } catch (NoClassDefFoundError | ReflectiveOperationException e) {
            return false;
        }
    }
}

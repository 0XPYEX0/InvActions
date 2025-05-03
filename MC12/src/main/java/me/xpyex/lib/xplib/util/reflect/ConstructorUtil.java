package me.xpyex.lib.xplib.util.reflect;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import me.xpyex.lib.xplib.util.RootUtil;
import me.xpyex.lib.xplib.util.value.ValueUtil;

public class ConstructorUtil extends RootUtil {
    private static final WeakHashMap<String, Constructor<?>> CONSTRUCTOR_CACHE = new WeakHashMap<>();

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) throws ReflectiveOperationException {
        ValueUtil.notEmpty("参数不应为空值", clazz);
        if (paramTypes == null) paramTypes = MethodUtil.EMPTY_CLASS_ARRAY;
        String mapKey = clazz.getName() + "(" + String.join(", ", MethodUtil.getClassNames(paramTypes)) + ")";
        if (CONSTRUCTOR_CACHE.containsKey(mapKey)) {
            return (Constructor<T>) CONSTRUCTOR_CACHE.get(mapKey);
        }
        Constructor<T> resultMethod = null;
        try {
            resultMethod = clazz.getDeclaredConstructor(paramTypes);  //尝试直接从class里面取得这个方法
            CONSTRUCTOR_CACHE.put(mapKey, resultMethod);
            return resultMethod;
        } catch (NoSuchMethodException ignored) {
            checkMethod:
            for (Constructor<?> currentMethod : clazz.getConstructors()) {  //遍历这个class内所有构造方法
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
                resultMethod = (Constructor<T>) currentMethod;  //参数长度、类型均匹配
            }
        }
        if (resultMethod != null) {
            CONSTRUCTOR_CACHE.put(mapKey, resultMethod);
            return resultMethod;
        }
        throw new NoSuchMethodException(clazz.getName() + " 类中不存在构造方法 " + clazz.getSimpleName() + "(" + String.join(", ", MethodUtil.getClassNames(paramTypes)) + ")");
    }

    public static <T> T newInstance(Class<T> clazz, Object... param) throws ReflectiveOperationException {
        ValueUtil.notNull("参数不应为空值", clazz);
        List<Class<?>> list = (param != null && param.length > 0) ? Arrays.stream(param).map(Object::getClass).collect(Collectors.toList()) : Collections.emptyList();
        Constructor<T> constructor = getConstructor(clazz, list.toArray(MethodUtil.EMPTY_CLASS_ARRAY));
        boolean accessible = constructor.isAccessible();
        constructor.setAccessible(true);
        T result = constructor.newInstance(param);
        constructor.setAccessible(accessible);
        return result;
    }
}

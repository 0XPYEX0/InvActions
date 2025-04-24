package me.xpyex.plugin.xplib.util.reflect;

import java.lang.reflect.Field;
import java.util.WeakHashMap;
import me.xpyex.plugin.xplib.util.RootUtil;
import me.xpyex.plugin.xplib.util.value.ValueUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FieldUtil extends RootUtil {
    private static final WeakHashMap<String, Field> FIELD_CACHE = new WeakHashMap<>();

    @NotNull
    public static Field getClassField(Class<?> clazz, String key) throws ReflectiveOperationException {
        ValueUtil.notEmpty("参数不应为空值", clazz, key);
        String mapKey = clazz.getName() + key;
        if (FIELD_CACHE.containsKey(mapKey)) {
            return FIELD_CACHE.get(mapKey);
        }
        try {
            FIELD_CACHE.put(mapKey, clazz.getDeclaredField(key));
            return FIELD_CACHE.get(mapKey);
        } catch (ReflectiveOperationException ignored) {
            if (clazz.getSuperclass() != null) {
                return getClassField(clazz.getSuperclass(), key);
            }
        }
        throw new NoSuchFieldException("类 " + clazz.getName() + " 内没有字段 " + key);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getObjectField(Object obj, String key) throws ReflectiveOperationException {
        Field field = getClassField(obj.getClass(), key);
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object result = field.get(obj);
            field.setAccessible(accessible);
            return (T) result;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getStaticField(Class<?> clazz, String key) throws ReflectiveOperationException {
        Field field = getClassField(clazz, key);
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object result = field.get(null);
            field.setAccessible(accessible);
            return (T) result;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    @Nullable
    public static <T> T getStaticField(String className, String key) throws ReflectiveOperationException {
        return getStaticField(ClassUtil.getClass(className, true, false), key);
    }

    public static void setObjectField(Object obj, String key, Object value) throws ReflectiveOperationException {
        Field field = getClassField(obj.getClass(), key);
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(accessible);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static void setStaticField(Class<?> clazz, String key, Object value) throws ReflectiveOperationException {
        Field field = getClassField(clazz, key);
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(accessible);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    public static <T> void copyObjectFields(T origin, T target, Class<?> clazz) {
        for (Field declaredField : clazz.getDeclaredFields()) {  //仅复制该Class变量
            String fieldName = declaredField.getName();
            try {
                setObjectField(target, fieldName, getObjectField(origin, fieldName));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> void copyObjectFields(T origin, T target) {
        Class<?> clazz = origin.getClass();
        while (clazz != null && clazz != Object.class) {  //去找所有父类的字段复制
            copyObjectFields(origin, target, clazz);
            clazz = clazz.getSuperclass();
        }
    }
}

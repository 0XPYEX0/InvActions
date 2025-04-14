package me.xpyex.plugin.xplib.util.reflect;

import java.util.WeakHashMap;
import me.xpyex.plugin.xplib.util.RootUtil;
import org.jetbrains.annotations.NotNull;
import sun.reflect.Reflection;

public class ClassUtil extends RootUtil {
    private static final WeakHashMap<String, Class<?>> CLASS_CACHE = new WeakHashMap<>();

    @NotNull
    public static Class<?> getClass(String name, boolean needInitClass, boolean isDeepSearch) throws ClassNotFoundException {
        if (!CLASS_CACHE.containsKey(name)) {
            try {
                CLASS_CACHE.put(name, Class.forName(name, needInitClass, Reflection.getCallerClass().getClassLoader()));
            } catch (ReflectiveOperationException ignored) {
                try {
                    CLASS_CACHE.put(name, Class.forName(name, needInitClass, ClassLoader.getSystemClassLoader()));
                } catch (ReflectiveOperationException ignored1) {
                    if (isDeepSearch) {
                        for (Thread thread : Thread.getAllStackTraces().keySet()) {
                            try {
                                CLASS_CACHE.put(name, Class.forName(name, needInitClass, thread.getContextClassLoader()));
                                return CLASS_CACHE.get(name);
                            } catch (ReflectiveOperationException ignored3) {
                            }
                        }
                    }
                    throw new ClassNotFoundException("无法找到类 " + name);
                }
            }
        }
        return CLASS_CACHE.get(name);
    }

    public static void clearCache() {
        CLASS_CACHE.clear();
        //
    }
}

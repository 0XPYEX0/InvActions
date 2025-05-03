package me.xpyex.lib.xplib.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.xpyex.lib.xplib.util.RootUtil;
import me.xpyex.lib.xplib.util.reflect.ConstructorUtil;
import me.xpyex.lib.xplib.util.reflect.FieldUtil;
import me.xpyex.lib.xplib.util.reflect.MethodUtil;

public class GsonUtil extends RootUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * 将JSON文本转化为JsonObject
     *
     * @param jsonText JSON文本
     * @return 对应的JsonObject
     */
    public static JsonObject parseJsonObject(String jsonText) {
        return parseJson(jsonText, JsonObject.class);
        //
    }

    public static JsonObject parseJsonObject(Object obj) {
        return parseJsonObject(parseStr(obj));
        //
    }

    /**
     * 将JSON文本转化为JsonArray
     *
     * @param jsonText JSON文本
     * @return 对应的JsonArray
     */
    public static JsonArray parseJsonArray(String jsonText) {
        return parseJson(jsonText, JsonArray.class);
        //
    }

    /**
     * 将JSON文本转化为有规律的某个类
     *
     * @param jsonText JSON文本
     * @param type     要返回的类
     * @return 返回自定义类
     */
    public static <T> T parseJson(String jsonText, Class<T> type) {
        return GSON.fromJson(jsonText, type);
        //
    }

    /**
     * 将实例转化为JSON文本
     *
     * @param json 实例
     * @return JSON文本
     */
    public static String parseStr(Object json) {
        if (json instanceof String) {
            GsonUtil.parseJsonObject((String) json);  //检查是不是JSON格式
            return (String) json;
        }
        return GSON.toJson(json);
    }

    /**
     * 安全复制JsonObject
     *
     * @param o1 要被复制的JsonObject
     * @return 被复制的JsonObject
     */
    @SuppressWarnings("unchecked")
    public static <T extends JsonElement> T copy(T o1) {
        try {
            return (T) o1.deepCopy();
        } catch (IllegalAccessError ignored) {
            try {
                return MethodUtil.executeInstanceMethod(o1, "deepCopy");  //司马Gson，反射看你听不听话
            } catch (ReflectiveOperationException ignored1) {
                if (o1.isJsonObject()) {
                    JsonObject obj = (JsonObject) o1;
                    JsonObject result = new JsonObject();
                    obj.entrySet().forEach(entry -> result.add(entry.getKey(), copy(entry.getValue())));
                    return (T) result;
                } else if (o1.isJsonArray()) {
                    JsonArray array = (JsonArray) o1;
                    JsonArray result = new JsonArray();
                    array.forEach(result::add);
                    return (T) result;
                } else if (o1.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) o1;
                    try {
                        return (T) ConstructorUtil.newInstance(JsonPrimitive.class, FieldUtil.getInstanceField(primitive, "value"));
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        throw new IllegalArgumentException("传入的对象无法被复制");
    }

    /**
     * 获取JsonObject中的所有Key
     *
     * @param target 目标JsonObject
     * @return 所有Key组成的数组
     */
    public static Set<String> getKeysOfJsonObject(JsonObject target) {
        try {
            return target.keySet();
        } catch (NoSuchMethodError ignored) {
            Set<String> set = new HashSet<>();
            target.entrySet().forEach(E -> set.add(E.getKey()));
            return set;
        }
    }

    public static Gson getGson() {
        return GSON;
        //
    }

    public static List<JsonElement> arrayAsList(JsonArray array) {
        try {
            return array.asList();
        } catch (NoSuchMethodError e) {
            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);
            return list;
        }
    }
}

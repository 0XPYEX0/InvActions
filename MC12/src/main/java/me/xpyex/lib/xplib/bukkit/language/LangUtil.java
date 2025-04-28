package me.xpyex.lib.xplib.bukkit.language;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import me.xpyex.lib.xplib.api.Pair;
import me.xpyex.lib.xplib.bukkit.config.ConfigUtil;
import me.xpyex.lib.xplib.bukkit.config.GsonUtil;
import me.xpyex.lib.xplib.bukkit.core.XPPlugin;
import me.xpyex.lib.xplib.util.RootUtil;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class LangUtil extends RootUtil {
    private static final WeakHashMap<String, JsonObject> langCache = new WeakHashMap<>();  //插件名-zh_cn, 语言文件
    private static final JsonPrimitive DEFAULT_LANG = new JsonPrimitive("zh_cn");
    private static final JsonPrimitive EMPTY_STR = new JsonPrimitive("");

    //lastJsonObject, lastKey
    private static Pair<JsonObject, String> getFinalJsonObj(XPPlugin plugin, String key) {
        ValueUtil.notEmpty("参数不应为空值", plugin, key);

        String lang = ValueUtil.getOrDefault(ConfigUtil.getConfig(plugin, JsonObject.class).get("lang"),
                DEFAULT_LANG)
                          .getAsString();  //从config.json读取lang设定项，如zh、en等
        String cacheKey = plugin.getName() + "-" + lang;
        String fileName = "lang/" + lang + ".json";
        if (!langCache.containsKey(cacheKey)) {
            if (!new File(plugin.getDataFolder(), fileName).exists()) {
                plugin.saveResource(fileName, false);  //看看插件里面有没有自带对应的语言文件，不覆盖用户创建的
            }

            JsonObject messages = ConfigUtil.getConfig(plugin, "lang/" + lang, JsonObject.class);  //从lang/zh.json之类的文件取出JsonObject
            if (messages == null) {  //当不存在这个配置文件时
                plugin.getLogger().warning("找不到对应的语言文件，或内容非JSON文本: " + fileName);
                plugin.getLogger().warning("请调整 config.json 内的 lang 项");
                plugin.getLogger().warning("或在 plugins/" + plugin.getName() + "/lang 目录下创建对应的语言文件");
                return null;
            }
            langCache.put(cacheKey, messages);
        }

        JsonObject last = langCache.get(cacheKey);
        String lastKey = "";
        for (String s : key.split("\\.")) {
            if (last.has(s)) {
                JsonElement element = last.get(s);
                if (element.isJsonObject()) {
                    last = (JsonObject) element;
                }
                lastKey = s;
            }
        }
        if (last.has(lastKey)) {
            return Pair.of(last, lastKey);
        }
        plugin.getLogger().warning("在语言文件中无法找到对应的文本内容！");
        plugin.getLogger().warning("fileName = " + fileName);
        plugin.getLogger().warning("key = " + key);
        plugin.getLogger().warning("请调整 config.json 内的 lang 项，尝试使用默认配置");
        plugin.getLogger().warning("或在语言文件中新增该项，以自定义文本");
        return null;
    }

    public static List<String> getMessages(XPPlugin plugin, String key) {
        Pair<JsonObject, String> pair = getFinalJsonObj(plugin, key);
        if (pair != null) {
            if (pair.getKey().has(pair.getValue())) {
                return GsonUtil.arrayAsList(pair.getKey().get(pair.getValue()).getAsJsonArray()).stream().map(JsonElement::getAsString).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public static String getMessage(XPPlugin plugin, String key) {
        Pair<JsonObject, String> pair = getFinalJsonObj(plugin, key);
        if (pair != null) {
            if (pair.getKey().has(pair.getValue())) {
                return pair.getKey().get(pair.getValue()).getAsString();
            }
        }
        return "";
    }

    public static String getItemName(XPPlugin plugin, Material material) {
        String pluginLang = ValueUtil.getOrDefault(
                ConfigUtil.getConfig(plugin, JsonObject.class).get("lang"),
                DEFAULT_LANG)
                                .getAsString();
        JsonObject client = ConfigUtil.getConfig(InvActions.getInstance(), "minecraft/" + pluginLang, JsonObject.class);
        return ValueUtil.getOrDefault((() ->
                                           ((material.isBlock()) ?
                                                client.get("block.minecraft." + material.toString().toLowerCase()) :
                                                client.get("item.minecraft." + material.toString().toLowerCase())
                                           ).getAsString()
        ), material.toString().toLowerCase());
    }

    public static String getTranslationName(XPPlugin plugin, EntityType type) {
        if (type == null) return "";

        String pluginLang = ValueUtil.getOrDefault(
                ConfigUtil.getConfig(plugin, JsonObject.class).get("lang"),
                DEFAULT_LANG)
                                .getAsString();
        JsonObject client = ConfigUtil.getConfig(InvActions.getInstance(), "minecraft/" + pluginLang, JsonObject.class);
        return ValueUtil.getOrDefault(client.get("entity.minecraft." + type.toString().toLowerCase()), EMPTY_STR).getAsString();
    }
}

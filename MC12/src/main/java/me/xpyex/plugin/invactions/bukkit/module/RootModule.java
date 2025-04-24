package me.xpyex.plugin.invactions.bukkit.module;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import lombok.Getter;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.invactions.bukkit.command.HandleCmd;
import me.xpyex.plugin.invactions.bukkit.config.InvActionsServerConfig;
import me.xpyex.plugin.invactions.bukkit.util.SettingsUtil;
import me.xpyex.lib.xplib.bukkit.config.ConfigUtil;
import me.xpyex.lib.xplib.bukkit.inventory.ItemUtil;
import me.xpyex.lib.xplib.bukkit.inventory.Menu;
import me.xpyex.lib.xplib.bukkit.inventory.button.Button;
import me.xpyex.lib.xplib.bukkit.inventory.button.UnmodifiableButton;
import me.xpyex.lib.xplib.bukkit.language.LangUtil;
import me.xpyex.lib.xplib.util.reflect.FieldUtil;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class RootModule implements Listener {
    public static String SETTING_HELP = "&e该功能在 &f/InvActions &e中调整";
    public static ArrayList<RootModule> modules = new ArrayList<>();
    private final boolean canLoad;
    private final WeakHashMap<UUID, Long> cooldown = new WeakHashMap<>();

    static {
        ValueUtil.ifPresent(LangUtil.getMessage(InvActions.getInstance(), "ActionBarSuffix"), s -> SETTING_HELP = s);
    }

    @Getter
    private final String name = getClass().getSimpleName();;

    public RootModule() {
        boolean canLoad1;
        try {
            canLoad1 = canLoad();
        } catch (Throwable e) {
            e.printStackTrace();
            canLoad1 = false;
        }
        this.canLoad = canLoad1;
        // 只获取一次，避免性能浪费. 在此之后的逻辑都不应再调用canLoad()方法

        if (canLoad) {
            try {
                InvActions.getInstance().registerListener(this);
                registerCustomListener();
            } catch (Throwable e) {
                InvActions.getInstance().getLogger().severe("无法为模块 " + getName() + " 注册监听器: " + e);
                e.printStackTrace();
            }
        } else {
            InvActions.getInstance().getLogger().warning("您的服务器不支持使用 " + getName() + " ，已自动禁用");
        }

        modules.add(this);
    }

    public void registerCustomListener() {
    }

    protected boolean canLoad() throws NoClassDefFoundError {
        return true;
        //
    }

    public final String getNationalMessage(String key, Object... toFormatObj) {
        if (key == null || toFormatObj == null) return "";
        String lang = LangUtil.getMessage(InvActions.getInstance(), getName() + "." + key);
        return MessageFormat.format(lang, toFormatObj);
    }

    public final List<String> getNationalMessages(String key) {
        return LangUtil.getMessages(InvActions.getInstance(), getName() + "." + key);
    }

    public final String getMessageWithSuffix(String key, Object... toFormatObj) {
        return getNationalMessage(key, toFormatObj) + " " + SETTING_HELP;
    }

    public Button getMenuButton(Menu menu) {
        return new UnmodifiableButton(menu,
            p -> {
                if (!serverEnabled()) {
                    return -1;
                }
                if (!playerEnabled(p)) {
                    return 0;
                }
                return 1;
            })
                   .addMode(-1, ItemUtil.getItemStack(HandleCmd.MENU_WOOL_RED,
                       getNationalMessage("menu.name"),
                       getMenuLore(-1)
                   ))
                   .addMode(0, ItemUtil.getItemStack(HandleCmd.MENU_WOOL_RED,
                       getNationalMessage("menu.name"),
                       getMenuLore(0)
                   ))
                   .addMode(1, ItemUtil.getItemStack(HandleCmd.MENU_WOOL_GREEN,
                       getNationalMessage("menu.name"),
                       getMenuLore(1)
                   ))
                   .setClickEffect((player, clickType, itemStack) -> {
                       try {
                           FieldUtil.setObjectField(SettingsUtil.getConfig(player), getName(), !playerEnabled(player));
                       } catch (ReflectiveOperationException e) {
                           throw new RuntimeException(e);
                       }
                       player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                       ConfigUtil.saveConfig(InvActions.getInstance(), "players" + File.separator + player.getUniqueId(), SettingsUtil.getConfig(player), true);
                   });
    }

    private String[] getMenuLore(int state) {
        ArrayList<String> list = new ArrayList<>(getNationalMessages("menu.lore"));  //不确定List具体类型，copy一份
        list.add("");
        list.add(LangUtil.getMessage(InvActions.getInstance(), "State.current")
                     + ": "
                     + LangUtil.getMessage(InvActions.getInstance(), "State." + (state == 1 ? "player_enabled" : (state == 0 ? "player_disabled" : "server_disabled")))
        );
        return list.toArray(new String[0]);
    }

    public boolean serverEnabled() {
        if (!canLoad) return false;
        try {
            return FieldUtil.<Boolean>getObjectField(InvActionsServerConfig.getConfig(), getName());
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    public boolean playerEnabled(Player player) {
        if (!canLoad) return false;
        try {
            return FieldUtil.<Boolean>getObjectField(SettingsUtil.getConfig(player), getName());
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    public long getCooldown(Player player) {
        ValueUtil.mustTrue("玩家自身未启用该模块", playerEnabled(player));
        if (!cooldown.containsKey(player.getUniqueId())) {
            cooldown.put(player.getUniqueId(), 0L);
        }
        return cooldown.get(player.getUniqueId());
    }

    public boolean isEndedCooldown(Player player, long cooldown) {
        if (getCooldown(player) + cooldown < System.currentTimeMillis()) {
            this.cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            return true;
        }
        return false;
    }
}

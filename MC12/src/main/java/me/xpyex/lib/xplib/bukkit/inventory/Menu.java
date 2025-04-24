package me.xpyex.lib.xplib.bukkit.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import me.xpyex.lib.xplib.api.Pair;
import me.xpyex.lib.xplib.bukkit.inventory.button.Button;
import me.xpyex.lib.xplib.util.value.ValueUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Menu {
    private static final HashMap<UUID, Menu> MENUS = new HashMap<>();
    private final HashMap<Pair<Integer, Integer>, Button> buttons = new HashMap<>();
    private final Player player;
    private final HashMap<Integer, InvBuilder> setters = new HashMap<>();
    private final HashMap<Integer, Inventory> pages = new HashMap<>();
    @Getter
    private int openingPage;

    /**
     * 构造函数
     *
     * @param player 该Menu会向谁打开
     */
    public Menu(Player player) {
        ValueUtil.notNull("Menu必须有一个持有者", player);
        this.player = player;
    }

    /**
     * 获取指定玩家所打开的Menu
     *
     * @param player 玩家
     * @return 玩家所打开的Menu，可为空
     */
    @Nullable
    public static Menu getOpeningMenu(Player player) {
        return MENUS.get(player.getUniqueId());
        //
    }

    /**
     * 当玩家关闭Menu时，应当调用此方法
     *
     * @param player 关闭Menu的玩家
     */
    protected static void closed(Player player) {
        MENUS.remove(player.getUniqueId());
        //
    }

    /**
     * 获取所有开启的Menu，以及开启它的玩家
     *
     * @return MenuMap
     */
    @NotNull
    public static HashMap<UUID, Menu> getMenus() {
        return MENUS;
        //
    }

    /**
     * 设定当前Menu的某页应以什么setter构建
     *
     * @param page   页码数
     * @param setter 构造器
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public Menu setPage(int page, InvBuilder setter) {
        this.setters.put(page, setter);
        return this;
    }

    /**
     * 设定当前Menu的某页的相关信息
     *
     * @param page  页码数
     * @param title 界面标题
     * @param size  容量
     * @return 返回自身，创建链式代码
     */
    public Menu setPage(int page, String title, int size) {
        ArrayList<String> pattern = new ArrayList<>();
        {
            for (int i = 0; i < size; i++) {
                pattern.add("         ");
            }
        }
        return setPage(page, new InvBuilder(title, pattern.toArray(new String[0])).setSign(" ", Material.AIR));
    }

    /**
     * 设定该Menu中某页的InvSetter中的符号所对应的ItemStack
     *
     * @param sign  符号
     * @param stack 所对应的ItemStack
     * @param page  页数
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public Menu setSign(String sign, ItemStack stack, int page) {
        setters.get(page).setSign(sign, stack);
        return this;
    }

    /**
     * 设定该Menu中某页的InvSetter中的符号所对应的Button
     *
     * @param sign   符号
     * @param button 所对应的Button
     * @param page   页数
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public Menu setSign(String sign, Button button, int page) {
        setters.get(page).setSign(sign, button);
        return this;
    }

    /**
     * 获取打开该Menu的玩家
     *
     * @return 打开该Menu的玩家
     */
    @NotNull
    public Player getPlayer() {
        return player;
        //
    }

    /**
     * 设置该Menu中某页的某格为Button
     *
     * @param page   页数
     * @param slot   Button位置
     * @param button Button
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public Menu setButton(int page, int slot, Button button) {
        if (button != null) {
            buttons.put(Pair.of(page, slot), button);
        }
        return this;
    }

    /**
     * 设置该Menu中某页的某格为Button
     *
     * @param setter 某页的InvSetter
     * @param slot   Button位置
     * @param button Button
     * @return 返回自身，创建链式代码
     */
    @NotNull
    public Menu setButton(InvBuilder setter, int slot, Button button) {
        if (setter != null && button != null) {
            for (Integer k : setters.keySet()) {
                if (setters.get(k) == setter) {
                    setButton(k, slot, button);
                    break;
                }
            }
        }
        return this;
    }

    /**
     * 向打开该Menu的玩家更新Menu
     */
    public void updateInventory() {
        if (getPlayer().getOpenInventory().getTopInventory() == getPlayer().getInventory()) {  //没在Menu中
            return;
        }
        buttons.forEach((pair, button) -> {
            if (pair.getKey() == getOpeningPage()) {
                pages.get(getOpeningPage()).setItem(pair.getValue(), button.getStack());
            }
        });
    }

    /**
     * 获取对应页码的Inventory
     *
     * @param page 页码
     * @return 对应的Inventory
     */
    public Inventory getInv(int page) {
        return setters.get(page).build();
        //
    }

    /**
     * 向玩家打开指定页码
     *
     * @param page 页码
     */
    public void open(int page) {
        Inventory out = getInv(page);
        player.openInventory(out);
        pages.put(page, out);
        updateInventory();
        this.openingPage = page;
        MENUS.put(player.getUniqueId(), this);
    }

    /**
     * 获取slot位置的Button
     *
     * @param slot 位置
     * @return Button
     */
    @Nullable
    public Button getButton(int slot) {
        Pair<Integer, Integer> pair = Pair.of(getOpeningPage(), slot);
        for (Pair<Integer, Integer> i : buttons.keySet()) {
            if (i.equals(pair)) {
                return buttons.get(i);
            }
        }
        return null;
    }
}

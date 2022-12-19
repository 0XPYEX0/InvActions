package me.xpyex.plugin.invactions.bukkit.command;

import com.google.gson.JsonObject;
import me.xpyex.plugin.invactions.bukkit.InvActions;
import me.xpyex.plugin.xplib.bukkit.inventory.InvSetter;
import me.xpyex.plugin.xplib.bukkit.inventory.Menu;
import me.xpyex.plugin.xplib.bukkit.inventory.button.UnmodifiableButton;
import me.xpyex.plugin.xplib.bukkit.util.config.ConfigUtil;
import me.xpyex.plugin.xplib.bukkit.util.config.GsonUtil;
import me.xpyex.plugin.xplib.bukkit.util.inventory.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HandleCmd implements CommandExecutor {
    public static final JsonObject DEFAULT_SETTINGS = new JsonObject();

    static {
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenTool", true);
        DEFAULT_SETTINGS.addProperty("ReplaceBrokenArmor", true);
        DEFAULT_SETTINGS.addProperty("AutoFarmer", true);
        DEFAULT_SETTINGS.addProperty("DefaultF", false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(InvActions.getInstance(), () -> {
            if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("InvActions.admin")) {
                    sender.sendMessage("你没有权限");
                    return;
                }
                ConfigUtil.reload(InvActions.getInstance());
                sender.sendMessage("重载完成");
                return;
            }
            if (sender instanceof Player) {
                ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId(), GsonUtil.parseStr(DEFAULT_SETTINGS), false);
                InvSetter setter = new InvSetter(((Player) sender), "F键整理-设定-" + sender.getName(), "#########", "#1234  A#", "#########");
                setter.setSign("#", ItemUtil.getItemStack(Material.BLACK_STAINED_GLASS_PANE, " "));
                setter.setSign(" ", Material.AIR);

                Menu menu = new Menu((Player) sender);
                menu.setPage(1, setter)
                    .setSign("1", new UnmodifiableButton(menu, ((player, clickType) -> {
                            JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId());  //不放到外面是为了实时更新
                            return o.get("ReplaceBrokenTool").getAsBoolean() ? 1 : 0;
                        }))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动补充道具", "&f当手中物品损坏/用尽时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());  //不放到外面是为了实时更新
                                          boolean futureMode = !o.get("ReplaceBrokenTool").getAsBoolean();
                                          o.addProperty("ReplaceBrokenTool", futureMode);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
                                      }))
                        , 1)

                    .setSign("2", new UnmodifiableButton(menu, ((player, clickType) -> {
                            JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId());  //不放到外面是为了实时更新
                            return o.get("ReplaceBrokenArmor").getAsBoolean() ? 1 : 0;
                        }))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动穿戴盔甲", "&f当穿戴盔甲损坏时", "&f自动从背包补充", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());  //不放到外面是为了实时更新
                                          boolean futureMode = !o.get("ReplaceBrokenArmor").getAsBoolean();
                                          o.addProperty("ReplaceBrokenArmor", futureMode);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
                                      }))
                        , 1)

                    .setSign("3", new UnmodifiableButton(menu, ((player, clickType) -> {
                            JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId());  //不放到外面是为了实时更新
                            return o.get("AutoFarmer").getAsBoolean() ? 1 : 0;
                        }))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a自动收割", "&f当右键农作物时", "&f若成熟则自动收割", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());  //不放到外面是为了实时更新
                                          boolean futureMode = !o.get("AutoFarmer").getAsBoolean();
                                          o.addProperty("AutoFarmer", futureMode);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
                                      }))
                        , 1)
                    .setSign("4", new UnmodifiableButton(menu, ((player, clickType) -> {
                            JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + ((Player) sender).getUniqueId());  //不放到外面是为了实时更新
                            return o.get("DefaultF").getAsBoolean() ? 1 : 0;
                        }))
                                      .addMode(0, ItemUtil.getItemStack(Material.RED_WOOL, "&a按下 &e&lF &a整理", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &c禁用"))
                                      .addMode(1, ItemUtil.getItemStack(Material.LIME_WOOL, "&a按下 &e&lF &a整理", "&f不论是否潜行且低头", "&f按下 &e&lF &f就整理自身背包", "&5&o该设定不影响 &e&lShift+F &5&o整理容器", "", "&f当前状态: &a启用"))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          JsonObject o = ConfigUtil.getConfig(InvActions.getInstance(), "players/" + player.getUniqueId());  //不放到外面是为了实时更新
                                          boolean futureMode = !o.get("DefaultF").getAsBoolean();
                                          o.addProperty("DefaultF", futureMode);
                                          ConfigUtil.saveConfig(InvActions.getInstance(), "players/" + player.getUniqueId(), GsonUtil.parseStr(o), true);
                                      }))
                        , 1)
                    .setSign("A", new UnmodifiableButton(menu, ((player, clickType) -> {
                            return player.hasPermission("InvActions.admin") ? 1 : 0;
                        }))
                                      .addMode(1, ItemUtil.getItemStack(Material.ORANGE_WOOL, "&6重载所有玩家的配置文件"))
                                      .addMode(0, new ItemStack(Material.AIR))
                                      .setClickEffect(((player, clickType, itemStack) -> {
                                          Bukkit.getScheduler().runTask(InvActions.getInstance(), () -> {
                                              Bukkit.dispatchCommand(player, "sortItems reload");
                                          });
                                      }))
                        , 1);
                Bukkit.getScheduler().runTask(InvActions.getInstance(), () -> {
                    menu.open(1);
                });
            }
        });
        return true;
    }
}

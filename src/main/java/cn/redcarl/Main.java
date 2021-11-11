package cn.redcarl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    public final PluginManager pm;

    ArrayList<Player> cooldownd3 = new ArrayList<>();

    public Main() {
        this.pm = Bukkit.getServer().getPluginManager();
    }

    public void onEnable() {
        this.pm.registerEvents(this, (Plugin)this);
        autobroadcast();
    }

    @EventHandler
    public void DegenerationRate(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID)
                e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation());
        }
    }

    public void autobroadcast() {
        final String[] messages = { ChatColor.GRAY + "欢迎来到 " + ChatColor.DARK_AQUA + ChatColor.BOLD + "你" + ChatColor.YELLOW + ChatColor.BOLD +
                "的" + ChatColor.GRAY + ChatColor.BOLD + "故事 " + ChatColor.GRAY + "群组服务器!",
                ChatColor.GRAY + "发现错误或BUG？ 请通知 " + ChatColor.LIGHT_PURPLE + "服主或其他管理员" + ChatColor.GRAY + "!",
                ChatColor.GRAY + "服务器网站 " + ChatColor.GREEN + "www.mcys.xyz",
                ChatColor.GRAY + "服务器赞助 - " + ChatColor.YELLOW + "/paslaugos" };
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player players : Bukkit.getOnlinePlayers()){
                    BarAPI.setMessage(players, Arrays.asList(messages).get((new Random()).nextInt(messages.length)));
                }
            }
        }.runTaskTimer(this,0,200);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        ItemStack itemStack = new ItemStack(Material.BOW);
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("");
        meta.setLore(Arrays.asList("如果你想传送..."));
        itemStack.setItemMeta(meta);
        ItemStack itemStack2 = new ItemStack(Material.COMPASS);
        ItemMeta meta2 = itemStack2.getItemMeta();
        meta2.setDisplayName("选择");
        itemStack2.setItemMeta(meta2);
        ItemStack itemStack3 = new ItemStack(Material.ARROW);
        ItemMeta meta3 = itemStack3.getItemMeta();
        meta3.setDisplayName("箭头");
        itemStack3.setItemMeta(meta3);
        ItemStack itemStack4 = new ItemStack(Material.EMERALD);
        ItemMeta meta4 = itemStack4.getItemMeta();
        meta4.setDisplayName("按鼠标右键");
        meta4.setLore(Arrays.asList("可以看到服务器服务"));
        itemStack4.setItemMeta(meta4);
        p.setHealth(20.0D);
        p.setSaturation(10.0F);
        p.setFoodLevel(20);
        p.getInventory().setItem(0, itemStack4);
        p.getInventory().setItem(1, itemStack);
        p.getInventory().setItem(4, itemStack2);
        p.getInventory().setItem(34, itemStack3);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Player p = event.getPlayer();
            try {
                if (p.getItemInHand().getType() == Material.COMPASS &&
                        p.getItemInHand().getItemMeta().getDisplayName().equals("服务器选择"))
                    p.chat("/serveriai");
                if (p.getItemInHand().getType() == Material.EMERALD &&
                        p.getItemInHand().getItemMeta().getDisplayName().equals("按鼠标右键"))
                    p.chat("/paslaugos");
            } catch (Exception exception) {}
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            try {
                if (event.getClickedBlock().getType().equals(Material.CHEST) || event.getClickedBlock().getType().equals(Material.BED_BLOCK) ||
                        event.getClickedBlock().getType().equals(Material.WORKBENCH) || event.getClickedBlock().getType().equals(Material.FURNACE) ||
                        event.getClickedBlock().getType().equals(Material.BURNING_FURNACE) || event.getClickedBlock().getType().equals(Material.BREWING_STAND) ||
                        event.getClickedBlock().getType().equals(Material.ANVIL) || event.getClickedBlock().getType().equals(Material.ENDER_CHEST) ||
                        event.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE))
                    event.setCancelled(true);
            } catch (Exception exception) {}
    }

    @EventHandler
    public void onProjHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow)e.getEntity();
            if (arrow.getShooter() instanceof Player)
                arrow.remove();
        }
    }

    @EventHandler
    public void onPlayerEnterPortal(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (this.cooldownd3.contains(player) && player.getLocation().getBlock().getType() != Material.PORTAL)
            this.cooldownd3.remove(player);
        if (!this.cooldownd3.contains(player) && player.getLocation().getBlock().getType() == Material.PORTAL) {
            this.cooldownd3.add(player);
            player.setVelocity(player.getLocation().getDirection().multiply(-5).setY(1));
            getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable() {
                public void run() {
                    if (player.getLocation().getBlock().getType() != Material.PORTAL)
                        player.chat("/serveriai");
                }
            },  6L);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.cooldownd3.contains(player))
            this.cooldownd3.remove(player);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("cc")) {
            if (sender.isOp()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < 100; i++)
                        p.sendMessage("");
                    p.sendMessage(ChatColor.YELLOW + "聊天窗口已被清除!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "你没有许可!");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("ccme")) {
            for (int i = 0; i < 100; i++)
                sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "您的聊天窗口已被清除!");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("vote")) {
            sender.sendMessage(ChatColor.YELLOW + "投票: " + ChatColor.RED + "http://www.mcys.xyz/vote/");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("web")) {
            sender.sendMessage(ChatColor.YELLOW + "网站: " + ChatColor.RED + "http://www.mcys.xyz/");
            return true;
        }
        return false;
    }
}

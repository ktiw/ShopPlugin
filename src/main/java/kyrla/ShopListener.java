package kyrla;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private void sendConfigMessage(Player player, String key, double price) {
        String message = ShopPlugin.getInstance().getConfig().getString(key);
        if (message == null) return;
        message = message.replace("%price%", String.valueOf(price));
        message = ChatColor.translateAlternateColorCodes('&', message);
        player.sendMessage(message);
    }

    // Логика ПОКУПКИ
    private boolean tryBuy(Player player, double cost) {
        if (ShopPlugin.econ.has(player, cost)) {
            ShopPlugin.econ.withdrawPlayer(player, cost);
            return true;
        }
        return false;
    }

    // Логика ПРОДАЖИ
    private boolean trySell(Player player, double price, Material material) {
        if (player.getInventory().contains(material)) {
            player.getInventory().removeItem(new ItemStack(material, 1));
            ShopPlugin.econ.depositPlayer(player, price);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() == null || !(e.getInventory().getHolder() instanceof ShopHolder)) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;

        Player player = (Player) e.getWhoClicked();
        Material type = e.getCurrentItem().getType();

        double buyPrice = 0.0;
        double sellPrice = 0.0;

        if (type == Material.DIAMOND) {
            buyPrice = ShopPlugin.getInstance().getConfig().getDouble("prices.diamond");
            sellPrice = ShopPlugin.getInstance().getConfig().getDouble("prices.diamond_sell");
        } else if (type == Material.GOLD_INGOT) {
            buyPrice = ShopPlugin.getInstance().getConfig().getDouble("prices.gold");
            sellPrice = ShopPlugin.getInstance().getConfig().getDouble("prices.gold_sell");
        } else {
            return;
        }

        if (e.isLeftClick()) {
            // --- ПОКУПКА ---
            if (tryBuy(player, buyPrice)) {
                player.getInventory().addItem(new ItemStack(type));
                sendConfigMessage(player, "messages.success", buyPrice);

                // ЗВУК что получилось
                // Аргументы: Локация, Звук, Громкость, Тональность (1.0 - обычная, 2.0 - писклявая)
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            } else {
                sendConfigMessage(player, "messages.no-money", buyPrice);

                // ЗВУК ОШИБКИ
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }

        } else if (e.isRightClick()) {
            // --- ПРОДАЖА ---
            if (trySell(player, sellPrice, type)) {
                sendConfigMessage(player, "messages.sell-success", sellPrice);

                // ЗВУК ПРОДАЖИ
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            } else {
                sendConfigMessage(player, "messages.no-item", 0);

                // ЗВУК ОШИБКИ
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        }
    }
}
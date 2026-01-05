package kyrla;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta; // <--- Нужно для работы с названием и описанием

import java.util.ArrayList;
import java.util.List;

public class ShopCommand implements CommandExecutor {

    // Метод Создает красивый предмет с названием и описанием
    private ItemStack createGuiItem(Material material, String name, double buyPrice, double sellPrice) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Создаем список строк для описания (Lore)
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Цена покупки: §c" + buyPrice + "$");
        lore.add("§7Цена продажи: §a" + sellPrice + "$");
        lore.add("");
        lore.add("§eЛКМ — Купить");
        lore.add("§eПКМ — Продать");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Только игроки могут использовать эту команду!");
            return true;
        }

        Player player = (Player) sender;


        Inventory gui = Bukkit.createInventory(new ShopHolder(), 9, "Магазин ресурсов");

        // Достаем цены из конфига, чтобы написать их на предмете
        double diamondBuy = ShopPlugin.getInstance().getConfig().getDouble("prices.diamond");
        double diamondSell = ShopPlugin.getInstance().getConfig().getDouble("prices.diamond_sell");

        double goldBuy = ShopPlugin.getInstance().getConfig().getDouble("prices.gold");
        double goldSell = ShopPlugin.getInstance().getConfig().getDouble("prices.gold_sell");


        gui.setItem(0, createGuiItem(Material.DIAMOND, "&bАлмаз", diamondBuy, diamondSell));

        gui.setItem(1, createGuiItem(Material.GOLD_INGOT, "&6Золотой слиток", goldBuy, goldSell));

        player.openInventory(gui);

        return true;
    }
}
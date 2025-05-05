package gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.slowRanks.SlowRanks;

import java.util.ArrayList;
import java.util.List;

public class RangShopGui {

    public void openRankShop(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§4§lRANGSHOP");

        for (String rank : SlowRanks.getInstance().getPluginConfig().getConfigurationSection("ranks").getKeys(false)) {
            String displayName = SlowRanks.getInstance().getPluginConfig().getString("ranks." + rank + ".name");
            List<String> lore = new ArrayList<>(SlowRanks.getInstance().getPluginConfig().getStringList("ranks." + rank + ".lore"));
            double price = SlowRanks.getInstance().getPluginConfig().getDouble("ranks." + rank + ".price");
            String itemName = SlowRanks.getInstance().getPluginConfig().getString("ranks." + rank + ".item");
            int slot = SlowRanks.getInstance().getPluginConfig().getInt("ranks." + rank + ".slot", -1);

            Material material;
            try {
                material = Material.valueOf(itemName.toUpperCase());
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Ungültiges Material für Rang: " + rank);
                continue;
            }

            ItemStack rankItem = new ItemStack(material);
            ItemMeta rankMeta = rankItem.getItemMeta();
            if (rankMeta != null) {
                rankMeta.setDisplayName(displayName);
                rankMeta.setLore(lore);
                rankItem.setItemMeta(rankMeta);
            }

            if (slot >= 0 && slot < gui.getSize()) {
                gui.setItem(slot, rankItem);
            } else {
                Bukkit.getLogger().warning("Ungültiger Slot für Rang: " + rank);
            }
        }

        ItemStack infoItem = new ItemStack(Material.BARRIER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName("§c§lRanginfos");
            infoItem.setItemMeta(infoMeta);
        }
        gui.setItem(49, infoItem);

        ItemStack placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        if (placeholderMeta != null) {
            placeholderMeta.setDisplayName(" ");
            placeholder.setItemMeta(placeholderMeta);
        }
        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null || gui.getItem(i).getType() == Material.AIR) {
                gui.setItem(i, placeholder);
            }
        }

        player.openInventory(gui);
    }
}
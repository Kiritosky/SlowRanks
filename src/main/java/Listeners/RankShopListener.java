package Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import plugin.slowRanks.SlowRanks;
import plugin.slowRanks.VaultHook;

public class RankShopListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§4§lRANGSHOP")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.getType() == Material.BARRIER  && clickedItem.getItemMeta().getDisplayName().equals("§c§lRanginfos")) {
                player.performCommand("rechte");
                return;
            }

            String rankName = clickedItem.getItemMeta() != null ? clickedItem.getItemMeta().getDisplayName() : "";
            for (String rank : SlowRanks.getInstance().getPluginConfig().getConfigurationSection("ranks").getKeys(false)) {
                if (rankName.equals(SlowRanks.getInstance().getPluginConfig().getString("ranks." + rank + ".name"))) {
                    double price = SlowRanks.getInstance().getPluginConfig().getDouble("ranks." + rank + ".price");
                    String luckPermsRank = SlowRanks.getInstance().getPluginConfig().getString("ranks." + rank + ".luckperms-rank");

                    if (hasRank(player, rank)) {
                        player.sendMessage("§f §aDu besitzt diesen Rang bereits.");
                        return;
                    }
                    if (!canAfford(player, price)) {
                        player.sendMessage("§f §cDu hast leider zu wenig §f auf deinem Kontostand.");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                        return;
                    }
                    if (!canPurchase(player, rank)) {
                        player.sendMessage("§f §cKaufe erst den §evorherigen §cRang §f");
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
                        return;
                    }

                    withdrawMoney(player, price);
                    player.sendMessage("§f §aErfolgreich Rang §cersteigert§7.");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                    if (luckPermsRank != null && !luckPermsRank.isEmpty()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent add " + luckPermsRank);
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent add " + rank);
                    }
                    return;
                }
            }
        }
    }

    private boolean hasRank(Player player, String rank) {
        return player.hasPermission("rank." + rank);
    }

    private boolean canAfford(Player player, double price) {
        return VaultHook.getEconomy().has(player, price);
    }

    private boolean canPurchase(Player player, String rank) {
        if (hasRank(player, rank)) {
            return false;
        }

        String[] ranks = SlowRanks.getInstance().getPluginConfig().getConfigurationSection("ranks").getKeys(false).toArray(new String[0]);
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i].equals(rank)) {
                if (i == 0) return true;
                String previousRank = ranks[i - 1];
                if (!hasRank(player, previousRank)) {
                    return false;
                }
                return true;
            }
        }
        throw new IllegalArgumentException("Rank not found: " + rank);
    }

    private void withdrawMoney(Player player, double amount) {
        VaultHook.getEconomy().withdrawPlayer(player, amount);
    }
}
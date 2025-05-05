package Command;

import gui.RangShopGui;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RangShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new RangShopGui().openRankShop(player);
            return true;
        }
        Player player = (Player) sender;
        sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
        player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1, 1);
        return false;
    }
}
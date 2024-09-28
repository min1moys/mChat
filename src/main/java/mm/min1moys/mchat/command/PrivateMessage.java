package mm.min1moys.mchat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эту команду могут использовать только игроки!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /" + label + " <игрок> <сообщение>");
            return true;
        }

        Player senderPlayer = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден или не в сети.");
            return true;
        }

        // Соединяем аргументы в сообщение
        String message = String.join(" ", args).substring(args[0].length()).trim();

        // Отправляем сообщение
        senderPlayer.sendMessage(ChatColor.GRAY + "Вы -> " + targetPlayer.getDisplayName() + ": " + message);
        targetPlayer.sendMessage(ChatColor.GRAY + senderPlayer.getDisplayName() + " -> Вам: " + message);

        return true;
    }
}

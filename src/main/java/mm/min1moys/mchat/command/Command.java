package mm.min1moys.mchat.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import mm.min1moys.mchat.configuration.Config;
import mm.min1moys.mchat.handler.ChatHandler;

public class Command implements CommandExecutor {
    private final ChatHandler chatHandler;

    public Command(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(chatHandler.formatMessage(Config.messages.getString("error.args")));
            return true;
        }

        if (sender instanceof ConsoleCommandSender || (sender.isOp() || sender.hasPermission("mchat.reload"))) {
            Config.reload();
            sender.sendMessage(chatHandler.formatMessage(Config.messages.getString("reloaded")));
        } else {
            sender.sendMessage(chatHandler.formatMessage(Config.messages.getString("error.permission")));
        }

        return true;
    }
}

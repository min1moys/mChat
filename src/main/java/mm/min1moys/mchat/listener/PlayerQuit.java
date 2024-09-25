package mm.min1moys.mchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import mm.min1moys.mchat.configuration.Config;
import mm.min1moys.mchat.handler.ChatHandler;
import mm.min1moys.mchat.handler.MessageType;

public class PlayerQuit implements Listener {
    private final ChatHandler chatHandler;

    public PlayerQuit(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Config.settings.getBoolean("quit")) {
            String message = Config.messages.getString("quit");
            if (message == null || message.isBlank()) {
                event.quitMessage(null);
            } else {
                event.quitMessage(chatHandler.formatMessage(MessageType.OTHER, event.getPlayer(), message));
            }
        } else {
            event.quitMessage(null);
        }

        if (Config.settings.getBoolean("mentions.enabled")) {
            ChatHandler.getInstance().updateTree();
        }
    }
}

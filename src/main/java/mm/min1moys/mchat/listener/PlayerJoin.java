package mm.min1moys.mchat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import mm.min1moys.mchat.configuration.Config;
import mm.min1moys.mchat.handler.ChatHandler;
import mm.min1moys.mchat.handler.MessageType;

public class PlayerJoin implements Listener {
    private final ChatHandler chatHandler;

    public PlayerJoin(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Config.settings.getBoolean("join")) {
            String message = Config.messages.getString("join");
            if (message == null || message.isBlank()) {
                event.joinMessage(null);
            } else {
                event.joinMessage(chatHandler.formatMessage(MessageType.OTHER, event.getPlayer(), message));
            }
        } else {
            event.joinMessage(null);
        }

        if (Config.settings.getBoolean("mentions.enabled")) {
            ChatHandler.getInstance().updateTree();
        }
    }
}

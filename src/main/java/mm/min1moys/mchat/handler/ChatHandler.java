package mm.min1moys.mchat.handler;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import mm.min1moys.mchat.mChat;
import mm.min1moys.mchat.configuration.Config;
import mm.min1moys.mchat.mention.AsciiTree;

import java.util.List;

/**
 * @author Mykola Murza
 * @author justADeni
 */
public class ChatHandler {
    private static ChatHandler instance = null;
    private final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private AsciiTree tree = null;

    private ChatHandler() {
    }

    public static ChatHandler getInstance() {
        if (instance == null) {
            instance = new ChatHandler();
        }
        return instance;
    }

    private static int square(int input) {
        return input * input;
    }

    public void updateTree() {
        tree = new AsciiTree(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }

    public void writeToGlobalChat(AsyncChatEvent event, Player player, String message) {
        event.viewers().forEach(recipient ->
                recipient.sendMessage(formatMessage(MessageType.GLOBAL, player, formatMentions(player, recipient, message))));
    }

    public void writeToLocalChat(AsyncChatEvent event, Player player, String message) {
        event.viewers().stream()
                .filter(audience -> audience instanceof Player && isPlayerHearLocalChat(player, (Player) audience)
                        || audience instanceof ConsoleCommandSender)
                .forEach(recipient ->
                        recipient.sendMessage(formatMessage(MessageType.LOCAL, player, formatMentions(player, recipient, message))));
    }

    public TextComponent formatMessage(MessageType type, Player player, String message) {
        String formatted = switch (type) {
            case GLOBAL -> Config.settings.getString("global-format");
            case LOCAL -> Config.settings.getString("local-format");
            case OTHER -> message;
        };

        if (mChat.papi) {
            return LEGACY.deserialize(PlaceholderAPI.setPlaceholders(player,
                    formatted.replace("%player%", player.getName()).replace("%message%", message)));
        } else {
            return LEGACY.deserialize(
                    formatted.replace("%player%", player.getName()).replace("%message%", message));
        }
    }

    public TextComponent formatMessage(String message) {
        return LEGACY.deserialize(message);
    }

    private boolean isPlayerHearLocalChat(Player player, Player viewer) {
        return viewer.getWorld().equals(player.getWorld()) &&
                viewer.getLocation().distanceSquared(player.getLocation()) <= square(Config.settings.getInt("radius"));
    }

    private String formatMentions(Player player, Audience recipient, String message) {
        if (recipient instanceof ConsoleCommandSender) {
            return message;
        }

        if (recipient == player) {
            return message;
        }

        if (!Config.settings.getBoolean("mentions.enabled")) {
            return message;
        }

        String formatted = message;
        int additional = 0;
        List<Integer> foundIndexes = tree.findMultiple(formatted);
        if (!foundIndexes.isEmpty()) {
            for (int index : foundIndexes) {
                int start = (index >> 16) + additional;
                int end = start + (short) (index);
                String word = formatted.substring(start, end);

                if (word.equals(player.getName())) {
                    continue;
                }

                if (!word.equals(((Player) recipient).getName())) {
                    continue;
                }

                if (Config.settings.getBoolean("mentions.highlight.enabled")) {
                    String replaced = Config.settings.getString("mentions.highlight.format").replace("%player%", word);
                    String intermediate = formatted.substring(0, start) + replaced + formatted.substring(end);

                    // We have to account for the fact that formatting shifts indexes around
                    additional += intermediate.length() - formatted.length();
                    formatted = intermediate;
                }

                if (Config.settings.getBoolean("mentions.sound.enabled")) {
                    float volume = (float) Config.settings.getDouble("mentions.sound.volume");
                    float pitch = (float) Config.settings.getDouble("mentions.sound.pitch");
                    String name = Config.settings.getString("mentions.sound.name");
                    Sound sound = Sound.sound(Key.key(name), Sound.Source.BLOCK, volume, pitch);
                    recipient.playSound(sound);
                }

                if (Config.settings.getBoolean("mentions.actionbar.enabled")) {
                    recipient.sendActionBar(LEGACY.deserialize(Config.messages.getString("actionbar")));
                }
            }
        }

        return formatted;
    }
}

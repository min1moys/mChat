package mm.min1moys.mchat.listener;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatClick implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        // Обрабатываем всех получателей сообщения
        for (Player recipient : event.getRecipients()) {
            String recipientName = recipient.getName();

            // Проверяем, есть ли имя игрока в сообщении
            if (message.contains(recipientName)) {
                // Заменяем имя на кликабельное сообщение
                String clickableMessage = message.replace(recipientName, ChatColor.UNDERLINE + recipientName + ChatColor.RESET);

                // Создаем TextComponent с кликабельным именем
                TextComponent clickComponent = new TextComponent(clickableMessage);
                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + recipientName + " "));

                // Отправляем сообщение
                recipient.spigot().sendMessage(clickComponent);
            }
        }
    }
}

package nl.hpfxd.pdlevent.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import nl.hpfxd.pdlevent.PdlEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("message|msg|tell|whisper|pm|t|w|m")
public class MessageCommand extends BaseCommand {
    @Dependency
    private PdlEvent pdlEvent;

    private Map<UUID, UUID> replyMap = new HashMap<>();

    @Default
    public void message(Player from, OnlinePlayer player, String message) {
        messagePlayer(from, player.getPlayer(), message);
    }

    @Private
    @Subcommand("reply")
    @CommandAlias("reply|r")
    public void reply(Player from, String message) {
        if (replyMap.containsKey(from.getUniqueId())) {
            Player player = pdlEvent.getServer().getPlayer(replyMap.get(from.getUniqueId()));

            if (player != null) {
                messagePlayer(from, player, message);
            } else {
                from.sendMessage("That player is no longer online.");
            }
        } else {
            from.sendMessage("There is no one to reply to.");
        }
    }

    private void messagePlayer(Player from, Player to, String message) {
        replyMap.put(from.getUniqueId(), to.getUniqueId());
        replyMap.put(to.getUniqueId(), from.getUniqueId());

        pdlEvent.getLogger().info("[PM] " + from.getName() + " > " + to.getName() + ": " + message);

        String msg = ChatColor.YELLOW + from.getName() + ChatColor.GRAY + " > " + ChatColor.YELLOW + to.getName() +
                ChatColor.WHITE + ": " + message;

        from.sendMessage(msg);
        to.sendMessage(msg);

        to.playSound(to.getLocation(), Sound.BURP, 1, 1);
    }
}

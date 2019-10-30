package nl.hpfxd.pdlevent.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import nl.hpfxd.pdlevent.PdlEvent;
import nl.hpfxd.pdlevent.util.GameState;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("event")
@CommandPermission("event.admin")
public class EventCommand extends BaseCommand {
    @Dependency private PdlEvent pdlEvent;

    @Subcommand("reload")
    public void reload() {
        pdlEvent.reloadConfig();
    }

    @Subcommand("game")
    public class Game extends BaseCommand {

        @Subcommand("start")
        public void start(CommandSender s) {
            s.sendMessage("Game state set to MIDGAME");
            pdlEvent.getGameManager().setGameState(GameState.MIDGAME);
        }

        @Subcommand("dragon")
        public void dragon(Player player) {
            pdlEvent.broadcast("The dragon has been spawned!");
            player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_DRAGON);
        }

        @Subcommand("respawn")
        public void respawn(OnlinePlayer player) {
            pdlEvent.getGameManager().getSpectators().put(player.getPlayer().getUniqueId(), 0);
        }

        @Subcommand("end")
        public class End extends BaseCommand {
            @Subcommand("unlock")
            public void unlock() {
                pdlEvent.setEndUnlocked(true);
                pdlEvent.broadcast("The end is unlocked!");
            }

            @Subcommand("lock")
            public void lock() {
                pdlEvent.setEndUnlocked(false);
                pdlEvent.broadcast("The end is locked!");
            }
        }
    }

    @Subcommand("teams")
    public class Teams extends BaseCommand {

        @Subcommand("list")
        @Default
        public void list(CommandSender s) {
            for (Team team : pdlEvent.getGameManager().getTeams()) {
                s.sendMessage(team.getColor() + "" + ChatColor.BOLD + team.getName());

                if (team.getPlayers().isEmpty()) {
                    s.sendMessage(ChatColor.GOLD + "  " + team.getName() + ChatColor.WHITE + " has no players.");
                } else {
                    for (UUID uuid : team.getPlayers()) {
                        OfflinePlayer player = pdlEvent.getServer().getOfflinePlayer(uuid);

                        if (player != null) {
                            s.sendMessage(ChatColor.WHITE + "  " + player.getName());
                        } else {
                            s.sendMessage(ChatColor.WHITE + "  Unknown");
                        }
                    }
                }
            }
        }

        @Subcommand("add")
        @CommandCompletion("@teams @players")
        public void add(CommandSender s, Team team, OnlinePlayer player) {
            Team currentTeam = Team.getPlayerTeam(player.getPlayer());
            if (currentTeam != null) {
                currentTeam.removePlayer(player.getPlayer());
            }
            team.addPlayer(player.getPlayer());
            player.getPlayer().getInventory().clear();
            player.getPlayer().teleport(team.getSpawn());
            s.sendMessage("Moved " + player.getPlayer().getName() + " to " + team.getColor() + team.getName());
        }
    }

    @Subcommand("chat")
    public class Chat extends BaseCommand {
        @Subcommand("broadcast")
        @CommandAlias("broadcast|bc")
        public void broadcast(CommandSender s, String message) {
            pdlEvent.broadcast(s.getName(), message);
        }

        @Subcommand("mute")
        public void mute(CommandSender s) {
            if (pdlEvent.isChatMuted()) {
                pdlEvent.setChatMuted(false);
                pdlEvent.broadcast(s.getName(), "Un-muted the chat.");
            } else {
                pdlEvent.setChatMuted(true);
                pdlEvent.broadcast(s.getName(), "Muted the chat.");
            }
        }
    }
}

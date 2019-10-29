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
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.UUID;

@CommandAlias("event")
@CommandPermission("event.admin")
public class EventCommand extends BaseCommand {
    @Dependency private PdlEvent pdlEvent;

    @Subcommand("game")
    public class Game {

        @Subcommand("start")
        public void start(CommandSender s) {
            s.sendMessage("Game state set to MIDGAME");
            pdlEvent.getGameManager().setGameState(GameState.MIDGAME);
        }
    }

    @Subcommand("teams")
    public class Teams {

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

            s.sendMessage("");
        }
    }

    @Subcommand("broadcast")
    @CommandAlias("broadcast|bc")
    public void broadcast(CommandSender s, String message) {
        pdlEvent.broadcast(s.getName(), message);
    }
}

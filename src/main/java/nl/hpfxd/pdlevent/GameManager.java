package nl.hpfxd.pdlevent;

import lombok.Getter;
import nl.hpfxd.pdlevent.util.GameState;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.*;

public class GameManager {
    private PdlEvent pdlEvent = PdlEvent.getInstance();
    @Getter private GameState gameState;
    @Getter private List<Team> teams = new ArrayList<>();
    @Getter private List<UUID> spectators = new ArrayList<>();
    @Getter private Map<UUID, Team> offlinePlayers = new HashMap<>();

    public void init() {
        gameState = GameState.WAITING;
        this.initTeams();
    }

    private void initTeams() {
        teams.add(new Team("Red", pdlEvent.getWorld().getHighestBlockAt(1000, 1000).getLocation(), ChatColor.RED));
        teams.add(new Team("Green", pdlEvent.getWorld().getHighestBlockAt(1000, -1000).getLocation(), ChatColor.GREEN));
        teams.add(new Team("Blue", pdlEvent.getWorld().getHighestBlockAt(-1000, -1000).getLocation(), ChatColor.BLUE));
        teams.add(new Team("Yellow", pdlEvent.getWorld().getHighestBlockAt(-1000, 1000).getLocation(), ChatColor.YELLOW));
        teams.add(new Team("Spectator", pdlEvent.getWorld().getHighestBlockAt(0, 0).getLocation(), ChatColor.GRAY));
    }

    public void setGameState(GameState gameState) {
        if (gameState == GameState.MIDGAME) {
            pdlEvent.broadcast("Game starting!");

            for (Player player : pdlEvent.getServer().getOnlinePlayers()) {
                Team team = Team.findTeamForPlayer(player);
                player.sendTitle(new Title(ChatColor.GOLD + "" + ChatColor.BOLD + "Event", ChatColor.WHITE + "The event is starting!"));
                player.sendMessage(ChatColor.WHITE + "You've been placed on the " + team.getColor() + team.getName() + " " + ChatColor.WHITE + "team!");
                team.addPlayer(player);
            }

            for (Team team : teams) {
                for (UUID uuid : team.getPlayers()) {
                    Player player = pdlEvent.getServer().getPlayer(uuid);

                    if (player != null) {
                        player.teleport(team.getSpawn());
                    }
                }
            }
        }
        this.gameState = gameState;
    }

    public void addPlayerSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player.getUniqueId());
    }

    public void addSpectator(Player player) {
        Team currentTeam = Team.getPlayerTeam(player);
        if (currentTeam != null) {
            currentTeam.removePlayer(player);
        }
        teams.get(4).addPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
    }
}

package nl.hpfxd.pdlevent.util;

import lombok.Getter;
import nl.hpfxd.pdlevent.PdlEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Team {
    private static PdlEvent pdlEvent = PdlEvent.getInstance();
    @Getter private String name;
    @Getter private ChatColor color;
    @Getter private Location spawn;
    @Getter private List<UUID> players = new ArrayList<>();

    public Team(String name, Location spawn, ChatColor color) {
        this.name = name;
        this.spawn = spawn.add(0.5, 0, 0.5);
        this.color = color;
    }

    public void addPlayer(Player player) {
        this.addPlayer(player.getUniqueId());
    }

    public void addPlayer(UUID uuid) {
        players.add(uuid);

        Player player = pdlEvent.getServer().getPlayer(uuid);
        if (player != null) {
            pdlEvent.getScoreboardHandler().setPlayerTeam(player, this);
        }
    }

    public void removePlayer(Player player) {
        this.removePlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public boolean isPlayerOnTeam(Player player) {
        return this.isPlayerOnTeam(player.getUniqueId());
    }

    public boolean isPlayerOnTeam(UUID uuid) {
        return players.contains(uuid);
    }

    public static Team getPlayerTeam(Player player) {
        for (Team team : pdlEvent.getGameManager().getTeams()) {
            if (team.getPlayers().contains(player.getUniqueId())) return team;
        }
        return null;
    }

    public static Team findTeamForPlayer(Player player) {
        List<Team> teams = new ArrayList<>(pdlEvent.getGameManager().getTeams());
        teams.remove(4); // spectator

        teams.sort(Comparator.comparingInt(a -> a.getPlayers().size()));

        return teams.get(0);
    }
}

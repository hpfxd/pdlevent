package nl.hpfxd.pdlevent.listeners;

import lombok.Getter;
import nl.hpfxd.pdlevent.PdlEvent;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler implements Listener {
    private PdlEvent pdlEvent = PdlEvent.getInstance();

    @Getter private List<org.bukkit.scoreboard.Team> teams = new ArrayList<>();
    private Scoreboard sb;

    public void init() {
        ScoreboardManager sbm = pdlEvent.getServer().getScoreboardManager();
        sb = sbm.getMainScoreboard();
        if (sb.getTeam("spectator") != null) {
            sb.getTeam("red").unregister();
            sb.getTeam("green").unregister();
            sb.getTeam("blue").unregister();
            sb.getTeam("yellow").unregister();
            sb.getTeam("spectator").unregister();
        }

        for (Team team : pdlEvent.getGameManager().getTeams()) {
            teams.add(createTeam(team));
        }
    }

    @SuppressWarnings("deprecation")
    public void setPlayerTeam(Player player, Team team) {
        org.bukkit.scoreboard.Team oldTeam = null;
        org.bukkit.scoreboard.Team newTeam = null;
        for (org.bukkit.scoreboard.Team sbt : teams) {
            pdlEvent.getLogger().info(team.getName());
            pdlEvent.getLogger().info(sbt.getName());
            if (sbt.getName().equalsIgnoreCase(team.getName())) {
                newTeam = sbt;
            } else if (sbt.hasPlayer(player)) {
                oldTeam = sbt;
            }
        }

        if (oldTeam != null) oldTeam.removePlayer(player);
        if (newTeam != null) newTeam.addPlayer(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        pdlEvent.getLogger().info("Setting scoreboard for " + player.getName());
        player.setScoreboard(sb);
    }

    private org.bukkit.scoreboard.Team createTeam(Team team) {
        org.bukkit.scoreboard.Team sbt = sb.registerNewTeam(team.getName().toLowerCase());
        sbt.setAllowFriendlyFire(false);
        sbt.setCanSeeFriendlyInvisibles(true);
        sbt.setNameTagVisibility(NameTagVisibility.ALWAYS);
        sbt.setDisplayName(team.getName());
        sbt.setPrefix(team.getColor() + "" + ChatColor.BOLD + team.getName().substring(0, 1) + " " + team.getColor());

        pdlEvent.getLogger().info(sbt.getName());

        return sbt;
    }
}

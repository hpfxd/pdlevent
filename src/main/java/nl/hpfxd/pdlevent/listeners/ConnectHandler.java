package nl.hpfxd.pdlevent.listeners;

import nl.hpfxd.pdlevent.GameManager;
import nl.hpfxd.pdlevent.PdlEvent;
import nl.hpfxd.pdlevent.util.GameState;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectHandler implements Listener {
    private PdlEvent pdlEvent = PdlEvent.getInstance();
    private GameManager gm = pdlEvent.getGameManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (gm.getGameState() == GameState.WAITING) {
            player.teleport(pdlEvent.getSpawn());
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
        } else if (gm.getGameState() == GameState.MIDGAME) {
            if (gm.getOfflinePlayers().containsKey(player.getUniqueId())) {
                Team team = gm.getOfflinePlayers().get(player.getUniqueId());

                team.addPlayer(player);
            } else {
                gm.addSpectator(player);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (gm.getGameState() == GameState.MIDGAME) {
            Team team = Team.getPlayerTeam(player);

            if (team != null) {
                gm.getOfflinePlayers().put(player.getUniqueId(), team);
                team.removePlayer(player);
            }
        }
    }
}

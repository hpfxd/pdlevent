package nl.hpfxd.pdlevent;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import nl.hpfxd.pdlevent.commands.EventCommand;
import nl.hpfxd.pdlevent.listeners.ConnectHandler;
import nl.hpfxd.pdlevent.listeners.ScoreboardHandler;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PdlEvent extends JavaPlugin {
    @Getter private static PdlEvent instance;
    @Getter private GameManager gameManager;
    @Getter private World world;
    @Getter private ScoreboardHandler scoreboardHandler;

    @Override
    public void onEnable() {
        instance = this;
        world = this.getServer().getWorld("world");
        gameManager = new GameManager();
        scoreboardHandler = new ScoreboardHandler();

        PaperCommandManager commandManager = new PaperCommandManager(instance);

        commandManager.registerDependency(PdlEvent.class, instance);

        commandManager.getCommandCompletions().registerCompletion("teams", c -> ImmutableList.of("red", "green", "blue", "yellow", "spectator"));

        commandManager.getCommandContexts().registerContext(Team.class, (c) -> {
            String search = c.popFirstArg();

            if (search.equalsIgnoreCase("red")) {
                return this.gameManager.getTeams().get(0);
            } else if (search.equalsIgnoreCase("green")) {
                return this.gameManager.getTeams().get(1);
            } else if (search.equalsIgnoreCase("blue")) {
                return this.gameManager.getTeams().get(2);
            } else if (search.equalsIgnoreCase("yellow")) {
                return this.gameManager.getTeams().get(3);
            } else if (search.equalsIgnoreCase("spectator")) {
                return this.gameManager.getTeams().get(4);
            }
            return null;
        });

        commandManager.registerCommand(new EventCommand());

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new ConnectHandler(), instance);
        pm.registerEvents(scoreboardHandler, instance);

        gameManager.init();
        scoreboardHandler.init();
    }

    @Override
    public void onDisable() {
    }

    public void broadcast(String msg) {
        this.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[EVENT] " + ChatColor.RESET + msg);
    }

    public void broadcast(String player, String msg) {
        this.broadcast(ChatColor.BOLD + player + ChatColor.RESET + ": " + msg);
    }
}

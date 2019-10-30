package nl.hpfxd.pdlevent;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import me.missionary.board.BoardManager;
import me.missionary.board.settings.BoardSettings;
import me.missionary.board.settings.ScoreDirection;
import nl.hpfxd.pdlevent.commands.EventCommand;
import nl.hpfxd.pdlevent.commands.MessageCommand;
import nl.hpfxd.pdlevent.commands.TeamCommand;
import nl.hpfxd.pdlevent.listeners.*;
import nl.hpfxd.pdlevent.util.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PdlEvent extends JavaPlugin {
    @Getter private static PdlEvent instance;
    @Getter private GameManager gameManager;
    @Getter private World world;
    @Getter private ScoreboardHandler scoreboardHandler;
    @Getter
    private Location spawn;
    @Getter
    private BoardManager boardManager;
    @Getter
    @Setter
    private boolean endUnlocked = false;
    @Getter
    @Setter
    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        world = this.getServer().getWorld("world");
        WorldBorder border = world.getWorldBorder();
        border.setSize(101);
        spawn = world.getHighestBlockAt(0, 0).getLocation().add(0.5, 1, 0.5);
        gameManager = new GameManager();
        scoreboardHandler = new ScoreboardHandler();

        this.setupCommands();
        this.setupBoard();
        this.setupEvents();

        gameManager.init();
        scoreboardHandler.init();
    }

    @Override
    public void onDisable() {
    }

    private void setupBoard() {
        boardManager = new BoardManager(this, BoardSettings.builder()
                .boardProvider(new EventBoardProvider())
                .scoreDirection(ScoreDirection.UP)
                .build());
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(instance);

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
        commandManager.registerCommand(new TeamCommand());
        commandManager.registerCommand(new MessageCommand());
    }

    private void setupEvents() {
        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(new ConnectHandler(), instance);
        pm.registerEvents(scoreboardHandler, instance);
        pm.registerEvents(new ChatHandler(), instance);
        pm.registerEvents(new DeathHandler(), instance);
        pm.registerEvents(new DragonHandler(), instance);
        pm.registerEvents(new MiscHandler(), instance);
    }

    public void broadcast(String msg) {
        String m = ChatColor.GOLD + "" + ChatColor.BOLD + "[EVENT] " + ChatColor.RESET + msg;
        for (Player player : this.getServer().getOnlinePlayers()) {
            player.sendMessage(m);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        }
    }

    public void broadcast(String player, String msg) {
        this.broadcast(ChatColor.BOLD + player + ChatColor.RESET + ": " + msg);
    }
}

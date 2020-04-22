package pl.extollite.hidenseek;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import lombok.Getter;
import pl.extollite.hidenseek.command.admin.*;
import pl.extollite.hidenseek.command.user.*;
import pl.extollite.hidenseek.data.*;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;
import pl.extollite.hidenseek.listener.CancelListener;
import pl.extollite.hidenseek.listener.FormListener;
import pl.extollite.hidenseek.listener.GameListener;
import pl.extollite.hidenseek.manager.PlayerManager;

import java.util.*;

@Getter
public class HNS extends PluginBase {
    private static HNS instance;

    public static HNS getInstance() {
        return instance;
    }

    private Language language;
    private PlayerManager playerManager;
    private Leaderboard leaderboard;
    private List<Game> games = new LinkedList<>();
    private List<KitEntry> kits = new LinkedList<>();
    private Map<Player, Integer> openedWindows = new HashMap<>();

    private HNSACommand mainAdminCommand;
    private HNSCommand mainCommand;

    private PlaceholderAPI papi = PlaceholderAPI.getInstance();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;
        List<String> authors = this.getDescription().getAuthors();
        this.getLogger().info(TextFormat.DARK_GREEN + "Plugin by " + authors.get(0));

        ConfigData.load(this.getConfig());

        language = new Language();
        playerManager = new PlayerManager();
        ArenaData.init();
        leaderboard = new Leaderboard();

        this.getServer().getPluginManager().registerEvents(new GameListener(), this);
        this.getServer().getPluginManager().registerEvents(new CancelListener(), this);
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        loadCommands();
        registerPlaceholders();
    }

    @Override
    public void onDisable() {
        stopAll();
        instance = null;
        playerManager = null;
        language = null;
        leaderboard = null;
        this.getLogger().info(TextFormat.RED + "HNS has been disabled!");
    }

    public void stopAll() {
        ArrayList<Player> ps = new ArrayList<>();
        for (Game g : games) {
            g.cancelTasks();
            ps.addAll(g.getHiders());
            ps.addAll(g.getSeekers());
        }
        for (Player player : ps) {
            if (playerManager.hasPlayerData(player))
                playerManager.getPlayerData(player).getGame().leave(player, false);
        }
        games.clear();
    }

    public Game getGame(String name) {
        for (Game g : games) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }

    public static void checkGame(Game game, Player player) {
        if (game.getMaps() == null || game.getMaps().size() == 0) {
            HNSUtils.sendMessage(player, "&cYou need to setup map for arena!");
            return;
        } else if (game.getStatus() == Status.BROKEN) {
            HNSUtils.sendMessage(player, "&cYour arena is marked as broken! use &7/hns debug &c to check for errors!");
            HNSUtils.sendMessage(player, "&cIf no errors are found, please use &7/hns toggle " + game.getName() + "&c!");
            return;
        } else if (game.getS() == null) {
            HNSUtils.sendMessage(player, "&cYour lobby sign is invalid! Please reset them!");
            HNSUtils.sendMessage(player, "&cSet lobby sign: &7/hnsa setsign " + game.getName());
            return;
        } else if (game.getLobby() == null) {
            HNSUtils.sendMessage(player, "&cYour lobby is invalid! Please reset them!");
            HNSUtils.sendMessage(player, "&cSet lobby: &7/hnsa setlobby " + game.getName());
            return;
        } else if (game.getMaps().size() > 0) {
            for (MapEntry entry : game.getMaps()) {
                if (entry.getSpawnSeekers() == null) {
                    HNSUtils.sendMessage(player, "&cSet seekers spawn for Map: " + entry.getName());
                    return;
                } else if (entry.getSpawnHiders() == null) {
                    HNSUtils.sendMessage(player, "&cSet hiders spawn for Map: " + entry.getName());
                    return;
                }
            }
        }
        HNSUtils.sendMessage(player, "&aYour Hide'n'Seek arena is ready to run!");
        game.setStatus(Status.WAITING);
    }

    private void loadCommands() {
        mainAdminCommand = new HNSACommand();
        mainCommand = new HNSCommand();
        this.getServer().getCommandMap().register("hnsa", this.mainAdminCommand);
        this.getServer().getCommandMap().register("hns", mainCommand);
        if (ConfigData.only_main_commands) {
            mainCommand.registerCommand(new JoinCommand());
            mainCommand.registerCommand(new LeaveCommand());
            mainCommand.registerCommand(new ListCommand());
            mainCommand.registerCommand(new ListGamesCommand());
            mainCommand.registerCommand(new StatsCommand());

            mainAdminCommand.registerCommand(new CreateCommand());
            mainAdminCommand.registerCommand(new CreateMapCommand());
            mainAdminCommand.registerCommand(new AddSpawnCommand());
            mainAdminCommand.registerCommand(new SetSignCommand());
            mainAdminCommand.registerCommand(new SetExitCommand());
            mainAdminCommand.registerCommand(new DeleteCommand());
            mainAdminCommand.registerCommand(new ToggleCommand());
            mainAdminCommand.registerCommand(new ForceStartCommand());
            mainAdminCommand.registerCommand(new ForceStopCommand());
            mainAdminCommand.registerCommand(new SetLobbyCommand());
            mainAdminCommand.registerCommand(new KitCommand());
        } else {
            JoinCommand joinCommand = new JoinCommand();
            mainCommand.registerCommand(joinCommand);
            this.getServer().getCommandMap().register("hnsjoin", joinCommand);
            LeaveCommand leaveCommand = new LeaveCommand();
            mainCommand.registerCommand(leaveCommand);
            this.getServer().getCommandMap().register("hnsleave", leaveCommand);
            ListCommand listCommand = new ListCommand();
            mainCommand.registerCommand(listCommand);
            this.getServer().getCommandMap().register("hnslist", listCommand);
            ListGamesCommand listgamesCommand = new ListGamesCommand();
            mainCommand.registerCommand(listgamesCommand);
            this.getServer().getCommandMap().register("hnslistgames", listgamesCommand);
            StatsCommand statsCommand = new StatsCommand();
            mainCommand.registerCommand(statsCommand);
            this.getServer().getCommandMap().register("hnsstats", statsCommand);

            CreateCommand createCommand = new CreateCommand();
            mainAdminCommand.registerCommand(createCommand);
            this.getServer().getCommandMap().register("hnscreate", createCommand);
            CreateMapCommand createMapCommand = new CreateMapCommand();
            mainAdminCommand.registerCommand(createMapCommand);
            this.getServer().getCommandMap().register("hnscreatemap", createCommand);
            AddSpawnCommand addSpawnCommand = new AddSpawnCommand();
            mainAdminCommand.registerCommand(addSpawnCommand);
            this.getServer().getCommandMap().register("hnsaddspawn", addSpawnCommand);
            SetSignCommand setSignCommand = new SetSignCommand();
            mainAdminCommand.registerCommand(setSignCommand);
            this.getServer().getCommandMap().register("hnssetsign", setSignCommand);
            SetExitCommand setExitCommand = new SetExitCommand();
            mainAdminCommand.registerCommand(setExitCommand);
            this.getServer().getCommandMap().register("hnsextexit", setExitCommand);
            DeleteCommand deleteCommand = new DeleteCommand();
            mainAdminCommand.registerCommand(deleteCommand);
            this.getServer().getCommandMap().register("hnsdelete", deleteCommand);
            ToggleCommand toggleCommand = new ToggleCommand();
            mainAdminCommand.registerCommand(toggleCommand);
            this.getServer().getCommandMap().register("hnstoggle", toggleCommand);
            ForceStartCommand forceStartCommand = new ForceStartCommand();
            mainAdminCommand.registerCommand(forceStartCommand);
            this.getServer().getCommandMap().register("hnsforcestart", forceStartCommand);
            ForceStopCommand forceStopCommand = new ForceStopCommand();
            mainAdminCommand.registerCommand(forceStopCommand);
            this.getServer().getCommandMap().register("hnsforcestop", forceStopCommand);
            SetLobbyCommand setLobbyCommand = new SetLobbyCommand();
            mainAdminCommand.registerCommand(setLobbyCommand);
            this.getServer().getCommandMap().register("hnsextlobby", setLobbyCommand);
            KitCommand kitCommand = new KitCommand();
            mainAdminCommand.registerCommand(kitCommand);
            this.getServer().getCommandMap().register("hnskits", kitCommand);
        }
    }

    public void registerPlaceholders(){
        for(int i = 0; i < 10; i++){
            int finalI = i;
            papi.staticPlaceholder("hns_top_points_"+(finalI+1), () -> {
                if(leaderboard.getStatsScores(Leaderboard.Stats.POINTS).size() > finalI){
                    return leaderboard.getStatsScores(Leaderboard.Stats.POINTS).get(finalI);
                }
                return "";
            }, 1, true);
            papi.staticPlaceholder("hns_top_games_"+(finalI+1), () -> {
                if(leaderboard.getStatsScores(Leaderboard.Stats.GAMES).size() > finalI){
                    return leaderboard.getStatsScores(Leaderboard.Stats.GAMES).get(finalI);
                }
                return "";
            }, 1, true);
            papi.staticPlaceholder("hns_top_player_points_"+(finalI+1), () -> {
                if(leaderboard.getStatsPlayers(Leaderboard.Stats.POINTS).size() > finalI){
                    return leaderboard.getStatsPlayers(Leaderboard.Stats.POINTS).get(finalI);
                }
                return "";
            }, 1, true);
            papi.staticPlaceholder("hns_top_player_games_"+(finalI+1), () -> {
                if(leaderboard.getStatsPlayers(Leaderboard.Stats.GAMES).size() > finalI){
                    return leaderboard.getStatsPlayers(Leaderboard.Stats.GAMES).get(finalI);
                }
                return "";
            }, 1, true);
        }
        papi.visitorSensitivePlaceholder("player_points", player -> leaderboard.getStat(player, Leaderboard.Stats.POINTS));
        papi.visitorSensitivePlaceholder("player_curr_points", player -> leaderboard.getStat(player, Leaderboard.Stats.CURR_POINTS));
        papi.visitorSensitivePlaceholder("player_game_played", player -> leaderboard.getStat(player, Leaderboard.Stats.GAMES));
    }
}

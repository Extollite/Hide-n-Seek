package pl.extollite.hidenseek.data;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;

import java.util.*;

public class Leaderboard {

    private Config leaderboardConfig;
    private final Map<String, Integer> points;
    private final Map<String, Integer> curr_points;
    private final Map<String, Integer> gamesPlayed;

    private final List<String> sorted_players_points;
    private final List<String> sorted_scores_points;
    private final List<String> sorted_players_curr_points;
    private final List<String> sorted_scores_curr_points;
    private final List<String> sorted_players_gamesPlayed;
    private final List<String> sorted_scores_gamesPlayed;

    public Leaderboard() {
        points = new TreeMap<>();
        curr_points = new TreeMap<>();
        gamesPlayed = new TreeMap<>();
        sorted_players_points = new ArrayList<>();
        sorted_scores_points = new ArrayList<>();
        sorted_players_curr_points = new ArrayList<>();
        sorted_scores_curr_points = new ArrayList<>();
        sorted_players_gamesPlayed = new ArrayList<>();
        sorted_scores_gamesPlayed = new ArrayList<>();
        loadLeaderboard();
    }

    public void addStat(UUID uuid, Stats stat) {
        addStat(uuid, stat, 1);
    }

    public void addStat(UUID uuid, Stats stat, int amount) {
        Map<String, Integer> map;
        switch (stat) {
            case GAMES:
                map = this.gamesPlayed;
                break;
            case CURR_POINTS:
                map = this.curr_points;
                break;
            default:
                map = this.points;
        }
        if (map.containsKey(uuid.toString())) {
            map.replace(uuid.toString(), map.get(uuid.toString()) + amount);
        } else {
            map.put(uuid.toString(), amount);
        }
        saveLeaderboard();
    }

    public void addStat(Player player, Stats stat) {
        addStat(player, stat, 1);
    }

    public void addStat(Player player, Stats stat, int amount) {
        addStat(player.getUniqueId(), stat, amount);
    }

    public boolean removePoints(Player player, Stats stat, int amount) {
        if(curr_points.containsKey(player.getUniqueId().toString())){
            int points = curr_points.get(player.getUniqueId().toString());
            if(points >= amount){
                curr_points.put(player.getUniqueId().toString(), points-amount);
                return true;
            }
            return false;
        }
        return false;
    }

    public int getStat(Player player, Stats stat) {
        return getStat(player.getUniqueId(), stat);
    }

    public int getStat(UUID uuid, Stats stat) {
        Map<String, Integer> map;
        switch (stat) {
            case GAMES:
                map = this.gamesPlayed;
                break;
            case CURR_POINTS:
                map = this.curr_points;
                break;
            default:
                map = this.points;
        }
        return map.getOrDefault(uuid.toString(), 0);
    }

    public List<String> getStatsPlayers(Stats stat) {
        switch (stat) {
            case GAMES:
                return sorted_players_gamesPlayed;
            case CURR_POINTS:
                return sorted_players_curr_points;
            default:
                return sorted_players_points;
        }
    }

    public List<String> getStatsScores(Stats stat) {
        switch (stat) {
            case GAMES:
                return sorted_scores_gamesPlayed;
            case CURR_POINTS:
                return sorted_scores_curr_points;
            default:
                return sorted_scores_points;
        }
    }

    private void saveLeaderboard() {
        leaderboardConfig.set("Total-Points", points);
        leaderboardConfig.set("Current-Points", curr_points);
        leaderboardConfig.set("Games-Played", gamesPlayed);
        leaderboardConfig.save();
        sortScores(points, sorted_scores_points, sorted_players_points);
        sortScores(curr_points, sorted_scores_curr_points, sorted_players_curr_points);
        sortScores(gamesPlayed, sorted_scores_gamesPlayed, sorted_players_gamesPlayed);
    }

    private void loadLeaderboard() {
        leaderboardConfig = new Config(HNS.getInstance().getDataFolder()+"/leaderboard.yml", Config.YAML);
        getLeaderboard("Total-Points", points, sorted_scores_points, sorted_players_points);
        getLeaderboard("Current-Points", curr_points, sorted_scores_curr_points, sorted_players_curr_points);
        getLeaderboard("Games-Played", gamesPlayed, sorted_scores_gamesPlayed, sorted_players_gamesPlayed);
    }

    private void getLeaderboard(String path, Map<String, Integer> map, List<String> scores, List<String> players) {
        if (leaderboardConfig.exists(path)) {
            for (String key : leaderboardConfig.getSection(path).getKeys(false)) {
                map.put(key, leaderboardConfig.getInt(path + "." + key));
            }
            sortScores(map, scores, players);
        }
    }

    private void sortScores(Map<String, Integer> map, List<String> scores, List<String> players) {
        scores.clear();
        players.clear();
        for (Map.Entry<String, Integer> sortingMap : entriesSortedByValues(map)) {
            String player = HNS.getInstance().getServer().getOfflinePlayer(UUID.fromString(sortingMap.getKey())).getName();
            int score = sortingMap.getValue();
            scores.add(String.valueOf(score));
            players.add(player);
        }
    }

    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                (Map.Entry<K, V> e2, Map.Entry<K, V> e1) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    if (res == 0) return 1;
                    else return res;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    public enum Stats {
        POINTS("points"),
        CURR_POINTS("curr_points"),
        GAMES("games");

        private final String stat;

        Stats(String stat) {
            this.stat = stat;
        }

        public String getName() {
            return this.stat;
        }
    }

}
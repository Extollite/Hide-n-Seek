package pl.extollite.hidenseek.game;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.yescallop.essentialsnk.EssentialsAPI;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.extollite.hidenseek.entity.EntityBlock;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.*;
import pl.extollite.hidenseek.form.MapPickWindow;
import pl.extollite.hidenseek.hnsutils.HNSUtils;
import pl.extollite.hidenseek.manager.PlayerManager;
import pl.extollite.hidenseek.entity.FakeBlockEntity;
import pl.extollite.hidenseek.task.HideTask;
import pl.extollite.hidenseek.task.StartingTask;

import java.util.*;

@Getter
@Setter
@ToString
public class Game {

    private HNS plugin;
    private String name;
    private Language lang;
    private Location lobby;
    private MapEntry map;
    private List<Player> hiders = new ArrayList<>();
    private List<Player> seekers = new ArrayList<>();
    private Map<Vector3, Player> playersBlocks = new HashMap<>();
    private Map<Player, Integer> kills = new HashMap<>();
    private Map<Player, Block> blocks = new HashMap<>();
    private Map<Player, Entity> linkBlock = new HashMap<>();
    private Map<Player, Integer> timers = new HashMap<>();
    private List<MapEntry> maps;
    private Map<String, Integer> votes = new HashMap<>();
    private List<Player> voted = new LinkedList<>();

    private PlayerManager playerManager;
    private Status status;
    private int minPlayers;
    private int maxPlayers;
    private int time;
    private int startSeekers;
    private BlockEntitySign s;
    private int hideTime;
    private int chestRefillTime = 0;
    private Location exit;

    private HideTask hideTask;
    private StartingTask starting;
    private pl.extollite.hidenseek.task.TimerTask timer;

    public Game(String name, BlockEntitySign lobbySign, int timer, int minPlayers, int maxPlayers, int startSeekers, int hide, boolean isReady, List<MapEntry> maps, Location lobby) {
        this.plugin = HNS.getInstance();
        this.playerManager = plugin.getPlayerManager();
        this.lang = plugin.getLanguage();
        this.s = lobbySign;
        this.time = timer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.startSeekers = startSeekers;
        this.hideTime = hide;
        if (isReady) this.status = Status.READY;
        else this.status = Status.BROKEN;
        this.name = name;
        this.maps = maps;
        this.lobby = lobby;

        setLobbyBlock(lobbySign);
    }

    public Game(String name, int timer, int minPlayers, int maxPlayers, int startSeekers, int hide) {
        this.plugin = HNS.getInstance();
        this.playerManager = plugin.getPlayerManager();
        this.lang = plugin.getLanguage();
        this.time = timer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.startSeekers = startSeekers;
        this.hideTime = hide;
        this.status = Status.NOTREADY;
        this.name = name;
    }

/*    public void addKill(Player player) {
        this.kills.put(player, this.kills.get(player) + 1);
    }*/

    public StartingTask getStartingTask() {
        return this.starting;
    }

    public void setStatus(Status status) {
        this.status = status;
        updateLobbyBlock();
    }

    public void join(Player player) {
        UUID uuid = player.getUniqueId();
        if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN && status != Status.READY) {
            HNSUtils.sendMessage(player, getLang().getArena_not_ready());
        } else if (maxPlayers <= hiders.size()) {
            HNSUtils.sendMessage(player, getLang().getGame_full());
        } else if(status == Status.COUNTDOWN && starting.getTimer() <= 15){
            HNSUtils.sendMessage(player, getLang().getArena_not_ready());
        } else if (!hiders.contains(player)) {

            if (player.getRiding() != null) {
                player.dismountEntity(player.getRiding());
            }

            hiders.add(player);
            HNS.getInstance().getServer().getScheduler().scheduleDelayedTask(HNS.getInstance(), () -> {
                player.teleport(lobby);

                playerManager.addPlayerData(new PlayerData(player, this));
                heal(player);
                kills.put(player, 0);
                HNS.getInstance().getOpenedWindows().put(player, player.showFormWindow(new MapPickWindow(this, player)));

                player.getInventory().setItem(0, ConfigData.mapPick.clone());
                player.sendAllInventories();

                if (hiders.size() == 1 && status == Status.READY)
                    status = Status.WAITING;
                if (hiders.size() >= minPlayers && (status == Status.WAITING || status == Status.READY)) {
                    startLobby();
                } else if (status == Status.WAITING) {
                    HNSUtils.broadcast(getLang().getPlayer_joined_game().replace("%player_name%",
                            player.getName()) + (minPlayers - hiders.size() <= 0 ? "!" : ":" +
                            getLang().getPlayers_to_start().replace("%amount%", String.valueOf((minPlayers - hiders.size())))), HNS.getInstance().getServer().getOnlinePlayers().values());
                }
                updateLobbyBlock();
            }, 5);
        }
    }

    public void startLobby() {
        status = Status.COUNTDOWN;
        starting = new StartingTask(this);
        updateLobbyBlock();
    }

    public void startHide() {
        status = Status.BEGINNING;
        updateLobbyBlock();
        hideTask = new HideTask(this);
    }


    public void startGame() {
        status = Status.RUNNING;
        timer = new pl.extollite.hidenseek.task.TimerTask(this, time);
        updateLobbyBlock();
    }

    public void msgAll(String message) {
        HNSUtils.broadcast(message, hiders);
        HNSUtils.broadcast(message, seekers);
    }

    public void tipAll(String message) {
        for (Player p : hiders) {
            HNSUtils.sendTip(p, message);
        }
        for (Player s : seekers) {
            HNSUtils.sendTip(s, message);
        }
    }

    public void titleAll(String message) {
        for (Player p : hiders) {
            HNSUtils.sendTitle(p, message);
        }
        for (Player p : seekers) {
            HNSUtils.sendTitle(p, message);
        }
    }

    public void actionBarAll(String message) {
        for (Player p : hiders) {
            HNSUtils.sendTip(p, message);
        }
        for (Player s : seekers) {
            HNSUtils.sendTip(s, message);
        }
    }

    private void updateLobbyBlock() {
/*        String[] lines = new String[4];
        lines[0] = HNSUtils.colorize(getLang().getLine_1());
        lines[1] = HNSUtils.colorize(getLang().getLine_2().replace("%status%", status.getName()));
        lines[2] = HNSUtils.colorize(getLang().getLine_3().replace("%players_count%", "" + (seekers.size()+hiders.size()) + "/" + maxPlayers));
        lines[3] = HNSUtils.colorize(getLang().getLine_4().replace("%arena%", getName()));
        s.setText(lines);*/
    }

    private void heal(Player player) {
        player.removeAllEffects();
        player.setHealth(20);
        player.getFoodData().setLevel(20);
        player.getFoodData().sendFoodLevel();
        HNS.getInstance().getServer().getScheduler().scheduleDelayedTask(HNS.getInstance(), player::extinguish, 1);
    }

    public void freeze(Player player) {
        player.setGamemode(Player.SURVIVAL);
        player.setImmobile(true);
    }

    public void unFreeze(Player player) {
        player.setImmobile(false);
    }

    public void setLobbyBlock(BlockEntitySign sign) {
/*        try {
            this.s = sign;
            String[] lines = new String[4];
            lines[0] = HNSUtils.colorize(getLang().getLine_1());
            lines[1] = HNSUtils.colorize(getLang().getLine_2().replace("%status%", status.getName()));
            lines[2] = HNSUtils.colorize(getLang().getLine_3().replace("%players_count%", "" + (hiders.size()+seekers.size()) + "/" + maxPlayers));
            lines[3] = HNSUtils.colorize(getLang().getLine_4().replace("%arena%", getName()));
            s.setText(lines);
        } catch (Exception e) {
            return;
        }
        if (ConfigData.globalExit != null) {
            this.exit = ConfigData.globalExit;
        } else {
            this.exit = Location.fromObject(s.getLevel().getSafeSpawn(), s.getLevel());
        }*/
    }

    public void cancelTasks() {
        if (timer != null) timer.stop();
        if (starting != null) starting.stop();
        if (hideTask != null) hideTask.stop();
    }

    public void stop(boolean stop) {
        List<UUID> win = new ArrayList<>();
        boolean seekersWon = false;
        cancelTasks();
        if(!stop){
            for (Player p : hiders) {
                heal(p);
                playerManager.getPlayerData(p).restore(p);
                playerManager.removePlayerData(p);
                win.add(p.getUniqueId());
                HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.POINTS, 1);
                HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.CURR_POINTS, 1);
                plugin.getLeaderboard().addStat(p, Leaderboard.Stats.GAMES);
                exit(p);
            }

            if(win.size() == 0){
                seekersWon = true;
                for (Player p : seekers) {
                    heal(p);
                    playerManager.getPlayerData(p).restore(p);
                    playerManager.removePlayerData(p);
                    win.add(p.getUniqueId());
                    HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.POINTS, 1);
                    HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.CURR_POINTS, 1);
                    plugin.getLeaderboard().addStat(p, Leaderboard.Stats.GAMES);
                    exit(p);
                }
            }
        }

        hiders.clear();
        seekers.clear();

        playersBlocks.clear();
        String winner = String.join(", ", HNSUtils.convertUUIDListToStringList(win));
        if (seekersWon && !win.isEmpty())
            HNSUtils.broadcast(getLang().getGame_seekers_won().replace("%arena%", getName()).replace("%winner%", winner), HNS.getInstance().getServer().getOnlinePlayers().values());
        else if(!win.isEmpty())
            HNSUtils.broadcast(getLang().getGame_hiders_won().replace("%arena%", getName()).replace("%winner%", winner), HNS.getInstance().getServer().getOnlinePlayers().values());

        status = Status.READY;
        updateLobbyBlock();
    }

    public void leave(Player player, Boolean death) {
        UUID uuid = player.getUniqueId();
        unFreeze(player);
        if (death) {
            heal(player);
            if(hiders.contains(player)) {
                EssentialsAPI.getInstance().setVanished(player, false);
                Entity entity = linkBlock.remove(player);
                if(playersBlocks.containsKey(player.getLevelBlock())){
                    playersBlocks.remove(player.getLevelBlock());
                }
                if(entity instanceof FakeBlockEntity){
                    entity.despawnFromAll();
                    entity.close();
                } else if(entity != null){
                    entity.kill();
                }
                HNS.getInstance().getLeaderboard().addStat(player, Leaderboard.Stats.GAMES);
                hiders.remove(player);
                if(hiders.size() > 1){
                    seekers.add(player);
                    player.getInventory().clearAll();
                    player.teleport(map.getSpawnSeekers());
                    HNS.getInstance().getKits().get(0).giveKit(player);
                    player.getLevel().addSound(player.getPosition(), Sound.GAME_PLAYER_DIE, 1, 1);
                } else {
                    playerManager.getPlayerData(uuid).restore(player);
                    playerManager.removePlayerData(player);
                    exit(player);
                }
            }
           else {
                heal(player);
                player.getInventory().clearAll();
                HNS.getInstance().getKits().get(0).giveKit(player);
                player.sendAllInventories();
                player.teleport(map.getSpawnSeekers());
            }
        } else {
            heal(player);
            playerManager.getPlayerData(uuid).restore(player);
            playerManager.removePlayerData(player);
            hiders.remove(player);
            Entity entity = linkBlock.get(player);
            if(entity instanceof FakeBlockEntity){
                entity.despawnFromAll();
                entity.close();
            } else if(entity != null) {
                entity.kill();
            }
            playersBlocks.remove(player.getLevelBlock());
            seekers.remove(player);
            exit(player);
        }
        updateAfterDeath(player);
    }

    private void updateAfterDeath(Player player) {
        if (status == Status.RUNNING || status == Status.BEGINNING || status == Status.COUNTDOWN) {
            if (isGameOver()) {
                HNS.getInstance().getServer().getScheduler().scheduleDelayedTask(plugin, () -> {
                    stop(false);
                    updateLobbyBlock();
                }, 20);

            }
        } else if (status == Status.WAITING) {
            msgAll(getLang().getPlayer_left_game().replace("%player_name%", player.getName()) +
                    (minPlayers - hiders.size() - seekers.size() <= 0 ? "!" : ":" + getLang().getPlayers_to_start()
                            .replace("%amount%", String.valueOf((minPlayers -  hiders.size() - seekers.size())))));
        }
        updateLobbyBlock();
    }

    private boolean isGameOver() {
        return hiders.size() == 0 || seekers.size() == 0;
    }

    private void exit(Player player) {
        player.invulnerable = false;
        if (this.exit != null && this.getExit().getLevel() != null)
            player.teleport(this.getExit());
        else
            player.teleport(HNS.getInstance().getServer().getDefaultLevel().getSpawnLocation());
    }

    public void randomMap(){
        for(Player player : hiders){
            player.getInventory().clearAll();
            player.sendAllInventories();
        }
        int max = 0;
        String mapName = null;
        for(Map.Entry<String, Integer> entry : votes.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
                mapName = entry.getKey();
            }
        }
        for(MapEntry entry : maps){
            if(HNSUtils.colorize(entry.getName()).equals(mapName)){
                this.map = entry;
                break;
            }
        }
        if(map == null){
            map = maps.get(0);
        }
        votes.clear();
        voted.clear();
        msgAll(lang.getMap_won().replace("%name%", map.getName()).replace("%amount%", String.valueOf(max)));
    }

    public void teleportAll(){
        Collections.shuffle(hiders);
        int seekersSize = (hiders.size() > 1 ? startSeekers : 1);
        for(int i = 0; i < seekersSize; i++){
            seekers.add(hiders.remove(0));
        }

        for(Player hider : hiders){
            hider.getInventory().clearAll();
            hider.sendAllInventories();
            hider.teleport(new Location(map.getSpawnHiders().getX(), map.getSpawnHiders().getY()+0.5, map.getSpawnHiders().getZ(), map.getSpawnHiders().getLevel()));
            spawnFallBlock(hider);
            freeze(hider);
        }

        for(Player seeker : seekers){
            seeker.getInventory().clearAll();
            HNS.getInstance().getKits().get(0).giveKit(seeker);
            seeker.teleport(new Location(map.getSpawnSeekers().getX(), map.getSpawnSeekers().getY()+0.5, map.getSpawnSeekers().getZ(), map.getSpawnHiders().getLevel()));
            freeze(seeker);
        }


    }

    public void spawnFallBlock(Player player){
        Block block = blocks.get(player);
        if(block == null){
            blocks.put(player, map.getMapBlocks().values().iterator().next());
        }
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))

                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putInt("TileID", blocks.get(player).getId())
                .putByte("Data", blocks.get(player).getDamage());

        EntityBlock fall = new EntityBlock(player.getLevel().getChunk((int) player.x >> 4, (int) player.z >> 4), nbt, player);
        if (fall != null) {
            fall.spawnToAll();
        }
        linkBlock.put(player, fall);
        EssentialsAPI.getInstance().setVanished(player, true);
    }

    public void updateTimers(String timer){
        for(Player p : hiders){
            if(!timers.containsKey(p))
                timers.put(p, 0);
            int time = timers.get(p);
            if(time < ConfigData.standTime - 1) {
                time++;
                p.setExperience(0, ConfigData.standTime - time);
                p.sendExperience();
                timers.put(p, time);
                HNSUtils.sendTip(p, timer+"\n&eVisible &bHiders "+hiders.size()+" &6Seekers "+seekers.size());
            } else if(time == ConfigData.standTime - 1){
                playersBlocks.put(p.getLevelBlock(), p);
                Entity link = linkBlock.remove(p);
                if(link != null){
                    link.kill();
                }
                spawnFakeBlock(p);
                time++;
                p.setExperience(7, 0);
                p.sendExperience();
                timers.put(p, time);
            } else {
                HNSUtils.sendTip(p, timer+"\n&aHidden &bHiders "+hiders.size()+" &6Seekers "+seekers.size());
            }
        }
        for(Player p : seekers){
            HNSUtils.sendTip(p, timer+"\n&bHiders "+hiders.size()+" &6Seekers "+seekers.size());
            if(!timers.containsKey(p))
                timers.put(p, ConfigData.seekersPower);
            int time = timers.get(p);
            if(time < ConfigData.seekersPower) {
                time++;
                p.setExperience(0, time);
                p.sendExperience();
                timers.put(p, time);
            }
        }
    }

    void spawnFakeBlock(Player player){
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))

                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putInt("TileID", 0)
                .putByte("Data", 0);

        FakeBlockEntity fall = new FakeBlockEntity(player.getLevel().getChunk((int) player.x >> 4, (int) player.z >> 4), nbt, player, blocks.get(player));
        fall.spawnToAll();
        linkBlock.put(player, fall);
        playersBlocks.put(player.getLevelBlock(), player);
        player.getLevel().addSound(player, Sound.CONDUIT_ACTIVATE, 1, 1, player);
    }
}

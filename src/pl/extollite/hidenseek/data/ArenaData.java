package pl.extollite.hidenseek.data;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.game.Game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArenaData {
    public static void init(){
        Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
        if(arenas.exists("arenas")) {
            for(String arena : arenas.getSection("arenas").getKeys(false)) {
                BlockEntitySign lobbysign;
                int timer;
                int minplayers;
                int maxplayers;
                int seekers;
                int hide;
                timer = arenas.getInt("arenas." + arena + ".info.timer");
                minplayers = arenas.getInt("arenas." + arena + ".info.min-players");
                maxplayers = arenas.getInt("arenas." + arena + ".info.max-players");
                seekers = arenas.getInt("arenas." + arena + ".info.start-seekers");
                hide = arenas.getInt("arenas." + arena + ".info.hide-time");
                Level lobby = HNS.getInstance().getServer().getLevelByName(arenas.getString("arenas."+arena+".lobbysign.level"));
                lobbysign = (BlockEntitySign)lobby.getBlockEntity(new Vector3(arenas.getInt("arenas."+arena+".lobbysign.x"), arenas.getInt("arenas."+arena+".lobbysign.y"), arenas.getInt("arenas."+arena+".lobbysign.z")));
                List<MapEntry> maps = new LinkedList<>();
                for(String map : arenas.getSection("arenas."+arena+".map").getKeys(false)){
                    String name = arenas.getString("arenas."+arena+".map."+map+".name");
                    Location seekersLoc = new Location(arenas.getInt("arenas."+arena+".map."+map+".seekers.x"), arenas.getInt("arenas."+arena+".map."+map+".seekers.y"), arenas.getInt("arenas."+arena+".map."+map+".seekers.z"), 0, 0, HNS.getInstance().getServer().getLevelByName(arenas.getString("arenas."+arena+".map."+map+".seekers.level")));
                    Location hidersLoc = new Location(arenas.getInt("arenas."+arena+".map."+map+".hiders.x"), arenas.getInt("arenas."+arena+".map."+map+".hiders.y"), arenas.getInt("arenas."+arena+".map."+map+".hiders.z"), 0, 0, HNS.getInstance().getServer().getLevelByName(arenas.getString("arenas."+arena+".map."+map+".hiders.level")));
                    Map<String, Block> mapBlocks;
                    if(arenas.exists("arenas."+arena+".map."+map+".blocks")){
                        mapBlocks = new HashMap<>();
                        for(String block : arenas.getStringList("arenas."+arena+".map."+map+".blocks")){
                            String[] data = block.split(":");
                            try{
                                int id = Integer.parseInt(data[0]);
                                int meta = Integer.parseInt(data[1]);
                                mapBlocks.put(data[2], Block.get(id, meta).clone());
                            } catch (NumberFormatException e){
                                HNS.getInstance().getLogger().info("Unknown block: "+data[0]+ ":"+data[1]);
                            }
                        }
                    } else{
                        mapBlocks = ConfigData.standardBlocks;
                    }
                    MapEntry entry = new MapEntry(name, seekersLoc, hidersLoc, mapBlocks);
                    maps.add(entry);
                }
                Location lobbyLoc = new Location(arenas.getInt("arenas."+arena+".lobby.x"), arenas.getInt("arenas."+arena+".lobby.y"), arenas.getInt("arenas."+arena+".lobby.z"), HNS.getInstance().getServer().getLevelByName(arenas.getString("arenas."+arena+".lobby.level")));
                Game game = new Game(arena, lobbysign, timer, minplayers, maxplayers, seekers, hide, true, maps, lobbyLoc);
                HNS.getInstance().getGames().add(game);
            }
        }
    }
}

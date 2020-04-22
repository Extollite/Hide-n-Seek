package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Location;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.data.MapEntry;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class AddSpawnCommand extends CommandManager {

    public AddSpawnCommand() {
        super("hnsaaddspawn", "", "/hnsa addspawn <arena_name> <map_name> <type>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();

        parameters.put("addspawn", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
                new CommandParameter("Map Name", CommandParamType.STRING, false),
                new CommandParameter("Spawn type", false, new String[]{"seekers", "hiders"}),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.addspawn");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 3)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            Game g = HNS.getInstance().getGame(args[0]);
            Location loc = p.getLocation();
            if(g == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                return false;
            }
            MapEntry entry = null;
            for(MapEntry map : g.getMaps()){
                if(map.getName().equals(args[1])){
                    entry = map;
                    break;
                }
            }
            if(entry == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist().replace("Arena", "Map"));
                return false;
            }
            Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
            if(args[2].equals("seekers")){
                entry.setSpawnSeekers(p.getLocation());
            } else {
                entry.setSpawnHiders(p.getLocation());
            }

            arenas.set("arenas."+g.getName()+".map."+args[1]+"."+args[2]+".x", loc.getFloorX());
            arenas.set("arenas."+g.getName()+".map."+args[1]+"."+args[2]+".y", loc.getFloorY());
            arenas.set("arenas."+g.getName()+".map."+args[1]+"."+args[2]+".z", loc.getFloorZ());
            arenas.set("arenas."+g.getName()+".map."+args[1]+"."+args[2]+".level", loc.getLevel().getName());
            arenas.save();
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_spawn_set().replace("%spawn_name%", args[2]));
            HNS.checkGame(g, p);
        }
        return true;
    }
}

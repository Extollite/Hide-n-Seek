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

public class SetLobbyCommand extends CommandManager {

    public SetLobbyCommand() {
        super("hnsasetlobby", "", "/hnsa setlobby <arena_name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("setlobby", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.setlobby");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            Game g = HNS.getInstance().getGame(args[0]);
            Location loc = p.getLocation();
            if(g == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                return false;
            }
            Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
            arenas.set("arenas."+g.getName()+".lobby.x", loc.getFloorX());
            arenas.set("arenas."+g.getName()+".lobby.y", loc.getFloorY());
            arenas.set("arenas."+g.getName()+".lobby.z", loc.getFloorZ());
            arenas.set("arenas."+g.getName()+".lobby.level", loc.getLevel().getName());
            arenas.save();
            g.setLobby(loc);
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_lobby_set());
            HNS.checkGame(g, p);
        }
        return true;
    }
}

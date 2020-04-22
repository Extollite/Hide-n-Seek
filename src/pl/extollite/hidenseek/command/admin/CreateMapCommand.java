package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.Config;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.data.MapEntry;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CreateMapCommand extends CommandManager {

    public CreateMapCommand() {
        super("hnsacreatemap", "", "/hnsa createmap <name> <arena-name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("createmap", new CommandParameter[]{
                new CommandParameter("Map Name", CommandParamType.STRING, false),
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.createmap");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 2)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            Game g = HNS.getInstance().getGame(args[1]);
            if(g == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_stop_noexist());
                return true;
            }
            if(g.getMaps() == null){
                g.setMaps(new LinkedList<>());
            }
            Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
            arenas.set("arenas." + args[1] +".map."+args[0]+".name", args[0]);
            arenas.save();
            MapEntry entry = new MapEntry(args[0], ConfigData.standardBlocks);
            g.getMaps().add(entry);
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_create_created().replace("%arena%", args[1]));
        }
        return true;
    }
}

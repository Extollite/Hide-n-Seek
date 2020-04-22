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
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class CreateCommand extends CommandManager {

    public CreateCommand() {
        super("hnsacreate", "", "/hnsa create <name> <min-players> <max-players> <time> <start_hiders> <hide_time>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("create", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
                new CommandParameter("Min Players", CommandParamType.INT, false),
                new CommandParameter("Max Players", CommandParamType.INT, false),
                new CommandParameter("Time", CommandParamType.INT, false),
                new CommandParameter("Start Hiders", CommandParamType.INT, false),
                new CommandParameter("Hide time", CommandParamType.INT, false)
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.create");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 6)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            int min = 0;
            int max = 0;
            int time = 0;
            int seekers = 0;
            int hidetime = 0;
            try {
                min = Integer.parseInt(args[1]);
                max = Integer.parseInt(args[2]);
                time = Integer.parseInt(args[3]);
                seekers = Integer.parseInt(args[4]);
                hidetime = Integer.parseInt(args[5]);
            }
            catch(NumberFormatException e){
                return false;
            }
            if(min > max){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_create_minmax());
                return false;
            }
            Config arenas = new Config(HNS.getInstance().getDataFolder()+"/arenas.yml", Config.YAML);
            arenas.set("arenas." + args[0] +".info.timer", time);
            arenas.set("arenas." + args[0] +".info.min-players", min);
            arenas.set("arenas." + args[0] +".info.max-players", max);
            arenas.set("arenas." + args[0] +".info.start-seekers", seekers);
            arenas.set("arenas." + args[0] +".info.hide-time", hidetime);
            arenas.save();
            HNS.getInstance().getGames().add(new Game(args[0], time, min, max, seekers, hidetime));
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_create_created().replace("%arena%", args[0]));
        }
        return true;
    }
}

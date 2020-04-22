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
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class SetExitCommand extends CommandManager {

    public SetExitCommand() {
        super("hnsasetexit", "", "/hnsa setexit");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.setexit");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location loc = p.getLocation();
            Config config = HNS.getInstance().getConfig();
            config.set("settings.globalexit.enable", true);
            config.set("settings.globalexit.level", loc.getLevel().getName());
            config.set("settings.globalexit.x", loc.getX());
            config.set("settings.globalexit.y", loc.getY());
            config.save();
            ConfigData.load(HNS.getInstance().getConfig());
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_exit_set() + " " + loc.toString());
            for(Game g : HNS.getInstance().getGames())
                g.setExit(loc);
        }
        return true;
    }
}

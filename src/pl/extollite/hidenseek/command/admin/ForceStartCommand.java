package pl.extollite.hidenseek.command.admin;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ForceStartCommand extends CommandManager {

    public ForceStartCommand() {
        super("hnsaforcestart", "", "/hnsa forcestart <name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("forcestart", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.forcestart");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Game g = HNS.getInstance().getGame(args[0]);
            if (g == null) {
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                return false;
            }
            if (g.getStatus() == Status.WAITING || g.getStatus() == Status.READY) {
                g.startLobby();
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_start_starting().replace("%arena%", g.getName()));
            } else if (g.getStatus() == Status.COUNTDOWN) {
                if(g.getStartingTask().getTimer() > 15){
                    g.randomMap();
                }
                g.getStartingTask().stop();
                g.startHide();
                HNSUtils.sendMessage(p, "&aGame starting now");
            } else {
                HNSUtils.sendMessage(p, "&cGame has already started");
            }
        }
        return true;
    }
}

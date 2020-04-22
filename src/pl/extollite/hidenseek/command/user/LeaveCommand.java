package pl.extollite.hidenseek.command.user;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.HashMap;
import java.util.Map;

public class LeaveCommand extends CommandManager {

    public LeaveCommand() {
        super("hnsleave", "", "/hns leave");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.command.leave", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!HNS.getInstance().getPlayerManager().hasPlayerData(p)) {
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_leave_not_in_game());
                return true;
            }  else {
                Game g = HNS.getInstance().getPlayerManager().getData(p).getGame();
                if (g != null) {
                    g.leave(p, false);
                    HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_leave_left().replace("%arena%", g.getName()));
                }
            }
        }
        return true;
    }
}

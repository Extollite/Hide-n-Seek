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

public class JoinCommand extends CommandManager {

    public JoinCommand() {
        super("hnsjoin", "", "/hns join <name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("join", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.command.join", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (HNS.getInstance().getPlayerManager().hasPlayerData(p)) {
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_join_in_game());
            } else {
                Game g = HNS.getInstance().getGame(args[0]);
                if (g != null && !g.getHiders().contains(p)) {
                    g.join(p);
                } else {
                    HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                }
            }
        }
        return true;
    }
}

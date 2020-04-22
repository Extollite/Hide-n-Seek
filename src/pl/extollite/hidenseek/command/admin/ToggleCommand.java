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
import java.util.Map;

public class ToggleCommand extends CommandManager {

    public ToggleCommand() {
        super("hnsatoggle", "", "/hnsa toggle <name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("toggle", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.toggle");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if(sender instanceof Player){
            Player p = (Player)sender;
            Game g = HNS.getInstance().getGame(args[0]);
            if(g == null){
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                return false;
            }
            if(g.getStatus() == Status.NOTREADY || g.getStatus() == Status.BROKEN){
                g.setStatus(Status.READY);
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_toggle_unlocked().replace("%arena%", g.getName()));
            }
            else{
                g.stop(true);
                g.setStatus(Status.NOTREADY);
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_toggle_locked().replace("%arena%", g.getName()));
            }
        }
        return true;
    }
}

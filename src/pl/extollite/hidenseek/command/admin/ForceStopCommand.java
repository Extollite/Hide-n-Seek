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

public class ForceStopCommand extends CommandManager {

    public ForceStopCommand() {
        super("hnsaforcestop", "", "/hnsa forcestop <name>");
        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("forcestop", new CommandParameter[]{
                new CommandParameter("Arena Name", CommandParamType.STRING, false),
        });
        this.setCommandParameters(parameters);
        Permission permission = new Permission("hns.admin.command.forcestop");
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length < 1)
            return false;
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args[0].equalsIgnoreCase("all")) {
                for (Game game : HNS.getInstance().getGames()) {
                    Status status = game.getStatus();
                    if (status == Status.RUNNING || status == Status.WAITING || status == Status.BEGINNING || status == Status.COUNTDOWN) {
                        game.stop(true);
                    }
                }
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_stop_all());
                return true;
            }
            Game g = HNS.getInstance().getGame(args[0]);
            if (g == null) {
                HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_stop_noexist());
                return false;
            }
            g.stop(true);
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_stop_arena());
        }
        return true;
    }
}

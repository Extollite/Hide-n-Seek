package pl.extollite.hidenseek.command.user;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.TextFormat;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.command.CommandManager;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.*;

public class HNSCommand extends CommandManager {

    private final Map<String, Command> commands;

    public HNSCommand() {
        super("hns", "", "/hns <command>");

        this.setAliases(new String[]{"hidenseek"});
        Permission permission = new Permission("hns.command", null, Permission.DEFAULT_TRUE);
        HNS.getInstance().getServer().getPluginManager().addPermission(permission);
        this.setPermission(permission.getName());
        this.commands = new HashMap<>();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            HNSUtils.sendMessage(sender, HNS.getInstance().getLanguage().getCmd_no_permission());
            return false;
        }
        if (args.length < 1) {
            sendUsage(sender);
            return false;
        }
        Command cmd = this.commands.get(args[0]);
        if (cmd == null) {
            sendUsage(sender);
            return false;
        }
        String[] newArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        cmd.execute(sender, cmd.getName(), newArgs);
        return false;
    }

    private void updateArguments() {
        Map<String, CommandParameter[]> params = new HashMap<>();
        this.commands.forEach((k, v) -> {
            List<CommandParameter> p = new ArrayList<>();
            p.add(new CommandParameter(k, false, new String[]{k}));
            v.getCommandParameters().values().forEach(s -> p.addAll(Arrays.asList(s)));
            params.put(k, p.toArray(new CommandParameter[0]));
        });
        this.setCommandParameters(params);
    }

    public void registerCommand(Command command) {
        this.commands.put(command.getName().replace("hns", "").replace("hidenseek", "").toLowerCase(), command);
        this.updateArguments();
    }

    static public void sendUsage(CommandSender sender) {
        sender.sendMessage(TextFormat.GREEN + "-- HNS " + HNS.getInstance().getDescription().getVersion() + " Commands --");
        for(String message : HNS.getInstance().getLanguage().getCmd_usage()){
            sender.sendMessage(TextFormat.colorize('&', message));
        }
    }

}

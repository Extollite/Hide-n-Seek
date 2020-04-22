package pl.extollite.hidenseek.data;

import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import lombok.Getter;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.hnsutils.HNSUtils;
import pl.extollite.hidenseek.manager.ItemManager;

import java.util.*;

@Getter
public class ConfigData {
    public static boolean only_main_commands;

    public static Location globalExit;
    public static Item blockPick;
    public static Item mapPick;
    public static Item hidersItem;

    public static int hideTime;
    public static int seekersRoomSize;
    public static int standTime;
    public static int giveItemAfter;
    public static int seekersPower;

    public static Map<String, Block> standardBlocks = new HashMap<>();

    public static Map<String, Map.Entry<Integer, String>> shop = new HashMap<>();

    public static void load(Config config){

        only_main_commands = config.getBoolean("only-main-commands");

        hideTime = config.getInt("settings.hide-time");

        seekersRoomSize = config.getInt("settings.seekers-room-size");
        seekersRoomSize *= seekersRoomSize;

        standTime = config.getInt("settings.hiders-stand-time");

        for(String entry : config.getStringList("blocks")){
            String[] data = entry.split(":");
            try{
              int id = Integer.parseInt(data[0]);
              Integer meta = Integer.parseInt(data[1]);
              if(meta == 0)
                  meta = null;
              Block block = Block.get(id, meta).clone();
              standardBlocks.put(data[2], block);
            } catch (NumberFormatException e){
                HNS.getInstance().getLogger().info("Unknown block: "+data[0]+ ":"+data[1]);
            }
        }

        if(config.getBoolean("settings.globalexit.enable")){
            globalExit = new Location((float)config.getDouble("settings.globalexit.x"), (float)config.getDouble("settings.globalexit.y"), (float)config.getDouble("settings.globalexit.z"), HNS.getInstance().getServer().getLevelByName(config.getString("settings.globalexit.level")));
        }
        else
            globalExit = null;
        blockPick = new Item(config.getInt("blockPickItem.id"), config.getInt("blockPickItem.meta"), 1, HNSUtils.colorize(config.getString("blockPickItem.name")) );
        blockPick.setCustomName(config.getString("blockPickItem.name"));
        blockPick.setLore(config.getStringList("blockPickItem.lore").toArray(new String[0]));
        blockPick.setNamedTag(blockPick.getNamedTag().putInt("HNSID", 1));

        mapPick = new Item(config.getInt("mapPickItem.id"), config.getInt("mapPickItem.meta"), 1, HNSUtils.colorize(config.getString("mapPickItem.name")) );
        mapPick.setCustomName(config.getString("mapPickItem.name"));
        mapPick.setLore(config.getStringList("mapPickItem.lore").toArray(new String[0]));
        mapPick.setNamedTag(mapPick.getNamedTag().putInt("HNSID", 2));

        hidersItem = ItemManager.getItem(config.getString("hidersItem"), false).clone();
        giveItemAfter = config.getInt("give-item-after");

        seekersPower = config.getInt("seekers-power");

        for(String entry : config.getStringList("shop")){
            String[] splited = entry.split(":");
            int points = 0;
            try{
                points = Integer.parseInt(splited[1]);
            } catch (NumberFormatException e){
                HNS.getInstance().getLogger().info(e.toString());
            }
            shop.put(splited[0], new AbstractMap.SimpleImmutableEntry<>(points, splited[2]));
        }
        loadKits(config, "kits");
    }

    private static void loadKits(Config config, String path){
        for(String key : config.getSection(path).getKeys(false)){
            Item[] armorContetns = new Item[4];
            armorContetns[0] = ItemManager.getItem(config.getString(path+"."+key+".helmet"), false);
            armorContetns[1] = ItemManager.getItem(config.getString(path+"."+key+".chestplate"), false);
            armorContetns[2] = ItemManager.getItem(config.getString(path+"."+key+".leggings"), false);
            armorContetns[3] = ItemManager.getItem(config.getString(path+"."+key+".boots"), false);
            String permission = config.getString(path+"."+key+".permission");
            Map<Integer, Item> inventoryContents = new HashMap<>();
            for(String item : config.getStringList(path+"."+key+".inventory")){
                if(inventoryContents.size() >= 36)
                    break;
                inventoryContents.put(inventoryContents.size(), ItemManager.getItem(item, true));
            }
            List<Effect> effects = new LinkedList<>();
            for(String effectString : config.getStringList(path+"."+key+".effects")){
                String[] splitted = effectString.split(":");
                Effect effect = Effect.getEffectByName(splitted[0]).setDuration(Integer.parseInt(splitted[1])).setAmplifier(Integer.parseInt(splitted[2]));
                effects.add(effect);
            }
            String name = HNSUtils.colorize(config.getString(path+"."+key+".name"));
            KitEntry kitEntry = new KitEntry(inventoryContents, armorContetns, effects, permission, name);
            HNS.getInstance().getKits().add(kitEntry);
        }
    }
}

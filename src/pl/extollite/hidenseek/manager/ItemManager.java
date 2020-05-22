package pl.extollite.hidenseek.manager;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

/**
 * Manage item stacks for kits and chests
 */
public class ItemManager {
    public static Item getItem(String args, boolean isStackable) {
        if (args == null) return null;
        int amount = 1;
        if (isStackable) {
            String a = args.split(" ")[1];
            try{
                amount = Integer.parseInt(a);
            }
            catch (NumberFormatException ignored){

            }
        }
        Item item = itemStringToStack(args.split(" ")[0], amount);
        if (item == null) return null;

        String[] ags = args.split(" ");
        for (String s : ags) {
            if (s.startsWith("enchant:")) {
                s = s.replace("enchant:", "").toUpperCase();
                String[] d = s.split(":");
                int level = 1;
                if (d.length != 1) {
                    try{
                        level = Integer.parseInt(d[1]);
                    }
                    catch (NumberFormatException ignored){

                    }
                }
                for (Enchantment e : Enchantment.getEnchantments()) {
                    if (e.getName().equalsIgnoreCase(d[0])) {
                        item.addEnchantment(e.setLevel(level));
                    }
                }
            } else if (s.startsWith("color:")) {
                if (item instanceof ItemColorArmor) {
                    BlockColor color = getColor(s);
                    if(color != null)
                        ((ItemColorArmor) item).setColor(color);
                }
            } else if (s.startsWith("name:")) {
                s = s.replace("name:", "").replace("_", " ");
                s = HNSUtils.colorize(s);
                item.setCustomName(s);
            } else if (s.startsWith("lore:")) {
                s = s.replace("lore:", "").replace("_", " ");
                s = HNSUtils.colorize(s);
                String[] lore = s.split(":");
                item.setLore(lore);
            }
        }
        return item;
    }

    private static Item itemStringToStack(String item, int amount) {
        String oldPotion = item.toUpperCase();
        String[] itemArr = item.split(":");
        if (oldPotion.startsWith("POTION:") || oldPotion.startsWith("SPLASH_POTION:") || oldPotion.startsWith("LINGERING_POTION:")) {
            return getPotion(item);
        }
        try{
            int id = Integer.parseInt(itemArr[0]);
            return Item.get(id, 0, amount);
        }
        catch (NumberFormatException ignored){
            HNS.getInstance().getLogger().error(TextFormat.RED+"Unknown item id: "+item+", skipping!");
            return null;
        }
    }

    private static Item getPotion(String item) {
        String[] potionData = item.split(":");
        try{
            int id = Integer.parseInt(potionData[0]);
            return Item.get(id, Integer.parseInt(potionData[1]));
        }
        catch (NumberFormatException ignored){
            HNS.getInstance().getLogger().error(TextFormat.RED+"Unknown potion id: "+item+", skipping!");
            return null;
        }
    }

    private static BlockColor getColor(String colorString) {
        String dyeString = colorString.replace("color:", "");
        try {
            DyeColor dc = DyeColor.valueOf(dyeString.toUpperCase());
            return dc.getColor();
        } catch (Exception ignore) {
        }
        try {
            return new BlockColor(Integer.parseInt(dyeString));
        } catch (Exception ignore) {
        }
        return null;
    }

}

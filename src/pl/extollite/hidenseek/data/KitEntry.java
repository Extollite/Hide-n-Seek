package pl.extollite.hidenseek.data;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.potion.Effect;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class KitEntry {
    private Map<Integer, Item> inventoryContents;
    private Item[] armorContents;
    private List<Effect> effects;
    private String permission;
    private String name;

    public void giveKit(Player player){
        player.getInventory().clearAll();
        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);
        player.removeAllEffects();
        for(Effect effect : effects){
            player.addEffect(effect);
        }
        player.sendAllInventories();
    }
}

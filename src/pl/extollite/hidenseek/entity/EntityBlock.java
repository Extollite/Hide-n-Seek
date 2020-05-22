package pl.extollite.hidenseek.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.game.Game;

public class EntityBlock extends EntityFallingBlock {
    private final Player linked;
    public EntityBlock(FullChunk chunk, CompoundTag nbt, Player player) {
        super(chunk, nbt);
        this.onGround = true;
        this.setImmobile(true);
        this.linked = player;
        this.closed = false;
    }

    @Override
    protected float getGravity() {
        return 0.00f;
    }

    @Override
    protected float getDrag() {
        return 0.00f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.VOID){
            return super.attack(source);
        } else if(source.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) source;
            if(ev.getDamager().equals(linked))
                return false;
            if(ev.getDamager() instanceof Player){
                Player p = (Player) ev.getDamager();
                Game g = HNS.getInstance().getPlayerManager().getGame(p);
                if(g != null && g.getHiders().contains(p))
                    return false;
            }
            linked.getLevel().addSound(linked, Sound.GAME_PLAYER_HURT);
            return linked.attack(source);
        }
        return false;
    }

    @Override
    public boolean onUpdate(int currentTick) {

        if (closed) {
            return false;
        }

        this.timing.startTiming();

        int tickDiff = currentTick - lastUpdate;
        if (tickDiff <= 0 && !justCreated) {
            return true;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        updateMovement();

        this.timing.stopTiming();

        return hasUpdate;
    }

}

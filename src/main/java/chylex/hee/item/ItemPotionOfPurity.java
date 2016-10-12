package chylex.hee.item;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalCurseEntity;
import chylex.hee.item.base.ItemAbstractPotion;
import chylex.hee.mechanics.curse.ICurseCaller;
import chylex.hee.system.abstractions.entity.EntitySelector;

public class ItemPotionOfPurity extends ItemAbstractPotion{
	@Override
	public void applyEffectDrunk(ItemStack is, World world, EntityPlayer player){
		for(EntityTechnicalCurseEntity curse:EntitySelector.type(world, EntityTechnicalCurseEntity.class, player.boundingBox.expand(0.5D, 0.5D, 0.5D))){
			if (curse.compareTarget(player)){
				curse.setDead();
				curse.onPurify();
			}
		}
	}

	@Override
	public void applyEffectThrown(Entity entity, double dist){
		if (entity instanceof ICurseCaller){
			entity.setDead();
			((ICurseCaller)entity).onPurify();
		}
	}
}

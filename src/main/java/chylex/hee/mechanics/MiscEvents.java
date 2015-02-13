package chylex.hee.mechanics;
import java.util.Random;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import chylex.hee.entity.item.EntityItemDragonEgg;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemTransferenceGem;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MiscEvents{
	/*
	 * Dragon Egg entity join world
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent e){
		if (!e.world.isRemote && e.entity.getClass() == EntityItem.class && ((EntityItem)e.entity).getEntityItem().getItem() == Item.getItemFromBlock(Blocks.dragon_egg)){
			e.setCanceled(true);
			
			EntityItem newEntity = new EntityItemDragonEgg(e.world,e.entity.posX,e.entity.posY,e.entity.posZ,((EntityItem)e.entity).getEntityItem());
			newEntity.delayBeforeCanPickup = 10;
			
			newEntity.copyLocationAndAnglesFrom(e.entity);
			newEntity.motionX = newEntity.motionY = newEntity.motionZ = 0D;
			newEntity.addVelocity(e.entity.motionX,e.entity.motionY,e.entity.motionZ);
			e.world.spawnEntityInWorld(newEntity);
		}
	}
	
	/*
	 * Endermanpocalypse spawn immunity
	 */
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e){
		if (!e.entity.worldObj.isRemote && e.entity.hurtResistantTime > 1700 && e.entity.hurtResistantTime < 2000){
			e.entityLiving.hurtResistantTime = 0;
			e.setCanceled(true);
		}
	}
	
	/*
	 * Endermen dropping heads
	 * Silverfish dropping blood
	 * Mobs dropping Spectral Essence and dying next to Spectral Essence Altar
	 */
	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent e){
		if (e.entity.worldObj.isRemote /*|| SpectralEssenceHandler.handleMobDeath(e)*/ || !e.recentlyHit)return;
		
		ItemStack is = null;
		Random rand = e.entityLiving.getRNG();
		
		if (rand.nextInt(Math.max(1,40-e.lootingLevel)) == 0 && e.entity instanceof EntityEnderman){
			is = new ItemStack(ItemList.enderman_head);
		}
		else if (rand.nextInt(14-Math.min(e.lootingLevel,4)) == 0 && e.entity instanceof EntitySilverfish){
			boolean drop = rand.nextInt(4) == 0;
			boolean isPlayer = e.source.getEntity() instanceof EntityPlayer;
			
			if (!drop && isPlayer){
				ItemStack held = ((EntityPlayer)e.source.getEntity()).inventory.getCurrentItem();
				if (held != null && held.getItem() == Items.golden_sword)drop = true;
			}
			
			if (drop)is = new ItemStack(ItemList.silverfish_blood);
		}
		
		if (is != null){
			EntityItem item = new EntityItem(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ,is);
			item.delayBeforeCanPickup = 10;
			e.drops.add(item);
		}
	}
	
	/*
	 * Right-clicking on item frame, mob and item with Transference Gem
	 */
	@SubscribeEvent
	public void onPlayerInteractEntity(EntityInteractEvent e){
		if (e.entity.worldObj.isRemote)return;

		if (e.target instanceof EntityItemFrame){
			EntityItemFrame itemFrame = (EntityItemFrame)e.target;
			
			ItemStack is = itemFrame.getDisplayedItem();
			if (is == null || is.getItem() != ItemList.transference_gem || e.entityPlayer.isSneaking())return;
			else if (EnhancementHandler.hasEnhancement(is,TransferenceGemEnhancements.TOUCH)){
				itemFrame.setDisplayedItem(((ItemTransferenceGem)ItemList.transference_gem).tryTeleportEntity(is,e.entityPlayer,e.entityPlayer));
				e.setCanceled(true);
			}
		}
	}
}

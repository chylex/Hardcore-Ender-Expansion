package chylex.hee.mechanics;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemTransferenceGem;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiscEvents{
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
		else if (e.target instanceof EntityLivingBase && !(e.target instanceof IBossDisplayData)){
			ItemStack is = e.entityPlayer.inventory.getCurrentItem();
			
			if (is == null || is.getItem() != ItemList.transference_gem || !e.entityPlayer.isSneaking())return;
			else if (EnhancementHandler.hasEnhancement(is,TransferenceGemEnhancements.MOB)){
				((ItemTransferenceGem)ItemList.transference_gem).tryTeleportEntity(is,e.entityPlayer,e.target);
				e.setCanceled(true);
			}
		}
	}
}

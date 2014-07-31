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
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemTransferenceGem;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.UnlockResult;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
			for(EntityPlayer observer:ObservationUtil.getAllObservers(e.entityLiving,8D))KnowledgeRegistrations.ENDERMAN_HEAD.tryUnlockFragment(observer,1F);
		}
		else if (rand.nextInt(14-Math.min(e.lootingLevel,4)) == 0 && e.entity instanceof EntitySilverfish){
			boolean drop = rand.nextInt(4) == 0;
			boolean isPlayer = e.source.getEntity() instanceof EntityPlayer;
			
			if (!drop && isPlayer){
				ItemStack held = ((EntityPlayer)e.source.getEntity()).inventory.getCurrentItem();
				if (held != null && held.getItem() == Items.golden_sword)drop = true;
			}
			
			if (drop){
				is = new ItemStack(ItemList.silverfish_blood);
				for(EntityPlayer observer:ObservationUtil.getAllObservers(e.entity,8D)){
					if (KnowledgeRegistrations.SILVERFISH_BLOOD.tryUnlockFragment(observer,0.8F) == UnlockResult.NOTHING_TO_UNLOCK &&
						!KnowledgeRegistrations.INFESTATION_REMEDY.tryUnlockFragment(observer,0.5F).stopTrying){
						KnowledgeRegistrations.ENHANCED_BREWING_STAND.tryUnlockFragment(observer,0.4F,new byte[]{ 4 });
					}
				}
			}
			else if (isPlayer && KnowledgeRegistrations.SILVERFISH_BLOOD.tryUnlockFragment((EntityPlayer)e.source.getEntity(),0.65F) == UnlockResult.NOTHING_TO_UNLOCK){
				EntityPlayer player = (EntityPlayer)e.source.getEntity();
				
				if (!KnowledgeRegistrations.INFESTATION_REMEDY.tryUnlockFragment(player,0.5F).stopTrying){
					KnowledgeRegistrations.ENHANCED_BREWING_STAND.tryUnlockFragment(player,0.2F,new byte[]{ 4 });
				}
			}
		}
		// TODO else if (e.entity.dimension == 1 && rand.nextInt(100) == 0)is = new ItemStack(ItemList.essence,1,EssenceType.SPECTRAL.getItemDamage());
		
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
	
	/*
	 * Knowledge from picking up transformed items
	 */
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemPickup(EntityItemPickupEvent e){
		if (e.item.getClass() == EntityItemAltar.class){
			EntityItemAltar altarItem = (EntityItemAltar)e.item;
			if (altarItem.hasChanged && altarItem.essenceType == EssenceType.DRAGON.id)KnowledgeRegistrations.DRAGON_ESSENCE_ALTAR.tryUnlockFragment(e.entityPlayer,0.3F);
		}
		else if (e.item.getEntityItem().getItem() == ItemList.biome_compass){
			KnowledgeRegistrations.BIOME_COMPASS.tryUnlockFragment(e.entityPlayer,1F,new byte[]{ 0 }); // TODO wtf?
		}
	}
}

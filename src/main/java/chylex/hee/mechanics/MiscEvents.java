package chylex.hee.mechanics;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import chylex.hee.entity.projectile.EntityProjectileEyeOfEnder;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class MiscEvents{
	/*
	 * Eye of Ender throwing
	 * Ender Pearl throwing in creative mode
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onItemUse(PlayerInteractEvent e){
		if (e.action == Action.RIGHT_CLICK_AIR && !e.world.isRemote && e.entityPlayer.getHeldItem() != null){
			ItemStack held = e.entityPlayer.getHeldItem();
			World world = e.world;
			
			if (held.getItem() == Items.ender_pearl && e.entityPlayer.capabilities.isCreativeMode){
				world.playSoundAtEntity(e.entity,"random.bow",0.5F,0.4F/(world.rand.nextFloat()*0.4F+0.8F));
				world.spawnEntityInWorld(new EntityEnderPearl(world,e.entityLiving));
				e.setCanceled(true);
			}
			else if (held.getItem() == Items.ender_eye){
				world.playSoundAtEntity(e.entity,"random.bow",0.5F,0.4F/(world.rand.nextFloat()*0.4F+0.8F));
				world.playAuxSFXAtEntity(null,1002,(int)e.entity.posX,(int)e.entity.posY,(int)e.entity.posZ,0);
				
				if (!e.entityPlayer.capabilities.isCreativeMode)--held.stackSize;
				world.spawnEntityInWorld(new EntityProjectileEyeOfEnder(world,e.entity));
				
				e.setCanceled(true);
			}
		}
	}
	
	/*
	 * Silverfish dropping blood
	 */
	/* TODO @SubscribeEvent
	public void onLivingDrops(LivingDropsEvent e){
		if (e.entity.worldObj.isRemote || !e.recentlyHit)return;
		
		if (e.entity.getClass() == EntitySilverfish.class && e.entityLiving.getRNG().nextInt(14-Math.min(e.lootingLevel,4)) == 0){
			boolean drop = e.entityLiving.getRNG().nextInt(4) == 0;
			boolean isPlayer = e.source.getEntity() instanceof EntityPlayer;
			
			if (!drop && isPlayer){
				ItemStack held = ((EntityPlayer)e.source.getEntity()).inventory.getCurrentItem();
				if (held != null && held.getItem() == Items.golden_sword)drop = true;
			}
			
			if (drop){
				EntityItem item = new EntityItem(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ,new ItemStack(ItemList.silverfish_blood));
				item.delayBeforeCanPickup = 10;
				e.drops.add(item);
			}
		}
	}*/
	
	/*
	 * Right-clicking on item frame, mob and item with Transference Gem
	 */
	/* TODO @SubscribeEvent
	public void onPlayerInteractEntity(EntityInteractEvent e){
		if (e.entity.worldObj.isRemote)return;
		
		if (e.target instanceof EntityItemFrame){
			EntityItemFrame itemFrame = (EntityItemFrame)e.target;
			
			ItemStack is = itemFrame.getDisplayedItem();
			
			if (is == null || is.getItem() != ItemList.transference_gem || e.entityPlayer.isSneaking())return;
			else if (EnhancementHandler.hasEnhancement(is,TransferenceGemEnhancements.TOUCH)){
				is = ((ItemTransferenceGem)ItemList.transference_gem).tryTeleportEntity(is,e.entityPlayer,e.entityPlayer);
				ItemUtil.getTagRoot(is,false).removeTag("cooldown");
				
				itemFrame.setDisplayedItem(is);
				e.setCanceled(true);
			}
		}
	}*/
}

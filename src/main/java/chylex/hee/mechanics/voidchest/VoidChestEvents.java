package chylex.hee.mechanics.voidchest;
import java.util.Iterator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import chylex.hee.entity.technical.EntityTechnicalVoidChest;
import com.google.common.collect.Lists;

public final class VoidChestEvents{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new VoidChestEvents());
		PlayerVoidChest.register();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerDrops(PlayerDropsEvent e){
		if (!e.entity.worldObj.isRemote && e.entity.dimension == 1 && !e.drops.isEmpty()){
			if (e.entity.posY < -28D){
				InventoryVoidChest voidChest = PlayerVoidChest.getInventory(e.entityPlayer);
				
				for(Iterator<EntityItem> iter = e.drops.iterator(); iter.hasNext();){
					EntityItem entity = iter.next();
					
					if (entity.posY <= -32D){
						voidChest.putItemRandomly(entity.getEntityItem(),e.entity.worldObj.rand);
						iter.remove();
					}
				}
			}
			
			if (!e.drops.isEmpty()){
				for(EntityItem entity:e.drops)e.entity.worldObj.spawnEntityInWorld(entity);
				e.entity.worldObj.spawnEntityInWorld(new EntityTechnicalVoidChest(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ,(EntityPlayerMP)e.entityPlayer,e.drops));
				e.drops.clear();
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemToss(ItemTossEvent e){
		if (!e.entity.worldObj.isRemote && e.entity.dimension == 1){
			if (e.entity.posY <= -32D){
				PlayerVoidChest.getInventory(e.player).putItemRandomly(e.entityItem.getEntityItem(),e.entity.worldObj.rand);
				e.setCanceled(true);
			}
			else{
				e.entity.worldObj.spawnEntityInWorld(new EntityTechnicalVoidChest(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ,(EntityPlayerMP)e.player,Lists.newArrayList(e.entityItem)));
			}
		}
	}
	
	private VoidChestEvents(){}
}

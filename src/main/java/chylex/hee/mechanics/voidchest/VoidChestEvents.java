package chylex.hee.mechanics.voidchest;
import java.util.Iterator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import chylex.hee.entity.technical.EntityTechnicalVoidChest;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class VoidChestEvents{
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
						voidChest.putItemRandomly(entity.getEntityItem());
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
	
	private VoidChestEvents(){}
}

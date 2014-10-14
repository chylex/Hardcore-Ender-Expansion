package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemScorchingPickaxe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FXEvents{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new FXEvents());
	}
	
	@SubscribeEvent
	public void onGetBlockHardness(BreakSpeed e){
		ItemStack heldItem = e.entityPlayer.getHeldItem();
		
		if (heldItem != null && heldItem.getItem() == ItemList.scorching_pickaxe && ItemScorchingPickaxe.isBlockValid(e.block)){
			Random rand = e.entity.worldObj.rand;
			for(int fx = 0; fx < 5; fx++)HardcoreEnderExpansion.fx.flame(e.entity.worldObj,e.x-0.2D+rand.nextDouble()*1.4D,e.y-0.2D+rand.nextDouble()*1.4D,e.z-0.2D+rand.nextDouble()*1.4D,6);
		}
	}
	
	private FXEvents(){}
}

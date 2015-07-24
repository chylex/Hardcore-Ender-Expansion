package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemScorchingPickaxe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXEvents{
	private static final FXEvents instance = new FXEvents();
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	@SubscribeEvent
	public void onGetBlockHardness(BreakSpeed e){
		ItemStack heldItem = e.entityPlayer.getHeldItem();
		
		if (heldItem != null && heldItem.getItem() == ItemList.scorching_pickaxe && (ItemScorchingPickaxe.isBlockValid(e.block) || e.block == BlockList.ravaged_brick)){
			Random rand = e.entity.worldObj.rand;
			Minecraft mc = Minecraft.getMinecraft();
			for(int fx = 0; fx < 5-2*mc.gameSettings.particleSetting; fx++)HardcoreEnderExpansion.fx.flame(e.entity.worldObj,e.x-0.2D+rand.nextDouble()*1.4D,e.y-0.2D+rand.nextDouble()*1.4D,e.z-0.2D+rand.nextDouble()*1.4D,6);
		}
	}
	
	private FXEvents(){}
}

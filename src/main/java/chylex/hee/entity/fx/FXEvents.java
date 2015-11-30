package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemScorchingPickaxe;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FXEvents{
	private static final FXEvents instance = new FXEvents();
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	private final Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public void onGetBlockHardness(BreakSpeed e){
		ItemStack heldItem = e.entityPlayer.getHeldItem();
		
		if (heldItem != null && heldItem.getItem() == ItemList.scorching_pickaxe && (ItemScorchingPickaxe.isBlockValid(e.block) || e.block == BlockList.ravaged_brick)){
			Random rand = e.entity.worldObj.rand;
			Minecraft mc = Minecraft.getMinecraft();
			for(int fx = 0; fx < 5-2*mc.gameSettings.particleSetting; fx++)HardcoreEnderExpansion.fx.flame(e.x-0.2D+rand.nextDouble()*1.4D,e.y-0.2D+rand.nextDouble()*1.4D,e.z-0.2D+rand.nextDouble()*1.4D,6);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase == Phase.START || mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.dimension != 1)return;
		
		Random rand = mc.theWorld.rand;
		EntityPlayer player = mc.thePlayer;
		double voidFactor = EndTerritory.getVoidFactor(player);
		
		if (voidFactor > 0.25D){
			int factor = Math.min(150,MathUtil.floor(voidFactor*50F));
			double dist = 16D-Math.min(voidFactor*2D,8D);
			
			for(int a = 5+factor/5+rand.nextInt(1+factor); a > 0; a--){
				HardcoreEnderExpansion.fx.global("aura",player.posX+(rand.nextDouble()-0.5D)*dist,player.posY+(rand.nextDouble()-0.5D)*dist,player.posZ+(rand.nextDouble()-0.5D)*dist,0D,0D,0D);
			}
		}
	}
	
	private FXEvents(){}
}

package chylex.hee.entity.fx;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemScorchingPickaxe;
import chylex.hee.item.ItemTempleCaller;
import chylex.hee.mechanics.misc.TempleEvents;

@SideOnly(Side.CLIENT)
public class FXEvents{
	private static final FXEvents instance = new FXEvents();
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static void beginTempleSmoke(){
		instance.templeSmokeEnable = true;
	}
	
	@SubscribeEvent
	public void onGetBlockHardness(BreakSpeed e){
		ItemStack heldItem = e.entityPlayer.getHeldItem();
		
		if (heldItem != null && heldItem.getItem() == ItemList.scorching_pickaxe && (ItemScorchingPickaxe.isBlockValid(e.state) || e.state.getBlock() == BlockList.ravaged_brick)){
			Random rand = e.entity.worldObj.rand;
			Minecraft mc = Minecraft.getMinecraft();
			for(int fx = 0; fx < 5-2*mc.gameSettings.particleSetting; fx++)HardcoreEnderExpansion.fx.flame(e.entity.worldObj,e.pos.getX()-0.2D+rand.nextDouble()*1.4D,e.pos.getY()-0.2D+rand.nextDouble()*1.4D,e.pos.getZ()-0.2D+rand.nextDouble()*1.4D,6);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (templeSmokeEnable && e.phase == Phase.START && ++templeSmokeTimer == 4){
			templeSmokeTimer = 0;
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			
			if (player == null)templeSmokeEnable = false;
			else{
				Random rand = player.getRNG();
				
				for(int a = 0; a < 30; a++){
					player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE,player.posX+rand.nextDouble()-0.5D,player.posY,player.posZ+rand.nextDouble()-0.5D,0D,0D,0D);
					HardcoreEnderExpansion.fx.omnipresent(EnumParticleTypes.SMOKE_LARGE,player.worldObj,ItemTempleCaller.templeX+1.5D+rand.nextDouble()-0.5D,ItemTempleCaller.templeY+2.5D,ItemTempleCaller.templeZ+6.5D+rand.nextDouble()-0.5D,0D,0D,0D);
				}
				
				if (TempleEvents.isPlayerInTemple(player)){
					player.rotationYaw = -90;
					templeSmokeEnable = false;
				}
				else if (player.dimension != 1)templeSmokeEnable = false;
			}
		}
	}
	
	private boolean templeSmokeEnable = false;
	private byte templeSmokeTimer;
	
	private FXEvents(){}
}

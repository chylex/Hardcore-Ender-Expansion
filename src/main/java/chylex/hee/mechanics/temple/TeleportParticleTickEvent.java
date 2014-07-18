package chylex.hee.mechanics.temple;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.item.ItemTempleCaller;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class TeleportParticleTickEvent{
	private static TeleportParticleTickEvent instance;
	
	public static void register(){
		 FMLCommonHandler.instance().bus().register(instance = new TeleportParticleTickEvent());
	}
	
	public static void startSmokeEffect(){
		instance.doSmokeEffect = true;
	}
	
	private Minecraft mc;
	private boolean doSmokeEffect = false;
	private byte tickTimer;
	
	private TeleportParticleTickEvent(){
		this.mc = Minecraft.getMinecraft();
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e){
		if (e.phase != Phase.START || ++tickTimer < 4 || !doSmokeEffect)return;
		
		EntityPlayer player = mc.thePlayer;
		if (player == null)doSmokeEffect = false;
		else{
			Random rand = player.getRNG();
			for(int a = 0; a < 40; a++){
				player.worldObj.spawnParticle("largesmoke",player.posX+rand.nextDouble()-0.5D,player.posY,player.posZ+rand.nextDouble()-0.5D,0D,0D,0D);
			}
			if (player.posY >= ItemTempleCaller.templeY || player.dimension != 1){
				player.rotationYaw = -90;
				doSmokeEffect = false;
			}
		}
	}
}
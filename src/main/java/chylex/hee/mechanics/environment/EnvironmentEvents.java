package chylex.hee.mechanics.environment;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.TeleportHandler;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;

public final class EnvironmentEvents{
	public static void register(){
		GameRegistryUtil.registerEventHandler(new EnvironmentEvents());
	}
	
	private EnvironmentEvents(){}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e){
		if (e.phase != Phase.END || e.side != Side.SERVER || e.player.dimension != 1)return;
		
		double voidFactor = EndTerritory.getVoidFactor(e.player);
		
		if (voidFactor > 1.25D){
			int rapidDmg = Math.min(5,MathUtil.floor((voidFactor-1D)*3.5F));
			Damage.base(0.25F+(float)voidFactor*0.75F).setSource("outOfWorld").addModifiers(IDamageModifier.dealCreative,IDamageModifier.rapidDamage(rapidDmg)).deal(e.player);
		}
		else if (voidFactor > 0.5D && e.player.worldObj.getTotalWorldTime()%25L == 0){
			Damage.base(0.25F+(float)voidFactor*0.5F).setSource("outOfWorld").addModifiers(IDamageModifier.dealCreative).deal(e.player);
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent e){
		if (e.player.dimension == 1 && !e.player.worldObj.isRemote){
			e.player.timeUntilPortal = 10;
			TeleportHandler.movePlayerToSpawn((EntityPlayerMP)e.player,e.player.worldObj);
		}
	}
}

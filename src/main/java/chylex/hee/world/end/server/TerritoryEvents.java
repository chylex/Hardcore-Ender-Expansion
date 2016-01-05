package chylex.hee.world.end.server;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.TeleportHandler;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public final class TerritoryEvents{
	public static void register(){
		GameRegistryUtil.registerEventHandler(new TerritoryEvents());
	}
	
	private final TLongObjectHashMap<TerritoryTicker> activeTickers = new TLongObjectHashMap<>(8);
	private int tickLimiter;
	
	private TerritoryEvents(){}
	
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
	
	@SubscribeEvent
	public void onServerTick(WorldTickEvent e){
		if (e.phase != Phase.START || e.side != Side.SERVER || e.world.provider.dimensionId != 1)return;
		
		for(TerritoryTicker ticker:activeTickers.valueCollection()){
			ticker.onTick(e.world);
		}
		
		if (++tickLimiter == 10){
			tickLimiter = 0;
			TLongSet toDeactivate = new TLongHashSet(activeTickers.keySet());
			
			for(EntityPlayer player:EntitySelector.players(e.world)){
				Pair<Pos,EndTerritory> data = EndTerritory.findTerritoryCenter(player.posX,player.posZ);
				if (data == null)continue;
				
				final long hash = getTerritoryHash(data);
				
				if (activeTickers.containsKey(hash))toDeactivate.remove(hash);
				else activeTickers.put(hash,new TerritoryTicker(data.getRight(),data.getLeft(),hash));
			}
			
			toDeactivate.forEach(hash -> {
				activeTickers.remove(hash);
				return true;
			});
		}
	}
	
	private static final long getTerritoryHash(Pair<Pos,EndTerritory> data){
		return data.getLeft().offset(0,data.getRight().ordinal(),0).toLong();
	}
}

package chylex.hee.world.end.server;
import gnu.trove.impl.Constants;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.global.WorldFile;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C13TerritoryInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.damage.Damage;
import chylex.hee.system.abstractions.damage.IDamageModifier;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.TeleportHandler;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public final class TerritoryEvents{
	public static void register(){
		GameRegistryUtil.registerEventHandler(new TerritoryEvents());
	}
	
	private static AxisAlignedBB getTerritoryAABB(EndTerritory territory, Pos centerPos){ // TODO TEST THIS SEE BELOW
		return territory.createBoundingBox().offset(centerPos).toAABB().expand(EndTerritory.chunksBetween*8D,512D,EndTerritory.chunksBetween*8D);
	}
	
	private final TLongObjectHashMap<TerritoryTicker> activeTickers = new TLongObjectHashMap<>(8);
	private final TObjectLongHashMap<UUID> currentTerritory = new TObjectLongHashMap<>(8,Constants.DEFAULT_LOAD_FACTOR,Pos.at(0,4095,0).toLong());
	private final TLongHashSet rareTerritories = new TLongHashSet(2);
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
	public void onPlayerLogout(PlayerLoggedOutEvent e){ // TODO HOLY SHIT DON'T FORGET TO TEST THIS ON MULTIPLAYER
		EntityPlayer player = e.player;
		
		if (player.dimension == 1 && !player.worldObj.isRemote){
			Pair<Pos,EndTerritory> data = EndTerritory.findTerritoryCenter(player.posX,player.posZ); // TODO SERIOUSLY TEST THIS
			if (data == null)return;
			
			final long hash = data.getRight().getHashFromPoint(data.getLeft());
			
			System.out.println(getTerritoryAABB(data.getRight(),data.getLeft())); // TODO HERE'S A PRINTOUT, HOPE YOU DON'T FORGET TO SEARCH FOR THOSE BEFORE RELEASING ANYTHING
			
			if (rareTerritories.remove(hash) && EntitySelector.players(player.worldObj,getTerritoryAABB(data.getRight(),data.getLeft())).isEmpty()){
				currentTerritory.remove(player.getUniqueID());
				activeTickers.remove(hash);
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e){
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
				
				final UUID playerID = player.getUniqueID();
				final long hash = data.getRight().getHashFromPoint(data.getLeft());
				
				if (activeTickers.containsKey(hash))toDeactivate.remove(hash);
				else{
					if (SaveData.global(WorldFile.class).isTerritoryRare(hash)){
						rareTerritories.add(hash);
					}
					
					activeTickers.put(hash,new TerritoryTicker(data.getRight(),data.getLeft(),hash));
				}
				
				if (currentTerritory.get(playerID) != hash){
					currentTerritory.put(playerID,hash);
					PacketPipeline.sendToPlayer(player,new C13TerritoryInfo(data.getRight(),SaveData.global(WorldFile.class).getTerritoryVariations(hash)));
				}
			}
			
			toDeactivate.forEach(hash -> {
				activeTickers.remove(hash);
				
				if (rareTerritories.remove(hash)){
					// TODO destroy the territory
				}
				
				return true;
			});
		}
	}
}

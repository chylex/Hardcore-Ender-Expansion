package chylex.hee.entity.boss.dragon.managers;
import java.util.List;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DragonChunkManager implements LoadingCallback{
	private static DragonChunkManager instance;
	
	public static void register(){
		if (instance != null)throw new RuntimeException("Cannot register DragonChunkManager twice!");
		instance = new DragonChunkManager();
		MinecraftForge.EVENT_BUS.register(instance);
		ForgeChunkManager.setForcedChunkLoadingCallback(HardcoreEnderExpansion.instance,instance);
	}
	
	public static void ping(EntityBossDragon dragon){
		Ticket ticket = instance.ticket;
		
		if (ticket == null){
			ticket = instance.ticket = ForgeChunkManager.requestTicket(HardcoreEnderExpansion.instance,dragon.worldObj,Type.ENTITY);
			Log.debug("Requested chunkloading ticket on dragon load.");
			
			if (ticket != null){
				ticket.bindEntity(dragon);
				ticket.setChunkListDepth(9);
			}
		}
		
		if (ticket == null || --instance.timer >= 0)return;
		
		instance.timer = 4;
		
		if (dragon.chunkCoordX == instance.prevChunkX && dragon.chunkCoordZ == instance.prevChunkZ)return;
		
		instance.prevChunkX = dragon.chunkCoordX;
		instance.prevChunkZ = dragon.chunkCoordZ;
		
		Stopwatch.timeAverage("DragonChunkManager - ping update",10);
		
		for(int xx = dragon.chunkCoordX-1; xx <= dragon.chunkCoordX+1; xx++){
			for(int zz = dragon.chunkCoordZ-1; zz <= dragon.chunkCoordZ+1; zz++){
				ForgeChunkManager.forceChunk(ticket,new ChunkCoordIntPair(xx,zz));
			}
		}
		
		Stopwatch.finish("DragonChunkManager - ping update");
	}
	
	public static void release(EntityBossDragon dragon){
		if (instance.ticket == null)return;
		
		WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).setLastDragonChunk(dragon.chunkCoordX,dragon.chunkCoordZ);
		ForgeChunkManager.releaseTicket(instance.ticket);
		instance.ticket = null;
		instance.clear();
		Log.debug("Dragon requested releasing the chunkloading ticket.");
	}
	
	private Ticket ticket;
	private int prevChunkX = Integer.MAX_VALUE, prevChunkZ = Integer.MAX_VALUE;
	private int timer;
	
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world){
		Log.debug("Loaded dragon chunkloading tickets ($0).",tickets.size());
		
		if (!tickets.isEmpty()){
			ticket = tickets.get(0);
			
			if (ticket.getType() != Type.ENTITY || !(ticket.getEntity() instanceof EntityBossDragon)){
				Log.debug("Canceled loaded ticket (invalid).");
				ticket = null;
			}
			else{
				clear();
				ping((EntityBossDragon)ticket.getEntity());
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e){
		if (!e.world.isRemote && e.world.provider.dimensionId == 1){
			DragonSavefile file = WorldDataHandler.get(DragonSavefile.class);
			if (file.isDragonDead())return;
			
			ChunkCoordIntPair chunk = file.getLastDragonChunk();
			e.world.getChunkFromChunkCoords(chunk.chunkXPos,chunk.chunkZPos);
			
			int xx = chunk.chunkXPos*16, zz = chunk.chunkZPos*16;
			List<EntityBossDragon> list = e.world.getEntitiesWithinAABB(EntityBossDragon.class,AxisAlignedBB.getBoundingBox(xx,-32,zz,xx+16,512,zz+16));
			
			if (!list.isEmpty()){
				Log.debug("Loading dragon based on last stored chunk.");
				clear();
				ping(list.get(0));
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e){
		if (ticket != null && !e.world.isRemote && e.world.provider.dimensionId == 1){
			ticket = null;
			Log.debug("World unloaded, dereferencing dragon chunkloading ticket.");
		}
	}
	
	private void clear(){
		timer = 0;
		prevChunkX = prevChunkZ = Integer.MAX_VALUE;
	}
}

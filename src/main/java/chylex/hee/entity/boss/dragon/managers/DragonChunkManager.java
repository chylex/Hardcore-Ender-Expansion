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
	private static DragonChunkManager instance = null;
	private Ticket ticket;
	private byte timer;

	public static void register(){
		if (instance != null)throw new RuntimeException("Cannot register DragonChunkManager twice!");
		instance = new DragonChunkManager();
		MinecraftForge.EVENT_BUS.register(instance);
		ForgeChunkManager.setForcedChunkLoadingCallback(HardcoreEnderExpansion.instance,instance);
	}

	public static void ping(EntityBossDragon dragon){
		if (instance.ticket == null){
			instance.ticket = ForgeChunkManager.requestTicket(HardcoreEnderExpansion.instance,dragon.worldObj,Type.ENTITY);
			Log.debug("Requested chunkloading ticket on dragon load.");

			if (instance.ticket != null){
				instance.ticket.bindEntity(dragon);
				instance.ticket.setChunkListDepth(9);
			}
		}

		if (instance.ticket == null || --instance.timer >= 0)return;

		instance.timer = 4;

		Stopwatch.timeAverage("DragonChunkManager - ping update",10);

		for(int xx = dragon.chunkCoordX-1; xx <= dragon.chunkCoordX+1; xx++){
			for(int zz = dragon.chunkCoordZ-1; zz <= dragon.chunkCoordZ+1; zz++){
				ForgeChunkManager.forceChunk(instance.ticket,new ChunkCoordIntPair(xx,zz));
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

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world){
		Log.debug("Loaded dragon chunkloading tickets ($0).",tickets.size());

		if (!tickets.isEmpty()){
			instance.ticket = tickets.get(0);

			if (instance.ticket.getType() != Type.ENTITY || !(instance.ticket.getEntity() instanceof EntityBossDragon)){
				Log.debug("Canceled loaded ticket (invalid).");
				instance.ticket = null;
			}
			else{
				instance.clear();
				ping((EntityBossDragon)instance.ticket.getEntity());
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
				instance.clear();
				ping(list.get(0));
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload e){
		if (instance.ticket != null && !e.world.isRemote && e.world.provider.dimensionId == 1){
			instance.ticket = null;
			Log.debug("World unloaded, dereferencing dragon chunkloading ticket.");
		}
	}

	private void clear(){
		instance.timer = 0;
	}
}

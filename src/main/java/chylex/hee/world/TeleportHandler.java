package chylex.hee.world;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import chylex.hee.system.util.DragonUtil;

public class TeleportHandler extends Teleporter{
	public static final ChunkCoordinates endSpawn = new ChunkCoordinates(0,256,0);
	
	public static void toOverworld(EntityPlayerMP player){
		player.worldObj.removeEntity(player);
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player,0,new TeleportHandler(player.getServerForPlayer()));
		player.playerNetServerHandler.playerEntity = player.mcServer.getConfigurationManager().respawnPlayer(player,0,true);
	}
	
	public static void toEnd(EntityPlayerMP player){
		World end = MinecraftServer.getServer().worldServerForDimension(1);
		player.playerNetServerHandler.setPlayerLocation(endSpawn.posX+0.5D,DragonUtil.getTopBlockY(end,Blocks.end_stone,endSpawn.posX,endSpawn.posZ,70)+1D,endSpawn.posZ+0.5D,player.rotationYaw,0F);
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player,1,new TeleportHandler(player.getServerForPlayer()));
		// TODO check experience
	}
	
	private TeleportHandler(WorldServer world){
		super(world);
	}
	
	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float yaw){
		entity.setLocationAndAngles(x,y,z,yaw,0F);
		entity.motionX = entity.motionY = entity.motionZ = 0D;
	}
	
	@Override
	public boolean makePortal(Entity entity){ return true; }
	
	@Override
	public void removeStalePortalLocations(long time){}
}

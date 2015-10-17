package chylex.hee.world;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.StrongholdFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.DragonUtil;

public class TeleportHandler extends Teleporter{
	public static final ChunkCoordinates endSpawn = new ChunkCoordinates(0,256,0);
	
	public static void toOverworld(EntityPlayerMP player){
		Optional<Pos> lastPortal = SaveData.player(player,StrongholdFile.class).getPortalPos();
		
		if (lastPortal.isPresent()){
			Pos portalPos = lastPortal.get();
			player.playerNetServerHandler.setPlayerLocation(portalPos.getX()+0.5D,portalPos.getY(),portalPos.getZ()+0.5D,player.rotationYaw,0F);
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player,0,new TeleportHandler(player.getServerForPlayer()));
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player,0,new TeleportHandler(player.getServerForPlayer()));
			// hey server, I would actually like to teleport the player, like, genuinely teleport them, you cunt
		}
		else{
			player.worldObj.removeEntity(player);
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player,0,new TeleportHandler(player.getServerForPlayer()));
			player.playerNetServerHandler.playerEntity = player.mcServer.getConfigurationManager().respawnPlayer(player,0,true);
		}
	}
	
	public static void toEnd(EntityPlayerMP player){ // TODO vanilla achievements + changing their text a bit
		World end = MinecraftServer.getServer().worldServerForDimension(1);
		player.playerNetServerHandler.setPlayerLocation(endSpawn.posX+0.5D,DragonUtil.getTopBlockY(end,Blocks.end_stone,endSpawn.posX,endSpawn.posZ,70)+1D,endSpawn.posZ+0.5D,player.rotationYaw,0F);
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player,1,new TeleportHandler(player.getServerForPlayer()));
	}
	
	private TeleportHandler(WorldServer world){
		super(world);
	}
	
	@Override
	public void placeInPortal(Entity entity, double x, double y, double z, float yaw){
		entity.setLocationAndAngles(x,y,z,yaw,0F);
		entity.motionX = entity.motionY = entity.motionZ = 0D;
		entity.fallDistance = 0F;
		if (entity instanceof EntityPlayer)((EntityPlayer)entity).addExperienceLevel(0);
	}
	
	@Override
	public boolean makePortal(Entity entity){ return true; }
	
	@Override
	public void removeStalePortalLocations(long time){}
}

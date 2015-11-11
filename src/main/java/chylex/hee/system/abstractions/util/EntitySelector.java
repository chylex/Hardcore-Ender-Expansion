package chylex.hee.system.abstractions.util;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public final class EntitySelector{
	public static @Nonnull List<EntityPlayerMP> players(){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		return manager == null ? Collections.emptyList() : (List<EntityPlayerMP>)manager.playerEntityList;
	}
	
	public static @Nonnull List<EntityPlayer> players(World world){
		return world.playerEntities;
	}
	
	public static @Nonnull List<EntityPlayer> players(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(EntityPlayer.class,boundingBox);
	}
	
	public static @Nonnull List<Entity> any(World world){
		return world.loadedEntityList;
	}
	
	public static @Nonnull List<Entity> any(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(Entity.class,boundingBox);
	}
	
	public static @Nonnull List<Entity> living(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox);
	}
	
	public static @Nonnull List<EntityLiving> mobs(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(EntityLiving.class,boundingBox);
	}
	
	public static @Nonnull <T extends Entity> List<T> type(World world, Class<T> extending, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(extending,boundingBox);
	}
	
	private EntitySelector(){}
}

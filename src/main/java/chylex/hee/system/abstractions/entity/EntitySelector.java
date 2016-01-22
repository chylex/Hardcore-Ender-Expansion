package chylex.hee.system.abstractions.entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.system.collections.CollectionUtil;

public final class EntitySelector{
	public static @Nonnull List<EntityPlayerMP> players(){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		return manager == null ? Collections.emptyList() : (List<EntityPlayerMP>)manager.playerEntityList;
	}
	
	public static @Nonnull List<EntityPlayer> players(World world){
		return world.playerEntities;
	}
	
	public static @Nonnull List<EntityPlayer> players(World world, AxisAlignedBB boundingBox){
		List<EntityPlayer> foundPlayers = new ArrayList<>(1+world.playerEntities.size()/2);
		
		for(EntityPlayer player:players(world)){
			if (player.boundingBox.intersectsWith(boundingBox))foundPlayers.add(player);
		}
		
		return foundPlayers;
	}
	
	public static @Nonnull List<Entity> any(World world){
		return world.loadedEntityList;
	}
	
	public static @Nonnull List<Entity> any(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(Entity.class,boundingBox);
	}
	
	public static @Nonnull List<EntityLivingBase> living(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox);
	}
	
	public static @Nonnull List<EntityLiving> mobs(World world, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(EntityLiving.class,boundingBox);
	}
	
	public static @Nonnull <T extends Entity> List<T> type(World world, Class<T> extending, AxisAlignedBB boundingBox){
		return world.getEntitiesWithinAABB(extending,boundingBox);
	}
	
	public static @Nullable <T extends Entity> T closest(Entity targetEntity, List<T> entities){
		return CollectionUtil.min(entities,entity -> entity.isDead || entity == targetEntity ? Double.MAX_VALUE : targetEntity.getDistanceSqToEntity(entity)).orElse(null);
	}
	
	public static @Nullable <T extends Entity> T closest(Entity targetEntity, AxisAlignedBB boundingBox, IEntitySelectorMethod<T> selector){
		return closest(targetEntity,selector.select(targetEntity.worldObj,boundingBox));
	}
	
	public static @Nullable <T extends Entity> T closest(Entity targetEntity, Class<T> extending, AxisAlignedBB boundingBox){
		return closest(targetEntity,type(targetEntity.worldObj,extending,boundingBox));
	}
	
	public static interface IEntitySelectorMethod<T extends Entity>{
		List<T> select(World world, AxisAlignedBB boundingBox);
	}
	
	private EntitySelector(){}
}

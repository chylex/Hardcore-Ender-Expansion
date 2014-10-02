package chylex.hee.entity.item;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;

public class EntityItemDragonEgg extends EntityItem{
	private boolean overrideDeath = false;
	
	public EntityItemDragonEgg(World world){
		super(world);
	}

	public EntityItemDragonEgg(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		lifespan = Integer.MAX_VALUE;
		overrideDeath = false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		overrideDeath = true;
		return super.attackEntityFrom(source,amount);
	}
	
	@Override
	public void setDead(){
		if (overrideDeath){
			DragonSavefile file = WorldDataHandler.get(DragonSavefile.class);
			
			if (file.isDragonDead()){
				ChunkCoordinates coords = file.getPortalEggLocation();
				World endWorld = MinecraftServer.getServer().worldServerForDimension(1);
				
				if (endWorld == null)HardcoreEnderExpansion.notifications.report("Could not teleport Dragon Egg to the End, world is null.");
				else if (endWorld.getBlock(coords.posX,coords.posY,coords.posZ) != Blocks.dragon_egg){
					endWorld.setBlock(coords.posX,coords.posY,coords.posZ,Blocks.dragon_egg);
					PacketPipeline.sendToAllAround(this,64D,new C20Effect(FXType.Basic.DRAGON_EGG_RESET,this));
					PacketPipeline.sendToAllAround(endWorld.provider.dimensionId,coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D,64D,new C20Effect(FXType.Basic.DRAGON_EGG_RESET,coords.posX+0.5D,coords.posY+0.5D,coords.posZ+0.5D));
				}
			}
		}
		
		super.setDead();
	}
}

package chylex.hee.entity.block;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.item.ItemTempleCaller;
import chylex.hee.mechanics.misc.TempleEvents;
import chylex.hee.system.util.BlockPosM;

public class EntityBlockTempleDragonEgg extends EntityFallingBlock{
	public EntityBlockTempleDragonEgg(World world){
		super(world);
	}
	
	public EntityBlockTempleDragonEgg(World world, double x, double y, double z){
		super(world,x,y,z,Blocks.dragon_egg,0);
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		if (++field_145812_b == 1 && !worldObj.isRemote){
			BlockPosM tmpPos = BlockPosM.tmp(this);
			
			if (tmpPos.getBlock(worldObj) != func_145805_f()){ // OBFUSCATED get block
				setDead();
				return;
			}
			
			tmpPos.setAir(worldObj);
			tmpPos.moveDown();
			
			for(int origX = tmpPos.x, origZ = tmpPos.z, x = origX+2; x < origX+5; x++){
				for(int z = origZ-1; z <= origZ+1; z++){
					if (tmpPos.set(x,tmpPos.y,z).getBlock(worldObj) == BlockList.temple_end_portal)tmpPos.setMetadata(worldObj,1,2);
				}
			}
		}
		
		moveEntity(motionX,motionY,motionZ);
		
		for(int y = 5; y >= 0; y--){
			for(int a = 0; a < Math.min(10,field_145812_b/4F); a++){
				worldObj.spawnParticle("portal",posX+rand.nextDouble()-0.5D,posY+y+rand.nextDouble()-0.3D,posZ+rand.nextDouble()-0.5D,0D,0D,0D);
			}
		}
		
		if (field_145812_b >= 24){
			motionY = 0.01D;
			
			double deltaY = posY-ItemTempleCaller.templeY-2;
			
			if (!worldObj.isRemote){
				if (deltaY > 4D && rand.nextInt(6-(deltaY > 6D ? 2 : 0)) == 0){
					double xx = -5D, zz = -5D;
					
					switch(rand.nextInt(4)){
						case 0: zz = ItemTempleCaller.templeZ;
						case 1: xx = ItemTempleCaller.templeX+rand.nextFloat()*18D;
								if (zz == -5D)zz = ItemTempleCaller.templeZ+13D;
								break;
						case 2: xx = ItemTempleCaller.templeX;
						case 3: zz = ItemTempleCaller.templeZ+rand.nextFloat()*13D;
								if (xx == -5D)xx = ItemTempleCaller.templeX+18D;
								break;
					}
					
					worldObj.createExplosion(this,xx,ItemTempleCaller.templeY+rand.nextFloat()*8D,zz,1.5F+(float)((deltaY-4F)*0.5F),true);
				}
				
				if (deltaY > 6D){
					for(EntityPlayerMP player:new ArrayList<EntityPlayerMP>(worldObj.playerEntities)){
						if (player.ridingEntity != null)player.mountEntity(null);
						//DragonUtil.teleportToOverworld(player);
					}
					
					TempleEvents.destroyWorld();
				}
			}
		}
	}
	
	@Override
	protected void fall(float distance){}
	
	@Override
	public Block func_145805_f(){
		return Blocks.dragon_egg;
	}
}

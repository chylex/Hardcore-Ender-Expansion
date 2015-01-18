package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.block.BlockDragonEggCustom;
import chylex.hee.system.util.MathUtil;

public class EntityBlockFallingDragonEgg extends EntityFallingBlock{
	public EntityBlockFallingDragonEgg(World world){
		super(world);
	}

	public EntityBlockFallingDragonEgg(World world, double x, double y, double z){
		super(world,x,y,z,Blocks.dragon_egg);
		preventEntitySpawning = true;
		setSize(0.98F,0.98F);
		yOffset = height/2F;
		setPosition(x,y,z);
		motionX = motionY = motionZ = 0D;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	@Override
	public void onUpdate(){
		if (func_145805_f().getMaterial() == Material.air){ // OBFUSCATED get block
			setDead();
			return;
		}

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		fallTime++;
		motionY -= 0.09D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.9D;
		motionY *= 0.9D;
		motionZ *= 0.9D;
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);

		if (fallTime == 1 && worldObj.getBlock(i,j,k) == func_145805_f()){ // OBFUSCATED get block
			worldObj.setBlockToAir(i,j,k);
		}
		else if (!worldObj.isRemote && fallTime == 1){
			die();
		}

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (worldObj.getBlock(i,j,k) != Blocks.piston_extension && worldObj.getEntitiesWithinAABB(EntityDragon.class,this.boundingBox.expand(1,1,1)).isEmpty()){
				if (worldObj.setBlock(i,j,k,func_145805_f()))setDead(); // OBFUSCATED get block
				else die();
			}
			else die();
		}
		else if (fallTime > 100 && !worldObj.isRemote && (j < 1 || j > 256) || fallTime > 600){
			die();
		}
	}

	private void die(){
		if (!worldObj.isRemote && !BlockDragonEggCustom.teleportNearby(worldObj,MathUtil.floor(posX),MathUtil.floor(posY),MathUtil.floor(posZ))){
			BlockDragonEggCustom.teleportEntityToPortal(this);
		}
		
		setDead();
	}
	
	@Override
	public Block func_145805_f(){ // OBFUSCATED get block
		return Blocks.dragon_egg;
	}
}

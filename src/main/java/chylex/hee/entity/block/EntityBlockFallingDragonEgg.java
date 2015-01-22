package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.BlockDragonEggCustom;
import chylex.hee.system.util.BlockPosM;

public class EntityBlockFallingDragonEgg extends EntityFallingBlock{
	public EntityBlockFallingDragonEgg(World world){
		super(world);
	}

	public EntityBlockFallingDragonEgg(World world, double x, double y, double z){
		super(world,x,y,z,Blocks.dragon_egg.getDefaultState());
		preventEntitySpawning = true;
		setSize(0.98F,0.98F);
		setPosition(x,y,z);
		motionX = motionY = motionZ = 0D;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	@Override
	public void onUpdate(){
		Block block = getBlock().getBlock();
		
		if (block.getMaterial() == Material.air){ // OBFUSCATED get block
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
		BlockPosM pos = new BlockPosM(this);

		if (fallTime == 1 && pos.getBlock(worldObj) == block){ // OBFUSCATED get block
			worldObj.setBlockToAir(pos);
		}
		else if (!worldObj.isRemote && fallTime == 1){
			die();
		}

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (pos.getBlock(worldObj) != Blocks.piston_extension && worldObj.getEntitiesWithinAABB(EntityDragon.class,this.boundingBox.expand(1,1,1)).isEmpty()){
				if (pos.setBlock(worldObj,getBlock()))setDead(); // OBFUSCATED get block
				else die();
			}
			else die();
		}
		else if (fallTime > 100 && !worldObj.isRemote && (pos.y < 1 || pos.y > 256) || fallTime > 600){
			die();
		}
	}

	private void die(){
		if (!worldObj.isRemote && !BlockDragonEggCustom.teleportNearby(worldObj,new BlockPosM(this)))BlockDragonEggCustom.teleportEntityToPortal(this);
		setDead();
	}
}

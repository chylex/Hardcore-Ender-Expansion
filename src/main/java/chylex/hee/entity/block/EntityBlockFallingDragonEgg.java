package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.override.BlockDragonEggCustom;
import chylex.hee.system.util.BlockPosM;
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
		field_145812_b++;
		motionY -= 0.09D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.9D;
		motionY *= 0.9D;
		motionZ *= 0.9D;
		BlockPosM tmpPos = BlockPosM.tmp(this);

		if (field_145812_b == 1 && tmpPos.getBlock(worldObj) == func_145805_f()){ // OBFUSCATED get block
			tmpPos.setAir(worldObj);
		}
		else if (!worldObj.isRemote && field_145812_b == 1){
			die();
		}

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (tmpPos.getBlock(worldObj) != Blocks.piston_extension){
				if (tmpPos.setBlock(worldObj,func_145805_f()))setDead(); // OBFUSCATED get block
				else die();
			}
			else die();
		}
		else if (field_145812_b > 100 && !worldObj.isRemote && (tmpPos.y < 1 || tmpPos.y > 256) || field_145812_b > 600){
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

package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;

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
		Pos pos = Pos.at(this);
		
		if (field_145812_b == 1){
			if (pos.getBlock(worldObj) == func_145805_f())pos.setAir(worldObj); // OBFUSCATED get block
			else if (!worldObj.isRemote)setDead();
		}
		
		if (onGround){
			if (pos.getBlock(worldObj) != Blocks.piston_extension){
				pos.setBlock(worldObj,func_145805_f()); // OBFUSCATED get block
			}
			
			setDead();
		}
		else if (pos.getY() < 1 || field_145812_b > 600){
			setDead();
		}
	}
	
	@Override
	public Block func_145805_f(){ // OBFUSCATED get block
		return Blocks.dragon_egg;
	}
}

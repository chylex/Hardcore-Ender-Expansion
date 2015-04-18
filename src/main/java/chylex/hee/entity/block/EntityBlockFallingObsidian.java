package chylex.hee.entity.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.util.BlockPosM;

public class EntityBlockFallingObsidian extends EntityFallingBlock{
	public EntityBlockFallingObsidian(World world){
		super(world);
	}
	
	public EntityBlockFallingObsidian(World world, double x, double y, double z){
		super(world,x,y,z,BlockList.obsidian_falling);
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
		motionY -= 0.15D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.9D;
		motionY *= 0.9D;
		motionZ *= 0.9D;
		BlockPosM tmpPos = BlockPosM.tmp(this);
		
		if (field_145812_b == 1 && tmpPos.getBlock(worldObj) == func_145805_f()){ // OBFUSCATED get block
			tmpPos.setAir(worldObj);
		}

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (field_145812_b > 5 && tmpPos.getBlock(worldObj) != Blocks.piston_extension && worldObj.getEntitiesWithinAABB(EntityBossDragon.class,boundingBox.expand(1,1,1)).isEmpty()){
				if (tmpPos.set(this).setBlock(worldObj,func_145805_f()))setDead();
			}
		}
		else if (!worldObj.isRemote && ((field_145812_b > 100 && (tmpPos.y < 1 || tmpPos.y > 256)) || field_145812_b > 600)){
			dropItem(Item.getItemFromBlock(Blocks.obsidian),1);
			setDead();
		}
	}
	
	@Override
	protected void fall(float distance){
		int i = MathHelper.ceiling_float_int(distance-1F);

		if (i > 0){
			for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox)){
				entity.attackEntityFrom(DamageSource.fallingBlock,Math.min(MathHelper.floor_float(i*5F),60));
			}
		}
	}
	
	@Override
	public Block func_145805_f(){
		return BlockList.obsidian_falling;
	}
}
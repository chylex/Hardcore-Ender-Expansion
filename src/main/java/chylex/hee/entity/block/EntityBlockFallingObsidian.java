package chylex.hee.entity.block;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class EntityBlockFallingObsidian extends EntityFallingBlock{
	public EntityBlockFallingObsidian(World world){
		super(world);
	}
	
	public EntityBlockFallingObsidian(World world, double x, double y, double z){
		super(world,x,y,z,BlockList.obsidian_falling.getDefaultState());
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
		motionY -= 0.15D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.9D;
		motionY *= 0.9D;
		motionZ *= 0.9D;
		BlockPosM pos = new BlockPosM(this);

		if (fallTime == 1 && pos.getBlock(worldObj) == block)worldObj.setBlockToAir(pos);

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (fallTime > 5 && pos.getBlock(worldObj) != Blocks.piston_extension && worldObj.getEntitiesWithinAABB(EntityDragon.class,this.boundingBox.expand(1,1,1)).isEmpty()){
				if (pos.setBlock(worldObj,getBlock()))setDead();
			}
		}
		else if (!worldObj.isRemote && ((fallTime > 100 && (pos.y < 1 || pos.y > 256)) || fallTime > 600)){
			dropItem(Item.getItemFromBlock(Blocks.obsidian),1);
			setDead();
		}
	}
	
	@Override
	public void fall(float distance, float damageMp){
		int i = MathHelper.ceiling_float_int(distance-1F);

		if (i > 0){
			for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox)){
				entity.attackEntityFrom(DamageSource.fallingBlock,Math.min(MathHelper.floor_float(i*5F),60));
			}
		}
	}
}
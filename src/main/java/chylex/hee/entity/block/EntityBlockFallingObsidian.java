package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class EntityBlockFallingObsidian extends EntityFallingBlock{
	public EntityBlockFallingObsidian(World world){
		super(world);
	}
	
	public EntityBlockFallingObsidian(World world, double x, double y, double z){
		super(world,x,y,z,BlockList.obsidian_end);
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
		
		int ix = MathHelper.floor_double(posX),
			iy = MathHelper.floor_double(posY),
			iz = MathHelper.floor_double(posZ);

		if (field_145812_b == 1 && worldObj.getBlock(ix,iy,iz) == func_145805_f())worldObj.setBlockToAir(ix,iy,iz);

		if (onGround){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;

			if (field_145812_b > 5 && worldObj.getBlock(ix,iy,iz) != Blocks.piston_extension && worldObj.getEntitiesWithinAABB(EntityDragon.class,this.boundingBox.expand(1,1,1)).size() == 0){
				if (worldObj.setBlock(ix,iy,iz,func_145805_f())){
					for(EntityPlayer observer:ObservationUtil.getAllObservers(worldObj,ix+0.5D,iy+0.5D,iz+0.5D,8D))KnowledgeRegistrations.FALLING_OBSIDIAN.tryUnlockFragment(observer,1F,new byte[]{ 0,1 });
					setDead();
				}
			}
		}
		else if (!worldObj.isRemote && ((field_145812_b > 100 && (iy < 1 || iy > 256)) || field_145812_b > 600)){
			dropItem(Item.getItemFromBlock(Blocks.obsidian),1);
			setDead();
		}
	}
	
	@Override
	protected void fall(float distance){
		int i = MathHelper.ceiling_float_int(distance-1F);

		if (i > 0){
			boolean hurt = false;
			
			for(Object o:worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox)){
				((Entity)o).attackEntityFrom(DamageSource.fallingBlock,Math.min(MathHelper.floor_float(i*5F),60));
				
				if (!hurt){
					for(EntityPlayer observer:ObservationUtil.getAllObservers((Entity)o,8D))KnowledgeRegistrations.FALLING_OBSIDIAN.tryUnlockFragment(observer,1F,new byte[]{ 0,1 });
					hurt = true;
				}
			}
		}
	}
	
	@Override
	public Block func_145805_f(){
		return BlockList.obsidian_end;
	}
}
package chylex.hee.entity.mob;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.GlobalMobData.IIgnoreEnderGoo;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.entity.EntityDataWatcher;

public abstract class EntityAbstractEndermanCustom extends EntityEnderman implements IIgnoreEnderGoo, IEndermanRenderer{
	private enum Data{ AGGRESSIVE, HELD_BLOCK_ID, HELD_BLOCK_META }
	
	protected EntityDataWatcher entityData;
	
	public EntityAbstractEndermanCustom(World world){
		super(world);
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		entityData = new EntityDataWatcher(this);
		entityData.addBoolean(Data.AGGRESSIVE,29);
		entityData.addShort(Data.HELD_BLOCK_ID,30);
		entityData.addByte(Data.HELD_BLOCK_META,31);
	}
	
	public void dropCarrying(){
		if (isCarrying())entityDropItem(getCarrying(),0F);
	}
	
	@Override
	public boolean isCarrying(){
		return entityData.getShort(Data.HELD_BLOCK_ID) != 0;
	}
	
	@Override
	public ItemStack getCarrying(){
		BlockInfo info = getCarryingInfo();
		return new ItemStack(info.block,1,info.meta);
	}
	
	public void setAggressive(boolean aggressive){
		entityData.setBoolean(Data.AGGRESSIVE,aggressive);
	}
	
	@Override
	public boolean isAggressive(){
		return entityData.getBoolean(Data.AGGRESSIVE);
	}
	
	/**
	 * Replacement method for the ones that set the carried block and metadata.
	 */
	public void setCarryingInfo(BlockInfo info){
		entityData.setShort(Data.HELD_BLOCK_ID,info == null ? 0 : Block.getIdFromBlock(info.block));
		entityData.setByte(Data.HELD_BLOCK_META,info == null ? 0 : info.meta);
	}
	
	/**
	 * Replacement method for the ones that return the carried block and metadata.
	 */
	public BlockInfo getCarryingInfo(){
		return new BlockInfo(Block.getBlockById(entityData.getShort(Data.HELD_BLOCK_ID)),entityData.getShort(Data.HELD_BLOCK_META));
	}
	
	/**
	 * Replacement method for isWet() to avoid automatic damage in EntityEnderman.onLivingUpdate() and replace it with a custom system.
	 */
	public boolean isEndermanWet(){
		return super.isWet();
	}
	
	// Disabled methods
	
	@Override
	public boolean isWet(){ return false; }
	
	@Override
	public void setScreaming(boolean isScreaming){}
	
	@Override
	public boolean isScreaming(){ return false; }

	@Override
	public void func_146081_a(Block carriedBlock){}

	@Override
	public Block func_146080_bZ(){ return Blocks.air; }
	
	@Override
	public void setCarryingData(int data){}
	
	@Override
	public int getCarryingData(){ return 0; }
	
	@Override
	protected boolean teleportRandomly(){ return false; }
	
	@Override
	protected boolean teleportTo(double x, double y, double z){ return false; }
	
	@Override
	protected boolean teleportToEntity(Entity entity){ return false; }
}

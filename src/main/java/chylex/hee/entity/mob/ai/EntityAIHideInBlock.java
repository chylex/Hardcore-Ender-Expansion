package chylex.hee.entity.mob.ai;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;

public class EntityAIHideInBlock extends EntityAIBase{
	private EntityCreature entity;
	private Block[] validBlocks;
	private IHideInBlock handler;
	private float chance = 0.1F;
	
	public EntityAIHideInBlock(EntityCreature owner, Block[] blocks, IHideInBlock handler){
		this.entity = owner;
		this.validBlocks = blocks;
		this.handler = handler;
	}
	
	public EntityAIHideInBlock setChancePerTick(float chance){
		this.chance = chance;
		return this;
	}
	
	@Override
	public boolean shouldExecute(){
		if (entity.getAttackTarget() != null || !entity.getNavigator().noPath() || entity.getRNG().nextFloat() > chance)return false;
		
		Pos pos = Pos.at(entity).offset(entity.getRNG().nextInt(6));
		
		if (ArrayUtils.contains(validBlocks,pos.getBlock(entity.worldObj))){
			pos.setBlock(entity.worldObj,handler.hide(pos.getInfo(entity.worldObj)));
			entity.spawnExplosionParticle(); // TODO check client
			entity.setDead();
		}
		
		return false;
	}
	
	@Override
	public boolean continueExecuting(){
		return false;
	}
	
	@FunctionalInterface
	public static interface IHideInBlock{
		BlockInfo hide(BlockInfo targetBlock);
	}
}

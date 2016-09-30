package chylex.hee.entity.mob.ai;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractContinuous;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;

public class EntityAIHideInBlock extends EntityAIAbstractContinuous{
	private final EntityCreature entity;
	private final Block[] validBlocks;
	private final IHideInBlock handler;
	private float chance = 1F/10F;
	
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
	public void tick(){
		if (entity.getAttackTarget() != null || !entity.getNavigator().noPath() || entity.getRNG().nextFloat() > chance)return;
		
		Pos pos = Pos.at(entity).offset(entity.getRNG().nextInt(6));
		
		if (ArrayUtils.contains(validBlocks, pos.getBlock(entity.worldObj))){
			pos.setBlock(entity.worldObj, handler.hide(pos.getInfo(entity.worldObj)));
			PacketPipeline.sendToAllAround(entity, 64D, new C21EffectEntity(FXType.Entity.ENTITY_EXPLOSION_PARTICLE, entity));
			entity.setDead();
		}
	}
	
	@FunctionalInterface
	public static interface IHideInBlock{
		BlockInfo hide(BlockInfo targetBlock);
	}
}

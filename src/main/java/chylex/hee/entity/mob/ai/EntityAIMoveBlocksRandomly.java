package chylex.hee.entity.mob.ai;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.init.Blocks;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;

public class EntityAIMoveBlocksRandomly extends EntityAIAbstractContinuous{
	private final EntityCreature entity;
	private final IMoveBlocks moveHandler;
	private final Block[] validBlocks;
	private float stealChance = 1F/200F;
	private float placeChance = 1F/400F;
	
	public EntityAIMoveBlocksRandomly(EntityCreature owner, IMoveBlocks moveHandler, Block[] validBlocks){
		this.entity = owner;
		this.moveHandler = moveHandler;
		this.validBlocks = validBlocks;
	}
	
	public EntityAIMoveBlocksRandomly setStealChancePerTick(float chance){
		this.stealChance = chance;
		return this;
	}
	
	public EntityAIMoveBlocksRandomly setPlaceChancePerTick(float chance){
		this.placeChance = chance;
		return this;
	}
	
	@Override
	protected void tick(){
		if (entity.getAttackTarget() != null || entity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))return;
		
		Random rand = entity.getRNG();
		BlockInfo holding = moveHandler.getCarryingBlock();
		
		if (holding.block == Blocks.air && rand.nextFloat() > stealChance){
			for(int attempt = 0; attempt < 5; attempt++){
				Pos pos = moveHandler.findBlockStealPosition(entity);
				
				if (ArrayUtils.contains(validBlocks,pos.getBlock(entity.worldObj))){
					moveHandler.setCarryingBlock(pos.getInfo(entity.worldObj));
					pos.setAir(entity.worldObj);
					break;
				}
			}
		}
		else if (holding.block != Blocks.air && rand.nextFloat() > placeChance){
			for(int attempt = 0; attempt < 5; attempt++){
				Pos pos = moveHandler.findBlockPlacePosition(entity);
				Pos below = pos.getDown();
				
				if (pos.isAir(entity.worldObj) && !below.isAir(entity.worldObj) && below.getBlock(entity.worldObj).renderAsNormalBlock()){
					moveHandler.setCarryingBlock(null);
					pos.setBlock(entity.worldObj,holding);
					break;
				}
			}
		}
	}
	
	public static interface IMoveBlocks{
		void setCarryingBlock(@Nullable BlockInfo info);
		@Nonnull BlockInfo getCarryingBlock();
		
		default Pos findBlockStealPosition(EntityCreature entity){
			return Pos.at(entity.posX+(entity.getRNG().nextDouble()-0.5D)*4D,
						  entity.posY+entity.getRNG().nextDouble()*3D,
						  entity.posZ+(entity.getRNG().nextDouble()-0.5D)*4D);
		}
		
		default Pos findBlockPlacePosition(EntityCreature entity){
			return Pos.at(entity.posX+(entity.getRNG().nextDouble()-0.5D)*3D,
						  entity.posY+entity.getRNG().nextDouble()*2D,
						  entity.posZ+(entity.getRNG().nextDouble()-0.5D)*3D);
		}
	}
}

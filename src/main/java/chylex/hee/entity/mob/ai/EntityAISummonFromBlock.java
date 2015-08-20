package chylex.hee.entity.mob.ai;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Pos;

public class EntityAISummonFromBlock extends EntityAIBase{
	private final EntityCreature entity;
	private final Block summonBlock;
	private final Function<World,EntityCreature> spawner;
	private int tick;
	
	public EntityAISummonFromBlock(EntityCreature owner, Block summonBlock, Function<World,EntityCreature> spawner){
		this.entity = owner;
		this.summonBlock = summonBlock;
		this.spawner = spawner;
	}
	
	public void setSummonTimer(int timer){
		if (this.tick == 0)this.tick = timer;
	}
	
	@Override
	public boolean shouldExecute(){
		return tick > 0;
	}
	
	@Override
	public void updateTask(){
		if (--tick <= 0){
			tick = 0;
			
			Random rand = entity.getRNG();
			
			for(int attempt = 0, spawnsLeft = 2+entity.worldObj.difficultySetting.getDifficultyId(); attempt < 350; attempt++){
				Pos pos = Pos.at(entity).offset(rand.nextInt(31)-15,rand.nextInt(11)-5,rand.nextInt(31)-15);
				
				if (pos.getBlock(entity.worldObj) == summonBlock){
					EntityCreature spawned = spawner.apply(entity.worldObj);
					spawned.setLocationAndAngles(pos.getX()+0.5D,pos.getY(),pos.getZ()+0.5D,rand.nextFloat()*360F-180F,0F);
					entity.worldObj.spawnEntityInWorld(spawned);
					pos.setAir(entity.worldObj);
					
					PacketPipeline.sendToAllAround(spawned,64D,new C21EffectEntity(FXType.Entity.ENTITY_EXPLOSION_PARTICLE,spawned));
					
					if (--spawnsLeft == 0)break;
				}
			}
		}
	}
}

package chylex.hee.world.feature.stronghold.rooms.traps;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.entity.technical.EntityTechnicalTrigger;
import chylex.hee.entity.technical.EntityTechnicalTrigger.TriggerBase;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C21EffectEntity;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.util.EntitySelector;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLargeIntersection;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;

public class StrongholdRoomLargeIntersectionTrap extends StrongholdRoomLargeIntersection{
	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		super.generate(inst,world,rand,x,y,z);
		
		// spawner
		world.addEntity(new EntityTechnicalTrigger(null,x+maxX/2+0.5F,y+1,z+maxZ/2+0.5F,new TriggerSilverfishBlocks()));
	}
	
	public static class TriggerSilverfishBlocks extends TriggerBase{
		private byte checkTimer = 0;
		
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			if (world.difficultySetting == EnumDifficulty.PEACEFUL)return;
			
			if (++checkTimer > 10){
				checkTimer = 0;
				
				if (world.getClosestPlayerToEntity(entity,5D) == null)return;
				
				PosMutable mpos = new PosMutable();
				
				List<EntityPlayer> players = EntitySelector.players(world,entity.boundingBox.expand(8.5D,4.5D,8.5D).offset(0D,2D,0D));
				if (players.isEmpty())return;
				
				for(int attempt = 0, spawnsLeft = 3+rand.nextInt(3)+world.difficultySetting.getDifficultyId(); attempt < 500; attempt++){
					mpos.set(entity).move(rand.nextInt(9)-4,rand.nextInt(4),rand.nextInt(9)-4);
					
					if (mpos.getBlock(world) == Blocks.stonebrick || mpos.getBlock(world) == Blocks.monster_egg){
						EntityMobSilverfish silverfish = new EntityMobSilverfish(entity.worldObj);
						silverfish.setLocationAndAngles(mpos.x+0.5D,mpos.y,mpos.z+0.5D,rand.nextFloat()*360F-180F,0F);
						silverfish.setAttackTarget(players.get(rand.nextInt(players.size())));
						silverfish.setCanHideInBlocks(false);
						silverfish.setCanSummonSilverfish(true);
						entity.worldObj.spawnEntityInWorld(silverfish);
						
						mpos.breakBlock(entity.worldObj,false);
						PacketPipeline.sendToAllAround(silverfish,64D,new C21EffectEntity(FXType.Entity.ENTITY_EXPLOSION_PARTICLE,silverfish));
						
						if (--spawnsLeft == 0)break;
					}
				}
				
				entity.setDead();
			}
		}
	}
	
	@Override
	protected float getWeightFactor(){
		return 1.75F;
	}
	
	@Override
	protected float getWeightMultiplier(){
		return 4F;
	}
}

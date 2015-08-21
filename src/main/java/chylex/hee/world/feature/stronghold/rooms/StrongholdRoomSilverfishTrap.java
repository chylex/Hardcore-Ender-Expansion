package chylex.hee.world.feature.stronghold.rooms;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.entity.technical.EntityTechnicalTrigger;
import chylex.hee.entity.technical.EntityTechnicalTrigger.TriggerBase;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Size;

public class StrongholdRoomSilverfishTrap extends StrongholdRoom{
	public StrongholdRoomSilverfishTrap(){
		super(new Size(13,7,13));
	}

	@Override
	public void generate(StructureDungeonPieceInst inst, StructureWorld world, Random rand, int x, int y, int z){
		PosMutable mpos = new PosMutable();
		
		// box
		placeCube(world,rand,placeStoneBrick,x,y,z,x+maxX,y,z+maxZ);
		placeCube(world,rand,placeStoneBrick,x,y+maxY,z,x+maxX,y+maxY,z+maxZ);
		placeWalls(world,rand,placeStoneBrick,x,y+1,z,x+maxX,y+maxY-1,z+maxZ);
		
		// silverfish
		for(int cornerX = 0; cornerX < 2; cornerX++){
			for(int cornerZ = 0; cornerZ < 2; cornerZ++){
				mpos.setX(x+3+6*cornerX).setZ(z+1+10*cornerZ);
				placeLine(world,rand,placeStoneBrick,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z);
				mpos.setX(x+3+6*cornerX).setZ(z+2+8*cornerZ);
				placeLine(world,rand,placeStoneBrick,mpos.x,y+2,mpos.z,mpos.x,y+maxY-1,mpos.z);
				mpos.setX(x+3+6*cornerX).setZ(z+3+6*cornerZ);
				placeLine(world,rand,placeStoneBrick,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z);
				mpos.setX(x+2+8*cornerX).setZ(z+3+6*cornerZ);
				placeLine(world,rand,placeStoneBrick,mpos.x,y+2,mpos.z,mpos.x,y+maxY-1,mpos.z);
				mpos.setX(x+1+10*cornerX).setZ(z+3+6*cornerZ);
				placeLine(world,rand,placeStoneBrick,mpos.x,y+1,mpos.z,mpos.x,y+maxY-1,mpos.z);
			}
		}
		
		// spawner
		world.addEntity(new EntityTechnicalTrigger(null,x+maxX/2+0.5F,y+1,z+maxZ/2+0.5F,new TriggerSilverfish()));
		
		// connections
		super.generate(inst,world,rand,x,y,z);
	}
	
	public static class TriggerSilverfish extends TriggerBase{
		private byte checkTimer = 0;
		private byte spawnsLeft = -1;
		
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			if (world.difficultySetting == EnumDifficulty.PEACEFUL)return;
			
			if (spawnsLeft != -1){
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class,entity.boundingBox.expand(7D,4.5D,7D).offset(0D,2D,0D));
				if (players.isEmpty() || rand.nextInt(3) != 0)return;
				
				for(int cycle = 0; cycle < 1+rand.nextInt(2); cycle++){
					EntityMobSilverfish silverfish = new EntityMobSilverfish(world);
					silverfish.setPositionAndRotation(entity.posX+4.5D*(rand.nextInt(2)*2-1),entity.posY+4D,entity.posZ+4.5D*(rand.nextInt(2)*2-1),rand.nextFloat()*360F-180F,0F);
					silverfish.setAttackTarget(players.get(rand.nextInt(players.size())));
					silverfish.setCanSummonSilverfish(false);
					silverfish.setCanHideInBlocks(false);
					world.spawnEntityInWorld(silverfish);
					
					if (--spawnsLeft == 0){
						entity.setDead();
						break;
					}
				}
			}
			else if (++checkTimer > 10){
				checkTimer = 0;
				if (world.getClosestPlayerToEntity(entity,5.5D) != null)spawnsLeft = (byte)(6+world.difficultySetting.getDifficultyId()*2+rand.nextInt(4));
			}
		}
	}
}

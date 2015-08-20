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
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Size;

public class StrongholdPieceSilverfishTrap extends StrongholdPiece{
	public StrongholdPieceSilverfishTrap(){
		super(Type.ROOM,new Size(13,7,13));
		
		addConnection(Facing4.NORTH_NEGZ,6,0,0,fromRoom);
		addConnection(Facing4.SOUTH_POSZ,6,0,12,fromRoom);
		addConnection(Facing4.EAST_POSX,12,0,6,fromRoom);
		addConnection(Facing4.WEST_NEGX,0,0,6,fromRoom);
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
		for(Facing4 facing:Facing4.list){
			if (!inst.isConnectionFree(facing)){
				int perX = facing.perpendicular().getX(), perZ = facing.perpendicular().getZ();
				
				mpos.set(x+maxX/2,y+1,z+maxZ/2).move(facing,6);
				placeCube(world,rand,placeAir,mpos.x-perX,y+1,mpos.z-perZ,mpos.x+perX,y+3,mpos.z+perZ);
			}
		}
	}
	
	public static class TriggerSilverfish extends TriggerBase{
		private byte checkTimer = 0;
		private byte spawnsLeft = -1;
		
		@Override
		protected void update(EntityTechnicalTrigger entity, World world, Random rand){
			if (world.difficultySetting == EnumDifficulty.PEACEFUL)return;
			
			if (spawnsLeft != -1){
				List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class,entity.boundingBox.expand(7D,4.5D,7D).offset(0D,2D,0D));
				if (players.isEmpty())return;
				
				EntityMobSilverfish silverfish = new EntityMobSilverfish(world);
				silverfish.setPositionAndRotation(entity.posX+4.5D*(rand.nextInt(2)*2-1),entity.posY+4D,entity.posZ+4.5D*(rand.nextInt(2)*2-1),rand.nextFloat()*360F-180F,0F);
				silverfish.setAttackTarget(players.get(rand.nextInt(players.size())));
				silverfish.setCanHideInBlocks(false);
				world.spawnEntityInWorld(silverfish);
				
				if (--spawnsLeft == 0)entity.setDead();
			}
			else if (++checkTimer > 15){
				checkTimer = 0;
				if (world.getClosestPlayerToEntity(entity,5D) != null)spawnsLeft = (byte)(6+world.difficultySetting.ordinal()*2+rand.nextInt(4));
			}
		}
	}
}

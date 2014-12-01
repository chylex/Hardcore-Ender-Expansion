package chylex.hee.world.structure.island.biome.feature.island.laboratory;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;

public final class LaboratoryContent{
	private enum SmallRoom{
		EMPTY, CLUSTER, ENDER_CHEST, FLOWER_POTS
	}
	
	private static final WeightedList<ObjectWeightPair<SmallRoom>> smallRoomList = new WeightedList<>(
		ObjectWeightPair.of(SmallRoom.EMPTY, 50),
		ObjectWeightPair.of(SmallRoom.FLOWER_POTS, 18),
		ObjectWeightPair.of(SmallRoom.CLUSTER, 15),
		ObjectWeightPair.of(SmallRoom.ENDER_CHEST, 8)
	);
	
	public static void populateSmallRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		SmallRoom design = smallRoomList.getRandomItem(rand).getObject();
		
		switch(design){
			case FLOWER_POTS:
				world.setBlock(x,y+1,z,BlockList.death_flower_pot,rand.nextInt(4) == 0 ? 15 : rand.nextInt(15));
				
				for(int extra = rand.nextInt(2+rand.nextInt(5)), xx, zz; extra > 0; extra--){
					xx = x+rand.nextInt(3)-rand.nextInt(3);
					zz = z+rand.nextInt(3)-rand.nextInt(3);
					
					if (world.isAir(xx,y+1,zz)){
						if (rand.nextInt(5) != 0){
							boolean adj = false;
							
							for(int dir = 0; dir < 4; dir++){
								if (world.getBlock(xx+Direction.offsetX[dir],y+1,zz+Direction.offsetZ[dir]) == BlockList.death_flower_pot){
									adj = true;
									break;
								}
							}
							
							if (adj)continue;
						}
						
						world.setBlock(xx,y+1,zz,BlockList.death_flower_pot,rand.nextInt(4) == 0 ? 15 : rand.nextInt(15));
					}
				}
				
				break;
				
			case CLUSTER:
				world.setBlock(x,y+1,z,BlockList.laboratory_floor);
				world.setBlock(x,y+2,z,BlockList.energy_cluster);
				
				for(int a = 0; a < 3; a++){
					for(int b = 0; b < 2; b++){
						if (world.getBlock(x-4+8*b,y+2,z-1+a) == BlockList.laboratory_glass)world.setBlock(x-4+8*b,y+2,z-1+a,BlockList.laboratory_obsidian);
						if (world.getBlock(x-1+a,y+2,z-4+8*b) == BlockList.laboratory_glass)world.setBlock(x-1+a,y+2,z-4+8*b,BlockList.laboratory_obsidian);
					}
				}
				
				break;
				
			case ENDER_CHEST:
				world.setBlock(x,y+1,z,Blocks.ender_chest,rand.nextInt(4));
				break;
				
			default:
		}
	}
	
	private enum LargeRoom{
		EMPTY, ENCASED_ENDIUM, LOOT_CHESTS
	}
	
	private static final WeightedList<ObjectWeightPair<LargeRoom>> largeRoomList = new WeightedList<>(
		ObjectWeightPair.of(LargeRoom.EMPTY, 50),
		ObjectWeightPair.of(LargeRoom.ENCASED_ENDIUM, 30),
		ObjectWeightPair.of(LargeRoom.LOOT_CHESTS, 25)
	);
	
	public static void populateLargeRoom(LargeStructureWorld world, Random rand, int x, int y, int z){
		LargeRoom design = largeRoomList.getRandomItem(rand).getObject();
		
		switch(design){
			case ENCASED_ENDIUM:
				world.setBlock(x,y+2,z,BlockList.endium_block);
				
				for(int py = 0; py < 3; py++){
					for(int px = 0; px < 3; px++){
						for(int pz = 0; pz < 3; pz++){
							if (world.isAir(x-1+px,y+1+py,z-1+pz))world.setBlock(x-1+px,y+1+py,z-1+pz,BlockList.laboratory_glass);
						}
					}
				}
				
				break;
				
			case LOOT_CHESTS:
				
				break;
				
			default:
		}
	}
	
	private LaboratoryContent(){}
}

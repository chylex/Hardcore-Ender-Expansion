package chylex.hee.world.feature;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.BlockColor;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.stronghold.StrongholdPiece;
import chylex.hee.world.feature.stronghold.corridors.StrongholdCorridorChest;
import chylex.hee.world.feature.stronghold.corridors.StrongholdCorridorDoubleChest;
import chylex.hee.world.feature.stronghold.corridors.StrongholdCorridorIntersection;
import chylex.hee.world.feature.stronghold.corridors.StrongholdCorridorStraight;
import chylex.hee.world.feature.stronghold.corridors.StrongholdStairsStraight;
import chylex.hee.world.feature.stronghold.corridors.StrongholdStairsVertical;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorGrates;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorSmall;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorTorches;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorWooden;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomFountain;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLargeIntersection;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLitCorners;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLitPole;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLitTotem;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomSmallIntersection;
import chylex.hee.world.feature.stronghold.rooms.general.StrongholdRoomEndPortal;
import chylex.hee.world.feature.stronghold.rooms.loot.StrongholdRoomClusterIntersection;
import chylex.hee.world.feature.stronghold.rooms.loot.StrongholdRoomRelicDungeon;
import chylex.hee.world.feature.stronghold.rooms.loot.StrongholdRoomRelicFountains;
import chylex.hee.world.feature.stronghold.rooms.traps.StrongholdRoomLargeIntersectionTrap;
import chylex.hee.world.feature.stronghold.rooms.traps.StrongholdRoomPrisonTrap;
import chylex.hee.world.feature.stronghold.rooms.traps.StrongholdRoomSilverfishTrap;
import chylex.hee.world.loot.WeightedLootTable;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.dungeon.generators.DungeonGeneratorSpreading;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.Range;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenStronghold implements IWorldGenerator{
	private static final int gridSize = 1088/16;
	private static final int maxDistance = 144/16;
	private static final Random checkRand = new Random();
	
	public static final WeightedLootTable loot = new WeightedLootTable();
	
	static{
		loot.addLoot(Items.iron_ingot).setAmount(1,5).setWeight(100);
		loot.addLoot(ItemList.ethereum).setAmount(1,2).setWeight(95);
		loot.addLoot(Items.gold_ingot).setAmount(1,3).setWeight(92);
		loot.addLoot(Items.rotten_flesh).setAmount(1,4).setWeight(82);
		loot.addLoot(Items.leather).setAmount(1,6,RandomAmount.aroundCenter).setWeight(80);
		loot.addLoot(ItemList.knowledge_note).setAmount(1).setWeight(72);
		loot.addLoot(Items.book).setAmount(1).setWeight(70);
		loot.addLoot(Blocks.obsidian).setAmount(2,3).setWeight(58);
		loot.addLoot(Items.bowl).setAmount(1,3,RandomAmount.preferSmaller).setWeight(40);
		loot.addLoot(Items.string).setAmount(1,3).setWeight(36);
		loot.addLoot(Items.dye).setDamage(Meta.getDye(BlockColor.BLACK)).setAmount(1).setWeight(30);
		loot.addLoot(Items.glass_bottle).setAmount(1).setWeight(22);
		loot.addLoot(Items.potionitem).setAmount(1).setWeight(22);
		loot.addLoot(Items.diamond).setAmount(1,2).setWeight(14);
	}
	
	public static Optional<ChunkCoordIntPair> findNearestStronghold(int chunkX, int chunkZ, World world){
		List<ChunkCoordIntPair> found = new ArrayList<>();
		
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				findSpawnChunk(chunkX+x*gridSize,chunkZ+z*gridSize,world).ifPresent(found::add);
			}
		}
		
		if (found.isEmpty())return Optional.empty();
		else return found.stream().min((c1, c2) -> Double.compare(MathUtil.distanceSquared(chunkX-c1.chunkXPos,0,chunkZ-c1.chunkZPos),MathUtil.distanceSquared(chunkX-c2.chunkXPos,0,chunkZ-c2.chunkZPos)));
	}
	
	public static Optional<ChunkCoordIntPair> findSpawnChunk(int chunkX, int chunkZ, World world){
		if (chunkX < 0)chunkX -= gridSize-1;
		if (chunkZ < 0)chunkZ -= gridSize-1;
		
		final int centerX = gridSize*(chunkX/gridSize)+gridSize/2;
		final int centerZ = gridSize*(chunkZ/gridSize)+gridSize/2;
		
		checkRand.setSeed(centerX*920419813L+centerZ*49979687L+world.getSeed());
		
		if (checkRand.nextInt(5) > 2 || Math.abs(centerX) < 50 || Math.abs(centerZ) < 50)return Optional.empty();
		else return Optional.of(new ChunkCoordIntPair(centerX+checkRand.nextInt(maxDistance*2+1)-maxDistance,centerZ+checkRand.nextInt(maxDistance*2+1)-maxDistance));
	}
	
	private static StructureDungeon<DungeonGeneratorSpreading> createStrongholdGenerator(Random rand){ // TODO profile and check if cloning instance w/ 100% known pieces would be better
		StructureDungeon<DungeonGeneratorSpreading> stronghold = new StructureDungeon<>(128,32,128,DungeonGeneratorSpreading::new);
		
		stronghold.setGeneratorSetupFunc(generator -> {
			generator.setPiecesBetweenRooms(3,8);
			generator.setAttemptMultiplier(6);
		});
		
		stronghold.setPieceAmount(40,50);
		stronghold.setStartingPiece(new StrongholdRoomEndPortal());
		
		stronghold.addPieces(16,new Range(0,8),StrongholdCorridorStraight.generateCorridors(3,5,7));
		stronghold.addPieces(8,new Range(0,8),StrongholdCorridorIntersection.generateCorners());
		stronghold.addPieces(12,new Range(0,5),StrongholdCorridorIntersection.generateThreeWay());
		stronghold.addPieces(5,new Range(0,5),StrongholdCorridorIntersection.generateFourWay());
		
		stronghold.addPieces(2,new Range(0,1),StrongholdCorridorChest.generateCorridors());
		stronghold.addPieces(1,new Range(0,1),StrongholdCorridorDoubleChest.generateCorridors());
		
		stronghold.addPieces(4,new Range(0,1),StrongholdStairsStraight.generateStairs());
		stronghold.addPieces(3,new Range(0,1),StrongholdStairsVertical.generateStairs(1));
		stronghold.addPieces(1,new Range(0,1),StrongholdStairsVertical.generateStairs(2));
		
		stronghold.addPieces(10,new Range(0,1),StrongholdDoorSmall.generateDoors());
		stronghold.addPieces(10,new Range(0,1),StrongholdDoorWooden.generateDoors());
		stronghold.addPieces(10,new Range(0,1),StrongholdDoorGrates.generateDoors());
		stronghold.addPieces(10,new Range(0,1),StrongholdDoorTorches.generateDoors());
		
		stronghold.addPieces(2,new Range(1,1),StrongholdRoomRelicDungeon.generateRelicRooms());
		stronghold.addPieces(2,new Range(1,1),StrongholdRoomRelicFountains.generateRelicRooms());
		
		stronghold.addPiece(3,new Range(0,1),new StrongholdRoomSilverfishTrap());
		stronghold.addPiece(3,new Range(0,1),new StrongholdRoomLargeIntersectionTrap());
		stronghold.addPieces(3,new Range(0,2),StrongholdRoomPrisonTrap.generatePrisons());
		
		stronghold.addPiece(5,new Range(0,10),new StrongholdRoomFountain());
		stronghold.addPiece(4,new Range(0,13),new StrongholdRoomLargeIntersection());
		stronghold.addPiece(5,new Range(0,9),new StrongholdRoomSmallIntersection());
		stronghold.addPiece(5,new Range(0,10),new StrongholdRoomLitPole());
		stronghold.addPiece(5,new Range(0,5),new StrongholdRoomLitTotem());
		stronghold.addPieces(4,new Range(0,14),StrongholdRoomLitCorners.generateColors());
		
		return stronghold;
	}
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		if (world.provider.dimensionId != 0)return;
		
		Optional<ChunkCoordIntPair> chunk = findSpawnChunk(chunkX,chunkZ,world);
		if (!chunk.isPresent())return; // TODO HardcoreEnderExpansion.notifications.report(chunk.get().toString());
		
		if (chunk.get().chunkXPos == chunkX && chunk.get().chunkZPos == chunkZ){
			HardcoreEnderExpansion.notifications.report("spawn");
			createStrongholdGenerator(rand).tryGenerateInWorld(world,rand,chunkX*16+8,4+rand.nextInt(11),chunkZ*16+8,10);
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			if (args.length < 1)return;
			
			if (args[0].equals("custom")){
				createStrongholdGenerator(world.rand).tryGenerateInWorld(world,world.rand,MathUtil.floor(player.posX)-8,MathUtil.floor(player.posY)-30,MathUtil.floor(player.posZ)-8,1);
			}
			else if (args[0].equals("pieces")){
				CustomArrayList<StrongholdPiece> pieces = new CustomArrayList<>();
				
				//pieces.add(new StrongholdRoomEndPortal());
				//pieces.add(new StrongholdRoomWorkshop());
				//pieces.add(new StrongholdRoomChestIntersection());
				//pieces.addAll(StrongholdRoomRelicHell.generateRelicRooms());
				//pieces.add(new StrongholdRoomClusterPillar());
				//pieces.add(new StrongholdRoomClusterFloating());
				pieces.add(new StrongholdRoomClusterIntersection());
				
				/*pieces.addAll(StrongholdCorridorStraight.generateCorridors(5));
				pieces.addAll(StrongholdCorridorIntersection.generateCorners());
				pieces.addAll(StrongholdCorridorIntersection.generateThreeWay());
				pieces.addAll(StrongholdCorridorIntersection.generateFourWay());
				pieces.addAll(StrongholdStairsStraight.generateStairs());
				pieces.addAll(StrongholdStairsVertical.generateStairs(1));
				
				pieces.addAll(StrongholdDoorGrates.generateDoors());
				pieces.addAll(StrongholdDoorSmall.generateDoors());
				pieces.addAll(StrongholdDoorTorches.generateDoors());
				pieces.addAll(StrongholdDoorWooden.generateDoors());
				
				pieces.addAll(StrongholdCorridorChest.generateCorridors());
				pieces.addAll(StrongholdCorridorDoubleChest.generateCorridors());
				
				pieces.addAll(StrongholdRoomRelicDungeon.generateRelicRooms());
				pieces.addAll(StrongholdRoomRelicFountains.generateRelicRooms());
				
				pieces.add(new StrongholdRoomLargeIntersectionTrap());
				pieces.addAll(StrongholdRoomPrisonTrap.generatePrisons());
				pieces.add(new StrongholdRoomSilverfishTrap());
				
				pieces.add(new StrongholdRoomArches());
				pieces.add(new StrongholdRoomFountain());
				pieces.add(new StrongholdRoomFountainCeiling());
				pieces.add(new StrongholdRoomHugeIntersection());
				pieces.add(new StrongholdRoomLargeIntersection());
				pieces.addAll(StrongholdRoomLitCorners.generateColors());
				pieces.add(new StrongholdRoomLitPole());
				pieces.add(new StrongholdRoomLitTotem());*/
				//pieces.add(new StrongholdRoomLowerCorners());
				/*pieces.add(new StrongholdRoomSmallIntersection());
				pieces.add(new StrongholdRoomStairSnake());*/
				
				int fullWidth = pieces.stream().mapToInt(piece -> piece.size.sizeX+2).sum();
				
				StructureWorld structureWorld = new StructureWorld(world,fullWidth/2,48,32);
				PosMutable pos = new PosMutable(-fullWidth/2,0,0);
				
				pieces.stream().map(piece -> new StructureDungeonPieceInst(piece,pos.move(piece.size.sizeX+2,0,0).offset(-piece.size.sizeX,0,-piece.size.sizeZ/2))).forEach(inst -> {
					Pos piecePos = inst.boundingBox.getTopLeft();
					inst.useAllConnections();
					inst.clearArea(structureWorld,world.rand);
					inst.generatePiece(structureWorld,world.rand);
					
					for(Connection connection:inst.piece.getConnections()){
						Block block = connection.facing == Facing4.NORTH_NEGZ ? Blocks.netherrack :
									  connection.facing == Facing4.SOUTH_POSZ ? Blocks.sandstone :
									  connection.facing == Facing4.EAST_POSX ? Blocks.emerald_block : Blocks.wool;
						
						structureWorld.setBlock(piecePos.getX()+connection.offsetX,piecePos.getY()+connection.offsetY,piecePos.getZ()+connection.offsetZ,block);
					}
				});
				
				structureWorld.generateInWorld(world,world.rand,MathUtil.floor(player.posX)-8,MathUtil.floor(player.posY)+3,MathUtil.floor(player.posZ)-8);
			}
			else if (args[0].equals("vanilla")){
				MapGenStronghold stronghold = new MapGenStronghold();
				int x = MathUtil.floor(player.posX)>>4, z = MathUtil.floor(player.posZ)>>4;
				StructureBoundingBox box = new StructureBoundingBox(-99999,0,-99999,99999,256,99999);
				
				try{
					Field f = MapGenStructure.class.getDeclaredField("field_143029_e");
					Method m = MapGenStronghold.class.getDeclaredMethod("getStructureStart",int.class,int.class);
					
					f.setAccessible(true);
					f.set(stronghold,new MapGenStructureData("TMPSTRONGHOLD"));
					
					m.setAccessible(true);
					StructureStart start = (StructureStart)m.invoke(stronghold,x,z);
					
					f = MapGenStructure.class.getDeclaredField("structureMap");
					f.setAccessible(true);
					((Map)f.get(stronghold)).put(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x,z)),start);
					start.generateStructure(world,world.rand,box);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		}
	};
}

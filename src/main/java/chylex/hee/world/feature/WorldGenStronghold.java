package chylex.hee.world.feature;
import gnu.trove.set.hash.TIntHashSet;
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
import chylex.hee.world.feature.stronghold.corridors.StrongholdEndWallDecorations;
import chylex.hee.world.feature.stronghold.corridors.StrongholdEndWaterfall;
import chylex.hee.world.feature.stronghold.corridors.StrongholdStairsStraight;
import chylex.hee.world.feature.stronghold.corridors.StrongholdStairsVertical;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorGrates;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorSmall;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorTorches;
import chylex.hee.world.feature.stronghold.doors.StrongholdDoorWooden;
import chylex.hee.world.feature.stronghold.rooms.decorative.*;
import chylex.hee.world.feature.stronghold.rooms.general.StrongholdRoomEndPortal;
import chylex.hee.world.feature.stronghold.rooms.general.StrongholdRoomLibrary;
import chylex.hee.world.feature.stronghold.rooms.general.StrongholdRoomScriptorium;
import chylex.hee.world.feature.stronghold.rooms.general.StrongholdRoomWorkshop;
import chylex.hee.world.feature.stronghold.rooms.loot.*;
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
	
	public static final WeightedLootTable lootGeneral = new WeightedLootTable();
	public static final WeightedLootTable lootLibrary = new WeightedLootTable();
	
	static{
		lootGeneral.addLoot(Items.iron_ingot).setAmount(1,5).setWeight(100);
		lootGeneral.addLoot(ItemList.ethereum).setAmount(1,2).setWeight(95);
		lootGeneral.addLoot(Items.gold_ingot).setAmount(1,3).setWeight(92);
		lootGeneral.addLoot(Items.rotten_flesh).setAmount(1,4).setWeight(82);
		lootGeneral.addLoot(Items.ender_pearl).setAmount(1).setWeight(80);
		lootGeneral.addLoot(Items.leather).setAmount(1,6,RandomAmount.aroundCenter).setWeight(76);
		lootGeneral.addLoot(ItemList.knowledge_note).setAmount(1).setWeight(72); // TODO add processor
		lootGeneral.addLoot(Items.book).setAmount(1).setWeight(70);
		lootGeneral.addLoot(Blocks.obsidian).setAmount(2,3).setWeight(58);
		lootGeneral.addLoot(Items.bowl).setAmount(1,3,RandomAmount.preferSmaller).setWeight(40);
		lootGeneral.addLoot(Items.string).setAmount(1,3).setWeight(36);
		lootGeneral.addLoot(Items.dye).setDamage(Meta.getDye(BlockColor.BLACK)).setAmount(1).setWeight(30);
		lootGeneral.addLoot(Items.glass_bottle).setAmount(1).setWeight(22);
		lootGeneral.addLoot(Items.potionitem).setAmount(1).setWeight(22);
		lootGeneral.addLoot(Items.diamond).setAmount(1,2).setWeight(14);
		
		lootLibrary.addLoot(Items.paper).setAmount(1,3).setWeight(50);
		lootLibrary.addLoot(Items.book).setAmount(1,2).setWeight(45);
		lootLibrary.addLoot(ItemList.knowledge_note).setAmount(1).setWeight(42); // TODO add processor
		lootLibrary.addLoot(ItemList.ancient_dust).setAmount(1,2).setWeight(36);
		lootLibrary.addLoot(Items.compass).setAmount(1).setWeight(22);
		lootLibrary.addLoot(Items.map).setAmount(1).setWeight(20);
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
			generator.setPiecesBetweenRooms(1,9);
			generator.setAttemptMultiplier(6);
		});
		
		stronghold.setPieceAmount(45,50);
		stronghold.setStartingPiece(new StrongholdRoomEndPortal());
		
		// corridors
		stronghold.addPieces(20,new Range(0,8),StrongholdCorridorStraight.generateCorridors(3,5,7));
		stronghold.addPieces(10,new Range(0,8),StrongholdCorridorIntersection.generateCorners());
		stronghold.addPieces(14,new Range(0,5),StrongholdCorridorIntersection.generateThreeWay());
		stronghold.addPieces(6,new Range(0,5),StrongholdCorridorIntersection.generateFourWay());
		
		stronghold.addPieces(2,new Range(0,1),StrongholdCorridorChest.generateCorridors());
		stronghold.addPieces(1,new Range(0,1),StrongholdCorridorDoubleChest.generateCorridors());
		
		stronghold.addPieces(4,new Range(0,1),StrongholdStairsStraight.generateStairs());
		stronghold.addPieces(3,new Range(0,1),StrongholdStairsVertical.generateStairs(1));
		stronghold.addPieces(1,new Range(0,1),StrongholdStairsVertical.generateStairs(2));
		
		stronghold.addPieces(10,new Range(0,2),StrongholdDoorSmall.generateDoors());
		stronghold.addPieces(10,new Range(0,2),StrongholdDoorWooden.generateDoors());
		stronghold.addPieces(10,new Range(0,2),StrongholdDoorGrates.generateDoors());
		stronghold.addPieces(10,new Range(0,2),StrongholdDoorTorches.generateDoors());
		
		// rooms
		stronghold.addPieces(3,new Range(0,3),StrongholdEndWaterfall.generateDeadEnds());
		stronghold.addPieces(3,new Range(0,3),StrongholdEndWallDecorations.generateDeadEnds());
		
		stronghold.addPieces(8,new Range(1,1),StrongholdRoomLibrary.generateLibraries());
		stronghold.addPieces(6,new Range(1,1),StrongholdRoomScriptorium.generateScriptoriums());
		stronghold.addPiece(6,new Range(1,1),new StrongholdRoomWorkshop());
		
		// TODO pregenerate
		stronghold.addPiece(4,new Range(1,1),new StrongholdRoomClusterFloating());
		stronghold.addPiece(4,new Range(1,1),new StrongholdRoomClusterPillar());
		stronghold.addPiece(4,new Range(1,1),new StrongholdRoomClusterWaterfall());
		stronghold.addPiece(4,new Range(1,1),new StrongholdRoomClusterIntersection());
		
		stronghold.addPiece(4,new Range(0,3),new StrongholdRoomChestIntersection());
		stronghold.addPieces(4,new Range(0,3),StrongholdRoomChestPool.generateRooms());
		stronghold.addPieces(4,new Range(0,3),StrongholdRoomChestStraight.generateChestRooms());
		stronghold.addPiece(4,new Range(0,3),new StrongholdRoomChestSupportSmall());
		//
		
		stronghold.addPiece(3,new Range(0,2),new StrongholdRoomSilverfishTrap());
		stronghold.addPiece(3,new Range(0,2),new StrongholdRoomLargeIntersectionTrap());
		stronghold.addPieces(3,new Range(1,2),StrongholdRoomPrisonTrap.generatePrisons());
		
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomArches());
		stronghold.addPiece(6,new Range(0,3),new StrongholdRoomFountain());
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomFountainCeiling());
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomHugeIntersection());
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomLargeIntersection());
		stronghold.addPieces(6,new Range(0,3),StrongholdRoomLitCorners.generateColors());
		stronghold.addPiece(6,new Range(0,3),new StrongholdRoomLitPole());
		stronghold.addPiece(6,new Range(0,3),new StrongholdRoomLitTotem());
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomLowerCorners());
		stronghold.addPiece(6,new Range(0,3),new StrongholdRoomSmallIntersection());
		stronghold.addPiece(6,new Range(0,2),new StrongholdRoomStairSnake());
		
		// relics
		StrongholdPiece[][] relicRooms = new StrongholdPiece[][]{
			StrongholdRoomRelicDungeon.generateRelicRooms(),
			StrongholdRoomRelicFountains.generateRelicRooms(),
			StrongholdRoomRelicHell.generateRelicRooms()
		};
		
		TIntHashSet relicIndexes = new TIntHashSet(new int[]{ 0, 1, 2 });
		relicIndexes.remove(rand.nextInt(relicIndexes.size()));
		
		for(int index:relicIndexes.toArray())stronghold.addPieces(5,new Range(1,1),relicRooms[index]);
		
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
				
				pieces.addAll(StrongholdRoomPrisonTrap.generatePrisons());
				
				/*pieces.addAll(StrongholdCorridorStraight.generateCorridors(5));
				pieces.addAll(StrongholdCorridorIntersection.generateCorners());
				pieces.addAll(StrongholdCorridorIntersection.generateThreeWay());
				pieces.addAll(StrongholdCorridorIntersection.generateFourWay());
				pieces.addAll(StrongholdCorridorChest.generateCorridors());
				pieces.addAll(StrongholdCorridorDoubleChest.generateCorridors());
				pieces.addAll(StrongholdStairsStraight.generateStairs());
				pieces.addAll(StrongholdStairsVertical.generateStairs(1));
				pieces.addAll(StrongholdEndWaterfall.generateDeadEnds());
				pieces.addAll(StrongholdEndWallDecorations.generateDeadEnds());
				
				pieces.addAll(StrongholdDoorGrates.generateDoors());
				pieces.addAll(StrongholdDoorSmall.generateDoors());
				pieces.addAll(StrongholdDoorTorches.generateDoors());
				pieces.addAll(StrongholdDoorWooden.generateDoors());
				
				pieces.add(new StrongholdRoomEndPortal());
				pieces.addAll(StrongholdRoomLibrary.generateLibraries());
				pieces.addAll(StrongholdRoomScriptorium.generateScriptoriums());
				pieces.add(new StrongholdRoomWorkshop());
				
				pieces.add(new StrongholdRoomClusterFloating());
				pieces.add(new StrongholdRoomClusterPillar());
				pieces.add(new StrongholdRoomClusterWaterfall());
				pieces.add(new StrongholdRoomClusterIntersection());
				pieces.add(new StrongholdRoomChestIntersection());
				pieces.addAll(StrongholdRoomChestPool.generateRooms());
				pieces.addAll(StrongholdRoomChestStraight.generateChestRooms());
				pieces.add(new StrongholdRoomChestSupportSmall());
				
				pieces.addAll(StrongholdRoomRelicDungeon.generateRelicRooms());
				pieces.addAll(StrongholdRoomRelicFountains.generateRelicRooms());
				pieces.addAll(StrongholdRoomRelicHell.generateRelicRooms());
				
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
				pieces.add(new StrongholdRoomLitTotem());
				pieces.add(new StrongholdRoomLowerCorners());
				pieces.add(new StrongholdRoomSmallIntersection());
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

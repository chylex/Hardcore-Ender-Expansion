package chylex.hee.world.feature;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
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
import chylex.hee.world.feature.stronghold.rooms.StrongholdRoomEndPortal;
import chylex.hee.world.feature.stronghold.rooms.decorative.StrongholdRoomLargeIntersection;
import chylex.hee.world.feature.stronghold.rooms.traps.StrongholdRoomLargeIntersectionTrap;
import chylex.hee.world.feature.stronghold.rooms.traps.StrongholdRoomSilverfishTrap;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonPiece.Connection;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
import chylex.hee.world.structure.util.Facing4;
import chylex.hee.world.structure.util.Range;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenStronghold implements IWorldGenerator{
	private static final StructureDungeon stronghold = new StructureDungeon(128,48,128);
	
	static{
		//stronghold.setPieceAmount(28,36);
		//stronghold.setPieceAmount(0,36);
		//stronghold.setStartingPiece(new StrongholdPieceEndPortal());
		//stronghold.addPiece(new StrongholdPieceCorridor(1,true));
		//stronghold.addPiece(new StrongholdPieceCorridor(1,false));
	}
	
	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider){
		if (world.provider.dimensionId != 0)return;
		
		/*stronghold.generateInWorld(world,rand,chunkX+8,80,chunkZ+8);*/
		// TODO
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			if (args.length < 1)return;
			
			if (args[0].equals("custom")){
				StructureDungeon stronghold = new StructureDungeon(128,48,128);
				stronghold.setPieceAmount(150,200);
				stronghold.setStartingPiece(new StrongholdRoomEndPortal());
				
				stronghold.addPieces(6,new Range(0,50),StrongholdCorridorStraight.generateCorridors(2,3,7));
				stronghold.addPieces(4,new Range(0,50),StrongholdCorridorIntersection.generateCorners());
				stronghold.addPieces(2,new Range(0,50),StrongholdCorridorIntersection.generateThreeWay());
				stronghold.addPieces(1,new Range(0,50),StrongholdCorridorIntersection.generateFourWay());
				
				stronghold.addPieces(2,new Range(0,40),StrongholdDoorSmall.generateDoors());
				stronghold.addPieces(2,new Range(0,40),StrongholdDoorWooden.generateDoors());
				stronghold.addPieces(2,new Range(0,40),StrongholdDoorGrates.generateDoors());
				stronghold.addPieces(2,new Range(0,40),StrongholdDoorTorches.generateDoors());
				
				stronghold.addPieces(3,new Range(0,15),StrongholdStairsStraight.generateStairs());
				stronghold.addPieces(3,new Range(0,10),StrongholdStairsVertical.generateStairs(1));
				stronghold.addPieces(1,new Range(0,5),StrongholdStairsVertical.generateStairs(2));
				
				stronghold.addPieces(1,new Range(0,15),StrongholdCorridorChest.generateCorridors());
				stronghold.addPieces(1,new Range(0,6),StrongholdCorridorDoubleChest.generateCorridors());
				
				stronghold.addPiece(3,new Range(1,2),new StrongholdRoomSilverfishTrap());
				
				stronghold.tryGenerateInWorld(world,world.rand,MathUtil.floor(player.posX)-8,MathUtil.floor(player.posY)-30,MathUtil.floor(player.posZ)-8,1);
			}
			else if (args[0].equals("pieces")){
				CustomArrayList<StrongholdPiece> pieces = new CustomArrayList<>();
				pieces.add(new StrongholdRoomEndPortal());
				pieces.addAll(StrongholdCorridorStraight.generateCorridors(5));
				pieces.addAll(StrongholdCorridorIntersection.generateCorners());
				pieces.addAll(StrongholdCorridorIntersection.generateThreeWay());
				pieces.addAll(StrongholdCorridorIntersection.generateFourWay());
				pieces.addAll(StrongholdDoorSmall.generateDoors());
				pieces.addAll(StrongholdDoorWooden.generateDoors());
				pieces.addAll(StrongholdDoorGrates.generateDoors());
				pieces.addAll(StrongholdDoorTorches.generateDoors());
				pieces.addAll(StrongholdStairsStraight.generateStairs());
				pieces.addAll(StrongholdStairsVertical.generateStairs(1));
				pieces.addAll(StrongholdCorridorChest.generateCorridors());
				pieces.addAll(StrongholdCorridorDoubleChest.generateCorridors());
				pieces.add(new StrongholdRoomSilverfishTrap());
				pieces.add(new StrongholdRoomLargeIntersection());
				pieces.add(new StrongholdRoomLargeIntersectionTrap());
				
				int fullWidth = pieces.stream().mapToInt(piece -> piece.size.sizeX+2).sum();
				
				StructureWorld structureWorld = new StructureWorld(fullWidth/2,48,32);
				PosMutable pos = new PosMutable(-fullWidth/2,0,0);
				
				pieces.stream().map(piece -> new StructureDungeonPieceInst(piece,pos.move(piece.size.sizeX+2,0,0).offset(-piece.size.sizeX,0,-piece.size.sizeZ/2))).forEach(inst -> {
					Pos piecePos = inst.boundingBox.getTopLeft();
					inst.useAllConnections();
					inst.piece.generate(inst,structureWorld,world.rand,piecePos.getX(),piecePos.getY(),piecePos.getZ());
					
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

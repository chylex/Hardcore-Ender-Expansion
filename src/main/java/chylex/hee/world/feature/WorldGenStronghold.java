package chylex.hee.world.feature;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureStart;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.stronghold.corridors.StrongholdPieceCorridor;
import chylex.hee.world.feature.stronghold.corridors.StrongholdPieceIntersection;
import chylex.hee.world.feature.stronghold.rooms.StrongholdPieceEndPortal;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.dungeon.StructureDungeon;
import chylex.hee.world.structure.dungeon.StructureDungeonPieceInst;
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
		@SuppressWarnings({ "unused", "null" })
		@Override
		public void run(String...args){
			if (args.length < 1)return;
			
			if (args[0].equals("custom")){
				StructureDungeon stronghold = new StructureDungeon(128,48,128);
				stronghold.setPieceAmount(10,10);
				stronghold.setStartingPiece(new StrongholdPieceEndPortal());
				
				stronghold.addPieces(4,new Range(0,20),StrongholdPieceCorridor.generateCorridors(2,3,7));
				stronghold.addPieces(3,new Range(0,20),StrongholdPieceIntersection.generateCorners());
				stronghold.addPieces(2,new Range(0,20),StrongholdPieceIntersection.generateThreeWay());
				stronghold.addPieces(1,new Range(0,20),StrongholdPieceIntersection.generateFourWay());
				
				stronghold.tryGenerateInWorld(world,world.rand,MathUtil.floor(player.posX)-8,MathUtil.floor(player.posY)-30,MathUtil.floor(player.posZ)-8,1);
			}
			else if (args[0].equals("piece")){
				StructureDungeonPieceInst inst = null;
				// edit on runtime
				
				if (inst != null){
					StructureWorld structureWorld = new StructureWorld(128,48,128);
					inst.piece.generate(inst,structureWorld,world.rand,0,24,0);
					structureWorld.generateInWorld(world,world.rand,MathUtil.floor(player.posX)-8,MathUtil.floor(player.posY)-30,MathUtil.floor(player.posZ)-8);
				}
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

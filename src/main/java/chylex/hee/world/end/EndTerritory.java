package chylex.hee.world.end;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.world.end.TerritoryGenerator.ITerritoryGeneratorConstructor;
import chylex.hee.world.end.gen.TerritoryTheHub;
import chylex.hee.world.structure.StructureWorld;

public enum EndTerritory{
	THE_HUB(24, new TerritorySpawnInfo(80,0), TerritoryTheHub::new), // 384 blocks
	;
	
	public final int chunkSize;
	private final TerritorySpawnInfo info;
	private final ITerritoryGeneratorConstructor constructor;
	
	private EndTerritory(int chunkSize, TerritorySpawnInfo info, ITerritoryGeneratorConstructor constructor){
		this.chunkSize = chunkSize;
		this.info = info;
		this.constructor = constructor;
	}
	
	public StructureWorld createWorld(World world){
		return new StructureWorld(world,8*chunkSize,info.getHeight(),8*chunkSize);
	}
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	/**
	 * Finds the top left chunk for a territory using magic. They are generated in this manner:<br>
	 * {@code 7|5} <br>
	 * {@code 3|1} <br>
	 * {@code 2|0} <br>
	 * {@code 6|4} <br>
	 * This was tested using modified values that cannot be used in production. Do not touch this. Ever.
	 */
	public static ChunkCoordIntPair getStartPoint(EndTerritory territory, int index){
		int chunkOffX = 0;
		
		if ((index/2)%2 == 0)chunkOffX = IntStream.range(0,territory.ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // positive x
		else chunkOffX = -IntStream.rangeClosed(1,territory.ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // negative x
		
		int distZ = index/4;
		if (index%2 != 0)distZ = -distZ-1; // moves odd indexes to negative z
		
		int chunkOffZ = distZ*(chunksBetween+territory.chunkSize);
		
		return new ChunkCoordIntPair(chunkOffX+chunkOffset,chunkOffZ+chunkOffset);
	}
	
	public static void generateTerritory(EndTerritory territory, ChunkCoordIntPair startPoint, World world, Random rand){
		for(int chunkX = 0; chunkX < territory.chunkSize; chunkX++){
			for(int chunkZ = 0; chunkZ < territory.chunkSize; chunkZ++){
				world.getChunkFromChunkCoords(startPoint.chunkXPos+chunkX,startPoint.chunkZPos+chunkZ);
			}
		}
		
		StructureWorld structureWorld = territory.createWorld(world);
		territory.constructor.construct(structureWorld,rand).generate();
		structureWorld.generateInWorld(world,rand,16*startPoint.chunkXPos+structureWorld.getArea().x2,territory.info.getBottomY(rand),16*startPoint.chunkZPos+structureWorld.getArea().z2);
	}
	
	public static void generateTerritory(EndTerritory territory, int index, World world, Random rand){
		generateTerritory(territory,getStartPoint(territory,index),world,rand);
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			generateTerritory(THE_HUB,0,world,world.rand);
		}
	};
}

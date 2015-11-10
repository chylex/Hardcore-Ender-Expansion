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

	/**
	 * Finds the top left chunk for the territory using magic. They are generated in this manner:<br>
	 * {@code 7|5} <br>
	 * {@code 3|1} <br>
	 * {@code 2|0} <br>
	 * {@code 6|4} <br>
	 * This was tested using modified values that cannot be used in production. Do not touch this. Ever.
	 */
	public ChunkCoordIntPair getStartPoint(int index){
		int chunkOffX = 0;
		
		if ((index/2)%2 == 0)chunkOffX = IntStream.range(0,ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // positive x
		else chunkOffX = -IntStream.rangeClosed(1,ordinal()).map(level -> chunksBetween+values[level].chunkSize).sum(); // negative x
		
		int distZ = index/4;
		if (index%2 != 0)distZ = -distZ-1; // moves odd indexes to negative z
		
		int chunkOffZ = distZ*(chunksBetween+chunkSize);
		
		return new ChunkCoordIntPair(chunkOffX+chunkOffset,chunkOffZ+chunkOffset);
	}
	
	/**
	 * Preloads required chunks and generates the territory in a specified location. For {@code useChunkDirectly}, see {@link StructureWorld#generateInWorld(World,Random,int,int,int,boolean)}.
	 */
	public void generateTerritory(ChunkCoordIntPair startPoint, World world, Random rand, boolean useChunkDirectly){
		for(int chunkX = 0; chunkX < chunkSize; chunkX++){
			for(int chunkZ = 0; chunkZ < chunkSize; chunkZ++){
				world.getChunkFromChunkCoords(startPoint.chunkXPos+chunkX,startPoint.chunkZPos+chunkZ);
			}
		}
		
		StructureWorld structureWorld = createWorld(world);
		constructor.construct(structureWorld,rand).generate();
		structureWorld.generateInWorld(world,rand,16*startPoint.chunkXPos+structureWorld.getArea().x2,info.getBottomY(rand),16*startPoint.chunkZPos+structureWorld.getArea().z2,true);
	}
	
	/**
	 * Preloads required chunks and generates the territory using the custom index based distribution system.
	 */
	public void generateTerritory(int index, World world, Random rand){
		generateTerritory(getStartPoint(index),world,rand,false);
	}
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			TerritoryTheHub.$regenerate = true;
			THE_HUB.generateTerritory(THE_HUB.getStartPoint(0),world,new Random(world.getSeed()),true);
		}
	};
}

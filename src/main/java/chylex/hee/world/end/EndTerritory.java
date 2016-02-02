package chylex.hee.world.end;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.ColorUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.TerritoryGenerator.ITerritoryGeneratorConstructor;
import chylex.hee.world.end.gen.TerritoryArcaneConjunctions;
import chylex.hee.world.end.gen.TerritoryForgottenTombs;
import chylex.hee.world.end.gen.TerritoryTest;
import chylex.hee.world.end.gen.TerritoryTheHub;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldLazy;
import chylex.hee.world.util.BoundingBox;

/**
 * YOU BETTER NOT FUCKING DARE CHANGE THE ORDER OR INSERT NEW ELEMENTS IN THE MIDDLE BECAUSE IT WILL MESS UP ORDINALS
 */
public enum EndTerritory{
	THE_HUB( // 384 blocks
		size(24), height(128), bottom(0), color(0,0,0), new TerritorySpawnGenerator.Empty(),
		TerritoryProperties.defaultProperties, new TerritoryTheHub.Environment(), TerritoryTheHub::new
	),
	
	FORGOTTEN_TOMBS( // 448 blocks
		size(28), height(128), bottom(64), color(253), new TerritorySpawnGenerator.Origin(),
		new TerritoryTest.Properties(), TerritoryEnvironment.defaultEnvironment, TerritoryForgottenTombs::new
	),
	
	ARCANE_CONJUNCTIONS(
		size(20), height(128), bottom(0), color(253), new TerritorySpawnGenerator.InSquareCenter(0.75D),
		TerritoryProperties.defaultProperties, TerritoryEnvironment.defaultEnvironment, TerritoryArcaneConjunctions::new
	);
	
	private final int chunkSize;
	private final int height;
	private final int bottom;
	
	private final TerritorySpawnGenerator spawn;
	private final ITerritoryGeneratorConstructor constructor;
	
	public final TerritoryProperties properties;
	public final TerritoryEnvironment environment;
	public final int tokenColor;
	
	private EndTerritory(int chunkSize, int height, int bottom, int tokenColor, TerritorySpawnGenerator spawn, TerritoryProperties properties, TerritoryEnvironment environment, ITerritoryGeneratorConstructor constructor){
		this.chunkSize = chunkSize;
		this.height = height;
		this.bottom = bottom;
		this.tokenColor = tokenColor;
		this.spawn = spawn;
		this.properties = properties;
		this.environment = environment;
		this.constructor = constructor;
	}
	
	public boolean canGenerate(){
		return ordinal() != 0;
	}
	
	public Random createRandom(long seed, int index){
		Random rand = new Random(seed);
		rand.setSeed(rand.nextLong()^(66L*index)+ordinal()*rand.nextInt());
		return rand;
	}
	
	public BoundingBox createBoundingBox(){
		final int rad = 8*chunkSize;
		return new BoundingBox(Pos.at(-rad,0,-rad),Pos.at(rad,255,rad));
	}
	
	public StructureWorld createWorld(World world){
		StructureWorldLazy structureWorld = new StructureWorldLazy(world,8*chunkSize,height,8*chunkSize);
		
		if ($debugging){
			structureWorld.setSendToWatchers();
			structureWorld.clearArea(Blocks.air,0);
		}
		
		return structureWorld;
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
	
	public long getHashFromIndex(int index){
		ChunkCoordIntPair startPoint = getStartPoint(index);
		return Pos.at(startPoint.chunkXPos*16+chunkSize*8,ordinal(),startPoint.chunkZPos*16+chunkSize*8).toLong();
	}
	
	public long getHashFromPoint(Pos centerPos){
		return centerPos.offset(0,ordinal()-centerPos.getY(),0).toLong();
	}
	
	public Pos generateTerritory(ChunkCoordIntPair startPoint, World world, Random rand, EnumSet<? extends Enum<?>> variations, boolean isRare){
		for(int chunkX = 0; chunkX < chunkSize; chunkX++){
			for(int chunkZ = 0; chunkZ < chunkSize; chunkZ++){
				world.getChunkFromChunkCoords(startPoint.chunkXPos+chunkX,startPoint.chunkZPos+chunkZ);
			}
		}
		
		StructureWorld structureWorld = createWorld(world);
		constructor.construct(this,variations,structureWorld,rand).generate();
		
		int startX = 16*startPoint.chunkXPos+structureWorld.getArea().x2;
		int startZ = 16*startPoint.chunkZPos+structureWorld.getArea().z2;
		
		if ($debugging){
			EntitySelector.any(world,structureWorld.getArea().offset(Pos.at(startX,0,startZ)).toAABB()).stream().filter(e -> !(e instanceof EntityPlayer)).forEach(Entity::setDead);
		}
		
		Pos spawnPos = spawn.createSpawnPoint(structureWorld,rand,this,isRare).offset(startX,bottom,startZ);
		structureWorld.generateInWorld(world,rand,startX,bottom,startZ);
		return spawnPos;
	}
	
	public Pos generateTerritory(int index, World world, Random rand, EnumSet<? extends Enum<?>> variations, boolean isRare){
		return generateTerritory(getStartPoint(index),world,rand,variations,isRare);
	}
	
	// STATIC FIELDS AND METHODS
	
	public static final int chunksBetween = 64; // 1024 blocks
	public static final int chunkOffset = -THE_HUB.chunkSize/2;
	public static final EndTerritory[] values = values();
	
	public static Optional<EndTerritory> getFromHash(long hash){
		return CollectionUtil.get(values,Pos.at(hash).getY());
	}
	
	/**
	 * If you touch this, your pet fish will die.
	 */
	public static @Nullable Pair<Pos,EndTerritory> findTerritoryCenter(double posX, double posZ){
		int middleX = chunkOffset*16;
		
		for(EndTerritory territory:values){
			if (Math.abs(Math.abs(posX)-(middleX+territory.chunkSize*8)) < (territory.chunkSize+chunksBetween)*8){
				final int gridSize = (territory.chunkSize+chunksBetween)*16;
				final int offset = chunkOffset*16+territory.chunkSize*8;
				
				int xStart = (posX >= 0D ? 1 : -1)*(middleX+territory.chunkSize*8);
				int zStart = posZ >= chunkOffset*16 ? gridSize*((MathUtil.floor(posZ)+gridSize/2-offset)/gridSize)+offset :
				                                      gridSize*((MathUtil.floor(posZ)-gridSize/2-offset)/gridSize)+offset;
				
				return Pair.of(Pos.at(xStart,0,zStart),territory);
			}
			
			middleX += (territory.chunkSize+chunksBetween)*16;
		}
		
		return null;
	}
	
	/**
	 * Do you wanna have a bad time? No? Then don't touch this.
	 */
	public static @Nullable EndTerritory fromPosition(double posX){
		int middlePoint = chunkOffset*16;
		
		for(EndTerritory territory:values){
			if (Math.abs(Math.abs(posX)-(middlePoint+territory.chunkSize*8)) < (territory.chunkSize+chunksBetween)*8){
				return territory;
			}
			
			middlePoint += (territory.chunkSize+chunksBetween)*16;
		}
		
		return null;
	}
	
	/**
	 * Returns how close the entity is to the void, starting from 0 with no upper bound.
	 * Values around 1 should show negative effects, eventually trying to kill the entity more violently as the value continues to increase.
	 */
	public static double getVoidFactor(Entity entity){
		Pair<Pos,EndTerritory> info = findTerritoryCenter(entity.posX,entity.posZ);
		if (info == null)return 0D;
		
		double diffX = Math.abs(info.getKey().getX()-entity.posX);
		double diffZ = Math.abs(info.getKey().getZ()-entity.posZ);
		
		double distXZ = Math.pow(Math.pow(diffX,2.5D)+Math.pow(diffZ,2.5D),0.4D)/((info.getValue().chunkSize+2)*8);
		double offY = Math.pow(Math.abs(128D-entity.posY)/164D,6D);
		
		return Math.max(0D,distXZ*3.5D-3D)+Math.max(0D,offY);
	}
	
	// INITIALIZATION UTILITY METHODS
	
	private static int size(int chunkSize){
		return chunkSize;
	}
	
	private static int height(int height){
		return height;
	}
	
	private static int bottom(int bottom){
		return bottom;
	}
	
	private static int color(int hue){
		float[] rgb = ColorUtil.hsvToRgb(hue/360F,0.32F,0.76F);
		return (MathUtil.floor(rgb[0]*255F)<<16)|(MathUtil.floor(rgb[1]*255F)<<8)|MathUtil.floor(rgb[2]*255F);
	}
	
	private static int color(int red, int green, int blue){
		return (red<<16)|(green<<8)|blue;
	}
	
	// DEBUG
	
	private static boolean $debugging = false;
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			$debugging = true;
			
			if (args.length == 0){
				THE_HUB.generateTerritory(0,world,new Random(world.getSeed()),EmptyEnumSet.get(),false);
			}
			else if (args[0].equals("spawn") && args.length >= 3){
				values[DragonUtil.tryParse(args[1],0)].generateTerritory(DragonUtil.tryParse(args[2],0),world,new Random(world.getSeed()),EmptyEnumSet.get(),false);
			}
			else if (args[0].equals("seed") && args.length >= 4){
				values[DragonUtil.tryParse(args[1],0)].generateTerritory(DragonUtil.tryParse(args[2],0),world,new Random(DragonUtil.tryParse(args[3],0)),EmptyEnumSet.get(),false);
			}
			else if (args[0].equals("loc") && args.length >= 3){
				EndTerritory territory = values[DragonUtil.tryParse(args[1],0)];
				ChunkCoordIntPair coords = territory.getStartPoint(DragonUtil.tryParse(args[2],0));
				HardcoreEnderExpansion.notifications.report("Start: "+(coords.chunkXPos*16)+", "+(coords.chunkZPos*16));
				HardcoreEnderExpansion.notifications.report("Center: "+(coords.chunkXPos*16+territory.chunkSize*8)+", "+(coords.chunkZPos*16+territory.chunkSize*8));
			}
			else if (args[0].equals("find") && args.length >= 3){
				int x = DragonUtil.tryParse(args[1],0), z = DragonUtil.tryParse(args[2],0);
				Log.reportedDebug("Found: $0",findTerritoryCenter(x,z));
			}
		}
	};
}

package chylex.hee.world.structure.island.biome;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.structure.island.ComponentScatteredFeatureIsland;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public abstract class IslandBiomeBase{
	public static final IslandBiomeBase infestedForest = new IslandBiomeInfestedForest(0,KnowledgeRegistrations.INFESTED_FOREST_BIOME),
										burningMountains = new IslandBiomeBurningMountains(1,KnowledgeRegistrations.BURNING_MOUNTAINS_BIOME),
										enchantedIsland = new IslandBiomeEnchantedIsland(2,KnowledgeRegistrations.ENCHANTED_ISLAND_BIOME);
	
	public static final List<IslandBiomeBase> biomeList = new ArrayList<>(Arrays.asList(infestedForest,burningMountains,enchantedIsland));
	
	public static IslandBiomeBase pickRandomBiome(Random rand){
		int i = rand.nextInt(3);
		
		if (i == 0)return infestedForest;
		else if (i == 1)return burningMountains;
		else return enchantedIsland;
	}
	
	public final byte biomeID;
	private final KnowledgeRegistration knowledgeRegistration;
	
	public final WeightedList<SpawnEntry> spawnEntries;
	protected final WeightedList<BiomeContentVariation> contentVariations;
	protected final List<BiomeRandomDeviation> randomDeviations;
	
	protected IslandBiomeData data;
	
	protected IslandBiomeBase(int biomeID, KnowledgeRegistration knowledgeRegistration){
		this.biomeID = (byte)biomeID;
		this.knowledgeRegistration = knowledgeRegistration;
		
		this.spawnEntries = new WeightedList<>();
		this.contentVariations = new WeightedList<>();
		this.randomDeviations = new ArrayList<>();
	}
	
	public final IslandBiomeData generateData(Random rand){
		BiomeContentVariation contentVariation = contentVariations.getRandomItem(rand);
		
		List<BiomeRandomDeviation> deviations = new ArrayList<>();
		int deviationAmt = Math.max(0,Math.min(randomDeviations.size(),(int)Math.floor((rand.nextGaussian()+0.35D)*randomDeviations.size()*(0.5D+0.5D*rand.nextDouble()))));
		
		if (deviationAmt > 0){
			List<BiomeRandomDeviation> availableDeviations = new ArrayList<>(randomDeviations);
			
			for(int deviationAttempt = 0; deviationAttempt < deviationAmt; deviationAttempt++){
				BiomeRandomDeviation deviation = availableDeviations.remove(rand.nextInt(availableDeviations.size()));
				if (deviation.isCompatible(contentVariation))deviations.add(deviation);
			}
		}
		
		return new IslandBiomeData(contentVariation,deviations.toArray(new BiomeRandomDeviation[deviations.size()]));
	}
	
	public final IslandBiomeData getData(){
		return data;
	}
	
	public void prepareDecoration(IslandBiomeData data){
		this.data = data;
	}
	
	public final void decorateGen(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		getDecorator().begin(world,rand,centerX,centerZ,data);
		decorate(world,rand,centerX,centerZ);
		getDecorator().end();
	}
	
	protected abstract void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ);
	
	public void updateCore(World world, int x, int y, int z){
		if (world.playerEntities.isEmpty())return;
		
		if (world.rand.nextInt(3) == 0){
			short halfsz = ComponentScatteredFeatureIsland.halfSize;
			short playerCheck = (short)(halfsz*2);
			
			SpawnEntry entry = spawnEntries.getRandomItem(world.rand);
			if (entry == null)return;
			
			if (world.difficultySetting == EnumDifficulty.PEACEFUL && EntityMob.class.isAssignableFrom(entry.getMobClass()))return;
			
			int currentAmount = world.getEntitiesWithinAABB(entry.getMobClass(),AxisAlignedBB.getBoundingBox(x-halfsz,y+10,z-halfsz,x+halfsz,y+55,z+halfsz)).size();
			if (currentAmount >= entry.getMaxAmount() || world.rand.nextFloat()*1.1F<(float)currentAmount/entry.getMaxAmount())return;
			
			int playerAmount = world.playerEntities.size();
			
			for(int a = 0; a < Math.min(entry.getMaxAmount()>>2,world.rand.nextInt(1+entry.getMaxAmount()-currentAmount)); a++){
				EntityLiving e = entry.createMob(world);
				
				for(int attempt = 0; attempt < 20+Math.min(10,playerAmount); attempt++){
					Object o = world.playerEntities.get(world.rand.nextInt(playerAmount));
					EntityPlayer player = (EntityPlayer)o;
					
					if (MathUtil.distance(player.posX-x,player.posZ-z) > playerCheck)continue;
					
					double ang = world.rand.nextDouble()*2D*Math.PI,len = 19+world.rand.nextInt(55)+Math.abs(world.rand.nextGaussian()*12D);
					double posX = player.posX+Math.cos(ang)*len,posZ = player.posZ+Math.sin(ang)*len;
					
					for(int yAttempt = 0; yAttempt < 28; yAttempt++){
						e.setLocationAndAngles(player.posX+Math.cos(ang)*len,(int)Math.floor(player.posY+(world.rand.nextDouble()-0.65D)*(yAttempt+4)*3D)+0.01D,player.posZ+Math.sin(ang)*len,world.rand.nextFloat()*360F,0F);
						if (hasEntitySpace(world,e))break;
					}
					
					int xx = (int)Math.floor(e.posX),zz = (int)Math.floor(e.posZ);
					boolean hasBlockBelow = false;
					
					for(int yy = (int)e.posY-1; yy > e.posY-4D; yy--){
						if (world.getBlock(xx,yy,zz).isOpaqueCube()){
							hasBlockBelow = true;
							break;
						}
					}
					if (!hasBlockBelow || DragonUtil.getTopBlock(world,BlockList.end_terrain,xx,zz,120) <= 30)continue;
					
					e.setPositionAndUpdate(e.posX,e.posY+0.01D,e.posZ);
					world.spawnEntityInWorld(e);
					break;
				}
			}
		}
		
		if (world.rand.nextFloat() <= 0.063F){
			for(Object o:world.playerEntities){
				EntityPlayer player = (EntityPlayer)o;

				if (isPlayerMoving(player)){
					for(int ix = (int)player.posX, iy = y+6, iz = (int)player.posZ; iy < y+60; iy++){
						if (world.getBlock(ix,iy,iz) == getTopBlock() && world.getBlockMetadata(ix,iy,iz) == getTopBlockMeta()){
							knowledgeRegistration.tryUnlockFragment(player,0.12F);
							break;
						}
					}
				}
			}
		}
		
		for(Object o:world.playerEntities){
			EntityPlayer player = (EntityPlayer)o;

			if (isPlayerMoving(player)){
				int ix = (int)player.posX, iy = (int)player.posY-1, iz = (int)player.posZ;
				
				if (world.getBlock(ix,iy,iz) == getTopBlock() && world.getBlockMetadata(ix,iy,iz) == getTopBlockMeta()){
					player.addStat(AchievementManager.WHOLE_NEW_CULTURES,1);
				}
			}
		}
	}
	
	private static final boolean hasEntitySpace(World world, Entity entity){
		return world.checkNoEntityCollision(entity.boundingBox) && world.getCollidingBoundingBoxes(entity,entity.boundingBox).isEmpty() && !world.isAnyLiquid(entity.boundingBox);
	}
	
	private static final boolean isPlayerMoving(EntityPlayer player){
		return Math.abs(player.lastTickPosX-player.posX) > 0.0001D || Math.abs(player.lastTickPosZ-player.posZ) > 0.0001D;
	}
	
	public float getIslandSurfaceHeightMultiplier(){
		return 1F;
	}
	
	public float getIslandMassHeightMultiplier(){
		return 1F;
	}
	
	public float getIslandFillFactor(){
		return 1F;
	}
	
	public float getCaveAmountMultiplier(){
		return 1F;
	}
	
	public float getCaveBranchingChance(){
		return 0.04F;
	}
	
	public float getOreAmountMultiplier(){
		return 1F;
	}
	
	protected abstract IslandBiomeDecorator getDecorator();
	
	public int getTopBlockMeta(){
		return 0;
	}
	
	public static final Block getTopBlock(){
		return BlockList.end_terrain;
	}
}

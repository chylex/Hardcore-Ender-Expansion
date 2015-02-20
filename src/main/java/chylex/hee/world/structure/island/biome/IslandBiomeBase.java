package chylex.hee.world.structure.island.biome;
import gnu.trove.map.hash.TByteObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.entity.technical.EntityTechnicalBiomeInteraction;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction.BiomeInteraction;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.data.BiomeRandomDeviation;
import chylex.hee.world.structure.island.biome.data.IslandBiomeData;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public abstract class IslandBiomeBase{
	public static final IslandBiomeBase infestedForest = new IslandBiomeInfestedForest(0),
										burningMountains = new IslandBiomeBurningMountains(1),
										enchantedIsland = new IslandBiomeEnchantedIsland(2);
	
	public static final List<IslandBiomeBase> biomeList = CollectionUtil.newList(infestedForest,burningMountains,enchantedIsland);
	
	public static final IslandBiomeBase pickRandomBiome(Random rand){
		return biomeList.get(rand.nextInt(biomeList.size()));
	}
	
	public final byte biomeID;
	
	private final TByteObjectHashMap<WeightedList<SpawnEntry>> spawnEntries;
	protected final TByteObjectHashMap<CustomArrayList<BiomeInteraction>> interactions;
	protected final WeightedList<BiomeContentVariation> contentVariations;
	protected final List<BiomeRandomDeviation> randomDeviations;
	
	protected IslandBiomeData data;
	
	protected IslandBiomeBase(int biomeID){
		this.biomeID = (byte)biomeID;
		this.spawnEntries = new TByteObjectHashMap<>();
		this.interactions = new TByteObjectHashMap<>();
		this.contentVariations = new WeightedList<>();
		this.randomDeviations = new ArrayList<>();
	}
	
	public Collection<WeightedList<SpawnEntry>> getAllSpawnEntries(){
		return spawnEntries.valueCollection();
	}
	
	public WeightedList<SpawnEntry> getSpawnEntries(BiomeContentVariation contentVariation){
		spawnEntries.putIfAbsent(contentVariation.id,new WeightedList<SpawnEntry>());
		return spawnEntries.get(contentVariation.id);
	}
	
	public CustomArrayList<BiomeInteraction> getInteractions(BiomeContentVariation contentVariation){
		interactions.putIfAbsent(contentVariation.id,new CustomArrayList<BiomeInteraction>());
		return interactions.get(contentVariation.id);
	}
	
	public final IslandBiomeData generateData(Random rand){
		BiomeContentVariation contentVariation = contentVariations.getRandomItem(rand);
		
		List<BiomeRandomDeviation> available = new ArrayList<>(), selected = new ArrayList<>();
		
		for(BiomeRandomDeviation deviation:randomDeviations){
			if (deviation.isCompatible(contentVariation))available.add(deviation);
		}
		
		int deviationAmt = available.isEmpty() ? 0 : MathUtil.clamp(MathUtil.floor((rand.nextGaussian()+0.35D)*available.size()*(0.5D+0.5D*rand.nextDouble())),0,available.size());
		
		for(int deviationAttempt = 0; deviationAttempt < deviationAmt; deviationAttempt++){
			selected.add(available.remove(rand.nextInt(available.size())));
		}
		
		return new IslandBiomeData(contentVariation,selected.toArray(new BiomeRandomDeviation[selected.size()]));
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

	public boolean isValidMetadata(int meta){
		for(BiomeContentVariation variation:contentVariations){
			if (variation.id == meta)return true;
		}
		
		return false;
	}
	
	public void updateCore(World world, int x, int y, int z, int meta){
		if (world.playerEntities.isEmpty())return;
		
		if (world.rand.nextInt(3) == 0){
			int halfsz = ComponentIsland.halfSize, playerCheck = halfsz*2;
			
			SpawnEntry entry = spawnEntries.containsKey((byte)meta) ? spawnEntries.get((byte)meta).getRandomItem(world.rand) : null;
			if (entry == null)return;
			
			if (world.difficultySetting == EnumDifficulty.PEACEFUL && entry.isMob)return;
			
			int currentAmount = world.getEntitiesWithinAABB(entry.getMobClass(),AxisAlignedBB.getBoundingBox(x-halfsz,y+10,z-halfsz,x+halfsz,y+55,z+halfsz)).size();
			if (currentAmount >= entry.getMaxAmount() || world.rand.nextFloat()*1.1F < (float)currentAmount/entry.getMaxAmount())return;
			
			int playerAmount = world.playerEntities.size();
			
			for(int a = 0; a < Math.min(entry.getMaxAmount()>>2,world.rand.nextInt(1+entry.getMaxAmount()-currentAmount)); a++){
				EntityLiving e = entry.createMob(world);
				
				for(int attempt = 0; attempt < 20+Math.min(10,playerAmount); attempt++){
					EntityPlayer player = (EntityPlayer)world.playerEntities.get(world.rand.nextInt(playerAmount));
					if (MathUtil.distance(player.posX-x,player.posZ-z) > playerCheck)continue;
					
					double ang = world.rand.nextDouble()*2D*Math.PI, len = 19D+world.rand.nextInt(55)+Math.abs(world.rand.nextGaussian()*12D);
					double posX = player.posX+Math.cos(ang)*len, posZ = player.posZ+Math.sin(ang)*len;
					
					if (MathUtil.distance(posX-x+0.5D,posZ-z+0.5D) > 150D)continue;
					
					for(int yAttempt = 0; yAttempt < 28; yAttempt++){
						e.setLocationAndAngles(posX,MathUtil.floor(player.posY+(world.rand.nextDouble()-0.65D)*(yAttempt+4)*3D)+0.01D,posZ,world.rand.nextFloat()*360F,0F);
						if (hasEntitySpace(world,e))break;
					}
					
					int xx = MathUtil.floor(e.posX), zz = MathUtil.floor(e.posZ);
					boolean hasBlockBelow = false;
					
					for(int yy = (int)e.posY-1; yy > e.posY-4D; yy--){
						if (world.getBlock(xx,yy,zz).isOpaqueCube()){
							hasBlockBelow = true;
							break;
						}
					}
					
					if (!hasBlockBelow || DragonUtil.getTopBlockY(world,BlockList.end_terrain,xx,zz,120) <= 30)continue;
					
					e.setPositionAndUpdate(e.posX,e.posY+0.01D,e.posZ);
					world.spawnEntityInWorld(e);
					break;
				}
			}
		}
		
		BiomeContentVariation variation = null;
		
		for(BiomeContentVariation contentVariation:contentVariations){
			if (contentVariation.id == meta){
				variation = contentVariation;
				break;
			}
		}
		
		if (variation != null && interactions.containsKey(variation.id) && world.rand.nextInt(5) == 0){
			for(BiomeInteraction interaction:interactions.get(variation.id)){
				if (world.rand.nextInt(interaction.getRNG()) == 0){
					List<EntityTechnicalBiomeInteraction> list = world.getEntitiesWithinAABB(EntityTechnicalBiomeInteraction.class,AxisAlignedBB.getBoundingBox(x-1,y-1,z-1,x+2,y+2,z+2));
					byte instances = 0;
					
					for(EntityTechnicalBiomeInteraction interactionEntity:list){
						if (interactionEntity.getInteraction().getIdentifier().equals(interaction.getIdentifier()))++instances;
					}
					
					if (instances < interaction.getMaxInstances()){
						AbstractBiomeInteraction interactionInstance = interaction.create();
						if (interactionInstance != null)world.spawnEntityInWorld(new EntityTechnicalBiomeInteraction(world,x+0.5D,y+0.5D,z+0.5D,interactionInstance));
					}
				}
			}
		}
		
		for(EntityPlayer player:(List<EntityPlayer>)world.playerEntities){
			if (isPlayerMoving(player) && player instanceof EntityPlayerMP){
				int ix = (int)player.posX, iy = (int)player.posY-1, iz = (int)player.posZ;
				
				if (world.getBlock(ix,iy,iz) == getTopBlock() && world.getBlockMetadata(ix,iy,iz) == getTopBlockMeta() && !((EntityPlayerMP)player).func_147099_x().hasAchievementUnlocked(getAchievement())){ // OBFUSCATED getStatisticsFile
					player.addStat(getAchievement(),1);
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
	
	protected abstract Achievement getAchievement();
	
	public abstract int getTopBlockMeta();
	
	public static final Block getTopBlock(){
		return BlockList.end_terrain;
	}
}

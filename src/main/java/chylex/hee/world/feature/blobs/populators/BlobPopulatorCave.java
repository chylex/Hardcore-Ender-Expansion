package chylex.hee.world.feature.blobs.populators;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.Vec3M;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorCave extends BlobPopulator{
	private IRandomAmount fullAmountGen = IRandomAmount.exact, totalAmountGen = IRandomAmount.exact;
	private byte minFullCaveAmount, maxFullCaveAmount, minTotalCaveAmountLimit, maxTotalCaveAmountLimit, maxRecursion;
	private double minRad, maxRad, minRecursionChance, maxRecursionChance, minRecursionRadMp, maxRecursionRadMp;
	private boolean recursionChanceCached = false;
	
	private int tmpCavesLeft;
	private double tmpRecursionChance;
	
	public BlobPopulatorCave(int weight){
		super(weight);
	}
	
	public BlobPopulatorCave rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	/**
	 * Sets the amount of full caves that go across the full structure.
	 */
	public BlobPopulatorCave fullCaveAmount(IRandomAmount fullAmountGen, int minFullCaveAmount, int maxFullCaveAmount){
		this.fullAmountGen = fullAmountGen;
		this.minFullCaveAmount = (byte)minFullCaveAmount;
		this.maxFullCaveAmount = (byte)maxFullCaveAmount;
		return this;
	}
	
	/**
	 * Sets the maximum limit for amount of caves, including recursively generated ones.
	 */
	public BlobPopulatorCave totalCaveAmount(IRandomAmount totalAmountGen, int minTotalCaveAmountLimit, int maxTotalCaveAmountLimit){
		this.totalAmountGen = totalAmountGen;
		this.minTotalCaveAmountLimit = (byte)minTotalCaveAmountLimit;
		this.maxTotalCaveAmountLimit = (byte)maxTotalCaveAmountLimit;
		return this;
	}
	
	public BlobPopulatorCave recursionChance(double minRecursionChance, double maxRecursionChance, int maxRecursion){
		this.minRecursionChance = minRecursionChance;
		this.maxRecursionChance = maxRecursionChance;
		this.maxRecursion = (byte)maxRecursion;
		return this;
	}
	
	public BlobPopulatorCave recursionRadMp(double minRadMp, double maxRadMp){
		this.minRecursionRadMp = minRadMp;
		this.maxRecursionRadMp = maxRadMp;
		return this;
	}
	
	/**
	 * Recursion chance is generated only once.
	 */
	public BlobPopulatorCave cacheRecursionChance(){
		this.recursionChanceCached = true;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		tmpCavesLeft = totalAmountGen.generate(rand,minTotalCaveAmountLimit,maxTotalCaveAmountLimit);
		tmpRecursionChance = minRecursionChance+rand.nextDouble()*(maxRecursionChance-minRecursionChance);
		
		for(int amount = fullAmountGen.generate(rand,minFullCaveAmount,maxFullCaveAmount), attempt = 0, generated = 0; attempt < amount*5 && generated < amount; attempt++){
			if (genFullCave(gen,rand,minRad+rand.nextDouble()*(maxRad-minRad)))++generated;
		}
	}
	
	private boolean genFullCave(DecoratorFeatureGenerator gen, Random rand, double rad){
		int side = rand.nextInt(6);
		float x = 0F, y = 0F, z = 0F;
		
		if (side <= 3)x = rand.nextInt(32)-16;
		else x = side == 4 ? 15 : -16;
		
		if (side <= 1 || side >= 4)y = rand.nextInt(32)-16;
		else y = side == 2 ? 15 : -16;
		
		if (side >= 2)z = rand.nextInt(32)-16;
		else z = side == 0 ? 15 : -16;
		
		if (genCave(gen,rand,x,y,z,rad,new Vec3M(-x,-y,-z).normalize(),0)){
			--tmpCavesLeft;
			return true;
		}
		else return false;
	}
	
	private boolean genCave(DecoratorFeatureGenerator gen, Random rand, double x, double y, double z, double rad, Vec3M dirVec, int recursionLevel){
		if (tmpCavesLeft < 0 || recursionLevel > maxRecursion)return false;
		
		boolean generatedSomething = false;
		Vec3M dirChangeVec = null;
		double dirChangeMp = 0D;
		
		List<double[]> recursionLocs = new ArrayList<>();
		
		for(int a = 0; a < 50; a++){
			if (BlobGenerator.genBlob(gen,x,y,z,rad,Blocks.air))generatedSomething = true;
			
			if (a == 0 || rand.nextInt(10) == 0){
				dirChangeVec = DragonUtil.getRandomVector(rand);
				dirChangeMp = rand.nextDouble();
			}
			
			dirVec.x += dirChangeVec.x*dirChangeMp;
			dirVec.y += dirChangeVec.y*dirChangeMp;
			dirVec.z += dirChangeVec.z*dirChangeMp;
			
			x += dirVec.x;
			y += dirVec.y;
			z += dirVec.z;
			
			if (!recursionChanceCached && a > 0)tmpRecursionChance = (minRecursionChance+rand.nextDouble()*(maxRecursionChance-minRecursionChance))/(recursionLevel+1);
			if (rand.nextDouble() < tmpRecursionChance)recursionLocs.add(new double[]{ x, y, z });
		}
		
		if (generatedSomething){
			for(double[] loc:recursionLocs){
				Vec3M newVec = dirVec.crossProduct(DragonUtil.getRandomVector(rand)).normalize();
				
				if (genCave(gen,rand,loc[0],loc[1],loc[2],rad*(minRecursionRadMp+rand.nextDouble()*(maxRecursionRadMp-minRecursionRadMp)),newVec,recursionLevel+1)){
					--tmpCavesLeft;
				}
			}
		}
		
		return generatedSomething;
	}
}

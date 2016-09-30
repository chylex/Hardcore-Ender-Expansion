package chylex.hee.world.feature.ores;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.GenerateFeature;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public final class GenerateOres extends GenerateFeature{
	final Block toReplace;
	final IBlockPicker orePicker;
	
	private IRangeGenerator oresPerCluster;
	private IOreGenerator oreGenerator;
	
	public GenerateOres(Block toReplace, IBlockPicker orePicker){
		this.toReplace = toReplace;
		this.orePicker = orePicker;
	}
	
	public GenerateOres(Block toReplace, Block oreBlock){
		this.toReplace = toReplace;
		this.orePicker = new BlockInfo(oreBlock);
	}
	
	public GenerateOres(Block toReplace, Block oreBlock, int oreMeta){
		this.toReplace = toReplace;
		this.orePicker = new BlockInfo(oreBlock, oreMeta);
	}
	
	public void setOresPerCluster(int min, int max){
		this.oresPerCluster = new RangeGenerator(min, max, RandomAmount.linear);
	}
	
	public void setOresPerCluster(int min, int max, RandomAmount distribution){
		this.oresPerCluster = new RangeGenerator(min, max, distribution);
	}
	
	public void setOreGenerator(IOreGenerator generator){
		this.oreGenerator = generator;
	}
	
	@Override
	public void generateSplit(StructureWorld world, Random rand){
		generateSplitInternal(world, rand, 1);
	}
	
	@Override
	public void generateFull(StructureWorld world, Random rand){
		generateFullInternal(world, rand, 4);
	}
	
	@Override
	protected boolean tryGenerateFeature(StructureWorld world, Random rand, int x, int y, int z){
		if (world.getBlock(x, y, z) == toReplace){
			oreGenerator.generate(this, world, rand, x, y, z, oresPerCluster.next(rand));
			return true;
		}
		else return false;
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			int amtX = DragonUtil.tryParse(args.length > 0 ? args[0] : "", 1);
			int amtZ = DragonUtil.tryParse(args.length > 1 ? args[1] : "", 1);
			
			GenerateOres gen = new GenerateOres(Blocks.air, BlockList.end_powder_ore);
			gen.setChunkSize(12);
			gen.setY(16, 16);
			gen.setAttemptsPerChunk(1);
			gen.setGeneratedPerChunk(1, 1);
			
			gen.setOresPerCluster(4, 4, RandomAmount.preferSmaller);
			gen.setOreGenerator(new IOreGenerator.AttachingLines(new RangeGenerator(4, 4, RandomAmount.aroundCenter), false));
			
			StructureWorld structureWorld = new StructureWorld(world, 12+6*amtX, 33, 12+6*amtZ);
			gen.generateSplit(structureWorld, world.rand);
			structureWorld.generateInWorld(world, world.rand, MathUtil.floor(player.posX), MathUtil.floor(player.posY)-24, MathUtil.floor(player.posZ));
		}
	};
}

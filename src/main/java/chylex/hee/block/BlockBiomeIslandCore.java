package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.block.state.BlockAbstractStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;

public class BlockBiomeIslandCore extends BlockAbstractStateEnum{
	public static enum Biome{ INFESTED_FOREST_DEEP, BURNING_MOUNTAINS_SCORCHING, ENCHANTED_ISLAND_HOMELAND, INFESTED_FOREST_RAVAGED, INFESTED_FOREST_RUINS, BURNING_MOUNTAINS_MINE, ENCHANTED_ISLAND_LABORATORY }
	public static final PropertyEnumSimple BIOME = PropertyEnumSimple.create("biome",Biome.class);
	
	public BlockBiomeIslandCore(){
		super(Material.rock);
		disableStats();
		createSimpleMeta(BIOME,Biome.class);
	}

	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ BIOME };
	}
	
	@Override
	public int tickRate(World world){
		return 4;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state){
		super.onBlockAdded(world,pos,state);
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		world.scheduleUpdate(pos,this,tickRate(world));
		
		int meta = getMetaFromState(state);
		
		for(IslandBiomeBase biome:IslandBiomeBase.biomeList){
			if (biome.isValidMetadata(meta)){
				biome.updateCore(world,new BlockPosM(pos),meta);
				break;
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos){
		return null;
	}
}

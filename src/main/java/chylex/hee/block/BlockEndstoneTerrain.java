package chylex.hee.block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;

public class BlockEndstoneTerrain extends BlockAbstract implements IBlockSubtypes{
	public static enum Variant{ INFESTED, BURNED, ENCHANTED }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public BlockEndstoneTerrain(){
		super(Material.rock);
		createSimpleMeta(VARIANT,Variant.class);
	}
	
	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	@Override
	public boolean canCreatureSpawn(IBlockAccess world, BlockPos pos, SpawnPlacementType type){
		switch((Variant)world.getBlockState(pos).getValue(VARIANT)){
			case INFESTED: return BlockList.blockRandom.nextInt(10) <= 2;
			case BURNED: return false;
			case ENCHANTED:
			default: return true;
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		switch((Variant)getEnumFromDamage(is.getItemDamage())){
			case INFESTED: return "tile.endStoneTerrain.infested";
			case BURNED: return "tile.endStoneTerrain.burned";
			case ENCHANTED: return "tile.endStoneTerrain.enchanted";
			default: return "";
		}
	}
}

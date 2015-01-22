package chylex.hee.block;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;

public class BlockCrossedDecoration extends BlockBush implements IShearable, IBlockSubtypes{
	public enum Variant{ UNUSED_1, UNUSED_2, THORN_BUSH, INFESTED_GRASS, INFESTED_FERN, INFESTED_TALL_GRASS, LILYFIRE, VIOLET_MOSS_TALL, VIOLET_MOSS_MODERATE, VIOLET_MOSS_SHORT }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public static IBlockState createState(Variant variant){
		return BlockList.crossed_decoration.getDefaultState().withProperty(VARIANT,variant);
	}
	
	public BlockCrossedDecoration(){
		setBlockBounds(0.1F,0F,0.1F,0.9F,0.8F,0.9F);
	}
	
	@Override
	public final IBlockState getStateFromMeta(int meta){
		return meta >= 0 && meta < Variant.values().length ? getDefaultState().withProperty(VARIANT,Variant.values()[meta]) : getDefaultState();
	}
	
	@Override
	public final int getMetaFromState(IBlockState state){
		return ((Enum)state.getValue(VARIANT)).ordinal();
	}
	
	public final Enum getEnumFromDamage(int damage){
		return damage >= 0 && damage < Variant.values().length ? Variant.values()[damage] : null;
	}
	
	@Override
	protected final BlockState createBlockState(){
		return new BlockState(this,new IProperty[]{ VARIANT });
	}
	
	@Override
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state){
		IBlockState soil = world.getBlockState(pos.down());
		return (world.getLight(pos) >= 8 || world.canBlockSeeSky(pos) || world.provider.getDimensionId() == 1) && soil.getBlock().canSustainPlant(world,pos.down(),EnumFacing.UP,this);
	}
	
	@Override
	protected boolean canPlaceBlockOn(Block block){
		return block == Blocks.end_stone || block == BlockList.end_terrain || super.canPlaceBlockOn(block);
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> ret = new ArrayList<>();
		if (state.getValue(VARIANT) == Variant.LILYFIRE)ret.add(new ItemStack(this,1,getMetaFromState(state)));
		return ret;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos){
		if (world.getBlockState(pos).getValue(VARIANT) == Variant.LILYFIRE)return AxisAlignedBB.fromBounds(pos.getX()+0.3F,pos.getY(),pos.getZ()+0.3F,pos.getX()+0.7F,pos.getY()+0.8F,pos.getZ()+0.7F);
		else return super.getSelectedBoundingBox(world,pos);
	}
	
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){
		return world.getBlockState(pos).getValue(VARIANT) != Variant.LILYFIRE;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune){
		ArrayList<ItemStack> ret = new ArrayList<>();
		ret.add(new ItemStack(this,1,getMetaFromState(world.getBlockState(pos))));
		return ret;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
		if (state.getValue(VARIANT) == Variant.THORN_BUSH){
			entity.attackEntityFrom(DamageSource.generic,1F);
			
			if (world.rand.nextInt(80) == 0 && entity instanceof EntityLivingBase && !((EntityLivingBase)entity).isPotionActive(Potion.poison)){
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.poison.id,30+world.rand.nextInt(40),1,false,false));
			}
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch((Variant)getEnumFromDamage(is.getItemDamage())){
			case THORN_BUSH: return "tile.crossedDecoration.thornyBush";
			case INFESTED_FERN: return "tile.crossedDecoration.infestedFern";
			case INFESTED_GRASS: return "tile.crossedDecoration.infestedBush";
			case INFESTED_TALL_GRASS: return "tile.crossedDecoration.infestedGrass";
			case LILYFIRE: return "tile.crossedDecoration.lilyfire";
			case VIOLET_MOSS_TALL: return "tile.crossedDecoration.violetMoss.tall";
			case VIOLET_MOSS_MODERATE: return "tile.crossedDecoration.violetMoss.moderate";
			case VIOLET_MOSS_SHORT: return "tile.crossedDecoration.violetMoss.short";
			default: return "";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(int a = 2; a < Variant.values().length; a++){
			list.add(new ItemStack(item,1,a));
		}
	}
}

package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;

public class BlockObsidianSpecial extends BlockAbstractStateEnum implements IBlockSubtypes{	
	/*
	 * Metadata
	 *   0: smooth
	 *   1: chiseled
	 *   2: pillar - vertical
	 *   3: pillar - NS
	 *   4: pillar - EW
	 *   5: (smooth) downward particle spawner - 4 blocks
	 *   6: (chiseled) upward particle spawner - 5 blocks
	 */
	
	public enum Variant{ SMOOTH, CHISELED, PILLAR_VERTICAL, PILLAR_NS, PILLAR_EW, SMOOTH_PARTICLES, CHISELED_PARTICLES }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	private static final Variant getDroppable(Variant variant){
		switch(variant){
			case SMOOTH_PARTICLES: return Variant.SMOOTH;
			case CHISELED_PARTICLES: return Variant.CHISELED;
			case PILLAR_EW:
			case PILLAR_NS: return Variant.PILLAR_VERTICAL;
			default: return variant;
		}
	}
	
	private final boolean isGlowing;
	
	public BlockObsidianSpecial(boolean isGlowing){
		super(Material.rock);
		this.isGlowing = isGlowing;
		createSimpleMeta(VARIANT,Variant.class);
	}
	
	@Override
	protected IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}
	
	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		if (meta == Variant.PILLAR_VERTICAL.ordinal()){
			switch(side.getAxis().ordinal()){
				case 1: return setProperty(Variant.PILLAR_NS);
				case 2: return setProperty(Variant.PILLAR_EW);
				default: return setProperty(Variant.PILLAR_VERTICAL);
			}
		}
		else return super.onBlockPlaced(world,pos,side,hitX,hitY,hitZ,meta,placer);
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return getDroppable((Variant)state.getValue(VARIANT)).ordinal();
	}
	
	@Override
	protected ItemStack createStackedBlock(IBlockState state){
		return new ItemStack(this,1,getDroppable((Variant)state.getValue(VARIANT)).ordinal());
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 1: return isGlowing ? "tile.obsidianSpecialGlowing.chiseled" : "tile.obsidianSpecial.chiseled";
			case 2: return isGlowing ? "tile.obsidianSpecialGlowing.pillar" : "tile.obsidianSpecial.pillar";
			default: return isGlowing ? "tile.obsidianSpecialGlowing.smooth" : "tile.obsidianSpecial.smooth";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		Variant variant = (Variant)state.getValue(VARIANT);
		
		if (variant == Variant.SMOOTH_PARTICLES){
			for(int a = 0; a < 10; a++){
				world.spawnParticle(EnumParticleTypes.PORTAL,pos.getX()+rand.nextFloat(),pos.getY()-4F*rand.nextFloat(),pos.getZ()+rand.nextFloat(),0D,0D,0D);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,pos.getX()+rand.nextFloat(),pos.getY()-4F*rand.nextFloat(),pos.getZ()+rand.nextFloat(),0D,0D,0D);
			}
		}
		else if (variant == Variant.CHISELED_PARTICLES){
			for(int a = 0; a < 30; a++){
				world.spawnParticle(EnumParticleTypes.PORTAL,pos.getX()+rand.nextFloat(),pos.getY()+5F*rand.nextFloat(),pos.getZ()+rand.nextFloat(),0D,0D,0D);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,Variant.SMOOTH.ordinal()));
		list.add(new ItemStack(item,1,Variant.CHISELED.ordinal()));
		list.add(new ItemStack(item,1,Variant.PILLAR_VERTICAL.ordinal()));
	}
}

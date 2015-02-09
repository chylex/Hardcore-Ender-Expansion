package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.state.BlockAbstractStateEnum;
import chylex.hee.block.state.PropertyEnumSimple;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.system.util.MathUtil;

public class BlockRavagedBrick extends BlockAbstractStateEnum implements IBlockSubtypes{
	public enum Variant{ NORMAL, CRACKED, DAMAGED_1, DAMAGED_2, DAMAGED_3, DAMAGED_4 }
	public static final PropertyEnumSimple VARIANT = PropertyEnumSimple.create("variant",Variant.class);
	
	public static final Variant getRandomDamagedBrick(Random rand){
		switch(rand.nextInt(4)){
			case 0: return Variant.DAMAGED_1;
			case 1: return Variant.DAMAGED_2;
			case 2: return Variant.DAMAGED_3;
			default: return Variant.DAMAGED_4;
		}
	}
	
	public BlockRavagedBrick(){
		super(Material.rock);
		createSimpleMeta(VARIANT,Variant.class);
	}
	
	@Override
	public IProperty[] getPropertyArray(){
		return new IProperty[]{ VARIANT };
	}

	@Override
	public String getUnlocalizedName(ItemStack is){
		return getUnlocalizedName();
	}
	
	@Override
	public float getBlockHardness(World world, BlockPos pos){
		if (world.provider.getDimensionId() != 1 || !world.isRemote)return blockHardness; // only run on client side
		
		List<TileEntity> list = world.loadedTileEntityList;
		int spawnerCount = 0;
		
		for(TileEntity tile:list){
			if (tile.getBlockType() == BlockList.custom_spawner && MathUtil.distance(pos.getX()-tile.getPos().getX(),pos.getZ()-tile.getPos().getZ()) < 260)++spawnerCount;
		}
		
		if (spawnerCount > 24)world.spawnParticle(EnumParticleTypes.REDSTONE,pos.getX()-0.2D+world.rand.nextDouble()*1.4D,pos.getY()-0.2D+world.rand.nextDouble()*1.4D,pos.getZ()-0.2D+world.rand.nextDouble()*1.4D,1D,0.2D,0.2D);
		
		return spawnerCount <= 24 ? blockHardness : blockHardness+(3F+(float)Math.pow(spawnerCount-24,0.8D)*1.5F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item));
	}
}

package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.block.state.BlockAbstractContainerStateInt;

public class BlockDeathFlowerPot extends BlockAbstractContainerStateInt{ // TODO BlockFlowerPot
	public BlockDeathFlowerPot(){
		super(Material.circuits);
		setTickRandomly(true);
		createSimpleMeta(BlockDeathFlower.DECAY,0,15);
	}
	
	@Override
	public IProperty[] getPropertyArray(){
		return new IProperty[]{ BlockDeathFlower.DECAY };
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityFlowerPot(Item.getItemFromBlock(BlockList.death_flower),0);
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return getMetaFromState(state);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		((BlockDeathFlower)BlockList.death_flower).updateFlowerLogic(world,pos,rand);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		return BlockList.death_flower.onBlockActivated(world,pos,state,player,side,hitX,hitY,hitZ);
	}
	
	@Override
	public int getDamageValue(World world, BlockPos pos){
		return getMetaFromState(world.getBlockState(pos));
	}
	
	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,pos,state,chance,fortune);
		spawnAsEntity(world,pos,new ItemStack(BlockList.death_flower,1,getMetaFromState(state)));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		BlockList.death_flower.randomDisplayTick(world,pos,state,rand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos){
		return Item.getItemFromBlock(BlockList.death_flower);
	}

	public static ItemStack getPlantForMeta(int meta){
		return new ItemStack(BlockList.death_flower,1,meta);
	}

	public static int getMetaForPlant(ItemStack is){
		return is.getItemDamage();
	}
}

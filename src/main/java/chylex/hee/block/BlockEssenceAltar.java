package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEssenceAltar extends BlockContainer{
	@SideOnly(Side.CLIENT)
	private IIcon[] iconTop, iconSide, iconBottom;
	
	public BlockEssenceAltar(){
		super(Material.iron);
		setBlockBounds(0.0F,0.0F,0.0F,1.0F,0.75F,1.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEssenceAltar();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote)return true;
		
		ItemStack held = player.getHeldItem();
		if (held != null && held.getItem() == ItemList.end_powder)return false;
		
		TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(x,y,z);
		if (altar != null)altar.onRightClick(player);
		
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		if (!world.isRemote){
			Pos.at(x,y,z).castTileEntity(world,TileEntityEssenceAltar.class).ifPresent(tile -> {
				tile.onBlockDestroy();
				dropBlockAsItem(world,x,y,z,IEnhanceableTile.createItemStack(tile));
			});
		}
		
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player){
		return new ItemStack(BlockList.essence_altar,1,world.getBlockMetadata(x,y,z));
	}
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		for(EssenceType essenceType:EssenceType.values())list.add(new ItemStack(item,1,essenceType.id));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (meta >= iconBottom.length)meta = 0;
		return side == 0 ? iconBottom[meta] : (side == 1 ? iconTop[meta] : iconSide[meta]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		String s = getTextureName()+"_";
		
		iconTop = new IIcon[EssenceType.values().length];
		iconSide = new IIcon[iconTop.length];
		iconBottom = new IIcon[iconTop.length];
		
		for(int a = 0; a < iconTop.length; a++){
			iconTop[a] = iconRegister.registerIcon(s+a+"_top");
			iconSide[a] = iconRegister.registerIcon(s+a+"_side");
			iconBottom[a] = iconRegister.registerIcon(s+a+"_bottom");
		}
	}
}

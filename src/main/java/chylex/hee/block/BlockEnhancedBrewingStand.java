package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnhancedBrewingStand extends BlockBrewingStand{
	@SideOnly(Side.CLIENT)
	private IIcon theIcon;
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnhancedBrewingStand();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is){
		super.onBlockPlacedBy(world,x,y,z,entity,is);
		
		IEnhanceableTile tile = (IEnhanceableTile)world.getTileEntity(x,y,z);
		if (tile != null)tile.getEnhancements().addAll(EnhancementHandler.getEnhancements(is));
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		ItemStack held = player.getHeldItem();
		if (held != null && held.getItem() == ItemList.end_powder)return false;
		
		player.openGui(HardcoreEnderExpansion.instance,0,world,x,y,z);
		return true;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z){
		return ItemList.enhanced_brewing_stand;
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
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		TileEntity tile = world.getTileEntity(x,y,z);
		if (tile instanceof IEnhanceableTile)dropBlockAsItem(world,x,y,z,((IEnhanceableTile)tile).createEnhancedItemStack());
		
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		super.registerBlockIcons(iconRegister);
		theIcon = iconRegister.registerIcon(getTextureName()+"_base");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconBrewingStandBase(){
		return theIcon;
	}
}

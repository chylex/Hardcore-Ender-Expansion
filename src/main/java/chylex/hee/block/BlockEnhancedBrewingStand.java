package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnhancedBrewingStand extends BlockBrewingStand{
	@SideOnly(Side.CLIENT)
    private IIcon theIcon;
	
	public BlockEnhancedBrewingStand(){
		super();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEnhancedBrewingStand();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		player.openGui(HardcoreEnderExpansion.instance,0,world,x,y,z);
		return true;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.enhanced_brewing_stand;
	}
	
	@Override
	public Item getItem(World world, int x, int y, int z){
        return ItemList.enhanced_brewing_stand;
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

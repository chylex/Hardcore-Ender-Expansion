package chylex.hee.block;
import java.util.List;
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
import net.minecraft.world.World;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEssenceAltar extends BlockContainer{
	private static final float hitCenter1 = 0.09F, hitCenter2 = 0.9F, hitDist = 0.05F;
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconTop,iconSide,iconBottom;
	
	public BlockEssenceAltar(){
		super(Material.iron);
		setBlockBounds(0.0F,0.0F,0.0F,1.0F,0.75F,1.0F);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote)return true;
		
		TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(x,y,z);
		if (altar != null){
			if (side == 1){
				if (hitX >= hitCenter1-hitDist && hitX <= hitCenter1+hitDist && hitZ >= hitCenter1-hitDist && hitZ <= hitCenter1+hitDist)altar.onSocketClick(player,3);
				else if (hitX >= hitCenter1-hitDist && hitX <= hitCenter1+hitDist && hitZ >= hitCenter2-hitDist && hitZ <= hitCenter2+hitDist)altar.onSocketClick(player,2);
				else if (hitX >= hitCenter2-hitDist && hitX <= hitCenter2+hitDist && hitZ >= hitCenter2-hitDist && hitZ <= hitCenter2+hitDist)altar.onSocketClick(player,1);
				else if (hitX >= hitCenter2-hitDist && hitX <= hitCenter2+hitDist && hitZ >= hitCenter1-hitDist && hitZ <= hitCenter1+hitDist)altar.onSocketClick(player,0);
				else altar.onRightClick(player);
				return true;
			}
			else altar.onRightClick(player);
		}
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta){
		if (world.isRemote)return;
		
		TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(x,y,z);
		if (altar != null)altar.onBlockDestroy();
		super.breakBlock(world,x,y,z,oldBlock,oldMeta);
	}
	

	@Override
	public int damageDropped(int damage){
		return damage;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEssenceAltar();
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
		for(EssenceType essenceType:EssenceType.values()){
			list.add(new ItemStack(item,1,essenceType.id));
		}
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

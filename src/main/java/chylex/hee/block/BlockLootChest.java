package chylex.hee.block;
import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.item.block.ItemBlockWithTooltip.IHaveTooltip;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityLootChest;

public class BlockLootChest extends BlockContainer implements IHaveTooltip{
	public BlockLootChest(){
		super(Material.rock);
		setBlockBounds(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityLootChest();
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
	public int getRenderType(){
		return ModCommonProxy.renderIdLootChest;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is){
		byte meta = 0;
		
		switch(MathUtil.floor(entity.rotationYaw/90F+0.5D)&3){
			case 0: meta = 2; break;
			case 1: meta = 5; break;
			case 2: meta = 3; break;
			case 3: meta = 4; break;
		}
		
		Pos.at(x, y, z).setMetadata(world, meta, 2);
		
		if (is.hasDisplayName())((TileEntityLootChest)Pos.at(x, y, z).getTileEntity(world)).setCustomInventoryName(is.getDisplayName());
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote && !Pos.at(x, y+1, z).getBlock(world).isNormalCube() && Pos.at(x, y, z).getTileEntity(world) instanceof TileEntityLootChest){
			player.openGui(HardcoreEnderExpansion.instance, 6, world, x, y, z);
		}
		
		return true;
	}
	
	@Override
	public void addTooltip(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		textLines.add(EnumChatFormatting.GRAY+"Editable in creative mode");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister){
		blockIcon = Blocks.obsidian.getBlockTextureFromSide(0);
	}
}

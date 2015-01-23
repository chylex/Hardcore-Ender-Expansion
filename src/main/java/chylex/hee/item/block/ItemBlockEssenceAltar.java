package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import chylex.hee.block.BlockEssenceAltar;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.tileentity.TileEntityEssenceAltar;

public class ItemBlockEssenceAltar extends ItemBlock{
	public ItemBlockEssenceAltar(Block block){
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName("essenceAltar");
	}

	@Override
	public int getMetadata(int damage){
		return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		EssenceType essenceType = EssenceType.getById(is.getItemDamage());
		if (ModCommonProxy.hardcoreEnderbacon && essenceType == EssenceType.DRAGON)return "tile.essenceAltar.dragon.bacon";
		return "tile.essenceAltar."+(essenceType == null ? EssenceType.INVALID : essenceType).essenceNameLowercase;
	}
	
	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state){
		if (super.placeBlockAt(is,player,world,pos,side,hitX,hitY,hitZ,state)){
			TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(pos);
			if (altar != null)altar.loadFromType((EssenceType)state.getValue(BlockEssenceAltar.VARIANT));
			return true;
		}
		return false;
	}
}

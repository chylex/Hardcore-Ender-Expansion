package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.mechanics.essence.EssenceType;
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
		return "tile.essenceAltar."+(essenceType == null?EssenceType.INVALID:essenceType).essenceNameLowercase;
    }
	
	@Override
	public boolean placeBlockAt(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata){
		if (super.placeBlockAt(is,player,world,x,y,z,side,hitX,hitY,hitZ,metadata)){
			TileEntityEssenceAltar altar = (TileEntityEssenceAltar)world.getTileEntity(x,y,z);
			if (altar != null)altar.loadFromDamage(metadata);
			return true;
		}
		return false;
	}
}

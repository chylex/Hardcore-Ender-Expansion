package chylex.hee.item;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.proxy.ModCommonProxy;

public class ItemEssence extends Item{
	public ItemEssence(){
		setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(EssenceType essenceType:EssenceType.values()){
			if (essenceType == EssenceType.INVALID)continue;
			list.add(new ItemStack(item,1,essenceType.id-1));
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		EssenceType essenceType = EssenceType.getById(is.getItemDamage()+1);
		if (ModCommonProxy.hardcoreEnderbacon && essenceType == EssenceType.DRAGON)return "item.essence.dragon.bacon";
		return "item.essence."+(essenceType == null ? "invalid" : essenceType.essenceNameLowercase);
	}
	
	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player){
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public EnumRarity getRarity(ItemStack is){
		return EnumRarity.uncommon;
	}
}
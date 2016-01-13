package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEssence extends Item{
	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damage){
		return CollectionUtil.getClamp(iconArray,damage);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister){
		iconArray = new IIcon[EssenceType.values().length-1];
		int index = -1;
		
		for(EssenceType essenceType:EssenceType.values()){
			if (essenceType == EssenceType.INVALID)continue;
			iconArray[++index] = iconRegister.registerIcon("hardcoreenderexpansion:essence_"+essenceType.id);
		}
	}
}
package chylex.hee.item;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.global.WorldFile;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.BitStream;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.world.end.EndTerritory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPortalToken extends Item{
	public static final ItemStack forTerritory(EndTerritory territory, boolean isRare){
		ItemStack is = new ItemStack(ItemList.portal_token,1,isRare ? 1 : 0);
		ItemUtil.getTagRoot(is,true).setByte("territory",(byte)territory.ordinal());
		return is;
	}
	
	public static final ItemStack forTerritory(EndTerritory territory, boolean isRare, Random rand){
		ItemStack is = forTerritory(territory,isRare);
		ItemUtil.getTagRoot(is,true).setInteger("variations",territory.properties.generateVariationsSerialized(rand,isRare));
		return is;
	}
	
	public static final @Nullable EndTerritory getTerritory(ItemStack is){
		return CollectionUtil.get(EndTerritory.values,ItemUtil.getTagRoot(is,false).getByte("territory")).orElse(null);
	}
	
	public static final boolean isRare(ItemStack is){
		return is.getItemDamage() == 1;
	}
	
	public static final EnumSet<? extends Enum<?>> getVariations(ItemStack is){
		EndTerritory territory = getTerritory(is);
		if (territory == null)return EmptyEnumSet.get();
		
		return territory.properties.deserialize(ItemUtil.getTagRoot(is,false).getInteger("variations"));
	}
	
	public static final Optional<Pos> generateTerritory(ItemStack is, World world){
		NBTTagCompound nbt = ItemUtil.getTagRoot(is,true);
		if (nbt.hasKey("tpos"))return Optional.of(Pos.at(nbt.getLong("tpos")));
		
		EndTerritory territory = getTerritory(is);
		if (territory == null)return Optional.empty();
		
		WorldFile file = SaveData.global(WorldFile.class);
		
		final int index = file.increment(territory);
		final EnumSet<? extends Enum<?>> variations = getVariations(is);
		
		file.setTerritoryVariations(territory,index,variations);
		if (isRare(is))file.setTerritoryRare(territory,index);
		
		Pos spawnPos = territory.generateTerritory(index,world,territory.createRandom(world.getSeed(),index),variations);
		nbt.setLong("tpos",spawnPos.toLong());
		return Optional.of(spawnPos);
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon iconInscription, iconRareOverlay;
	
	public ItemPortalToken(){
		setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return isRare(is) ? getUnlocalizedName()+".rare" : getUnlocalizedName();
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote && player.capabilities.isCreativeMode && getTerritory(is) != null){
			ItemUtil.getTagRoot(is,true).setInteger("variations",getTerritory(is).properties.generateVariationsSerialized(world.rand,isRare(is)));
		}
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		final int territory = ItemUtil.getTagRoot(is,false).getByte("territory");
		textLines.add(I18n.format("territory."+territory));
		
		final int variations = ItemUtil.getTagRoot(is,false).getInteger("variations");
		
		if (variations != 0){
			BitStream.forInt(variations).forEach(ordinal -> {
				textLines.add(EnumChatFormatting.YELLOW+I18n.format("territory."+territory+".variation."+ordinal));
			});
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list){
		for(EndTerritory territory:EndTerritory.values){
			if (territory.canGenerate()){
				list.add(forTerritory(territory,false));
				list.add(forTerritory(territory,true));
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack is, int pass){
		return pass == 0 ? itemIcon : pass == 1 ? iconInscription : iconRareOverlay;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int pass){
		if (pass == 0){
			EndTerritory territory = getTerritory(is);
			if (territory != null)return territory.tokenColor;
		}
		
		return 16777215;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderPasses(int damage){
		return damage == 1 ? 3 : 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		itemIcon = iconRegister.registerIcon(iconString+"_back");
		iconInscription = iconRegister.registerIcon(iconString+"_inscription");
		iconRareOverlay = iconRegister.registerIcon(iconString+"_rare");
	}
}

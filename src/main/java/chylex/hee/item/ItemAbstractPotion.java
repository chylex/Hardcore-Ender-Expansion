package chylex.hee.item;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectilePotion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemAbstractPotion extends Item{
	@SideOnly(Side.CLIENT)
	private IIcon iconBottleNormal, iconBottleSplash;
	
	public ItemAbstractPotion(){
		setMaxStackSize(1);
	}
	
	public abstract void applyEffectDrunk(ItemStack is, World world, EntityPlayer player);
	public abstract void applyEffectThrown(Entity entity, double dist);

	@Override
	public final ItemStack onEaten(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote)applyEffectDrunk(is,world,player);

		if (!player.capabilities.isCreativeMode){
			if (--is.stackSize <= 0)return new ItemStack(Items.glass_bottle);
			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}

		return is;
	}
	
	@Override
	public final ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.getItemDamage() == 1){
			if (!player.capabilities.isCreativeMode)--is.stackSize;
			
			world.playSoundAtEntity(player,"random.bow",0.5F,0.4F/(itemRand.nextFloat()*0.4F+0.8F));
			if (!world.isRemote)world.spawnEntityInWorld(new EntityProjectilePotion(world,player,this));
			
			return is;
		}
		
		player.setItemInUse(is,getMaxItemUseDuration(is));
		return is;
	}
	
	@Override
	public final int getMaxItemUseDuration(ItemStack is){
		return 32;
	}

	@Override
	public final EnumAction getItemUseAction(ItemStack is){
		return EnumAction.drink;
	}
	
	@Override
	public final String getItemStackDisplayName(ItemStack is){
		return is.getItemDamage() != 1 ? StatCollector.translateToLocal(getUnlocalizedName()+".name") : StatCollector.translateToLocal(getUnlocalizedName()+".splash.name");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean hasEffect(ItemStack is, int pass){
		return pass == 0 && is.getItemDamage() < 2;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIconFromDamageForRenderPass(int damage, int pass){
		return pass == 0 ? itemIcon : damage == 1 ? iconBottleSplash : iconBottleNormal;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final void getSubItems(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister iconRegister){
		super.registerIcons(iconRegister);
		iconBottleNormal = iconRegister.registerIcon("potion_bottle_drinkable");
		iconBottleSplash = iconRegister.registerIcon("potion_bottle_splash");
	}
}

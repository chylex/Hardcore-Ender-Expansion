package chylex.hee.item.block;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.enhancements.types.SoulCharmEnhancements;
import chylex.hee.tileentity.TileEntitySoulCharm;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSoulCharm extends ItemBlock{
	@SideOnly(Side.CLIENT)
	private IIcon iconSoulCharm,iconEmptySoulCharm;
	
	public ItemBlockSoulCharm(Block block){
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage){
		return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 1: return "tile.emptySoulCharm";
			default: return "tile.soulCharm";
		}
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack is){
		return is.getItemDamage() == 1 ? 120 : super.getMaxItemUseDuration(is);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is){
		return is.getItemDamage() == 1 ? EnumAction.bow : super.getItemUseAction(is);
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		Block block = world.getBlock(x,y,z);
		
		if (is.getItemDamage() == 1){
			if (block == BlockList.soul_charm){
				player.setItemInUse(is,getMaxItemUseDuration(is));
				
				if (is.stackTagCompound == null)is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setIntArray("usedLoc",new int[]{ x,world.getBlock(x,y-1,z) == BlockList.soul_charm?y-1:y,z });
			}
			
			return false;
		}

		if (block == Blocks.snow_layer && (world.getBlockMetadata(x,y,z)&7) < 1)side = 1;
		else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && (!block.isReplaceable(world,x,y,z))){
			switch(side){
				case 0: --y; break;
				case 1: ++y; break;
				case 2: --z; break;
				case 3: ++z; break;
				case 4: --x; break;
				case 5: ++x; break;
			}
		}

		if (is.stackSize == 0 || !player.canPlayerEdit(x,y,z,side,is) || (y == 255 && block.getMaterial().isSolid())||
			!world.canPlaceEntityOnSide(field_150939_a,x,y,z,false,side,player,is) || !world.isAirBlock(x,y+1,z))return false;	

		if (placeBlockAt(is,player,world,x,y,z,side,hitX,hitY,hitZ,block.onBlockPlaced(world,x,y,z,side,hitX,hitY,hitZ,0))){
			world.playSoundEffect(x+0.5F,y+0.5F,z+0.5F,block.stepSound.func_150496_b(),(block.stepSound.getVolume()+1F)/2F,block.stepSound.getPitch()*0.8F); // OBFUSCATED get place sound
			world.setBlock(x,y+1,z,field_150939_a,1,3);

			if (!player.capabilities.isCreativeMode && --is.stackSize <= 0)player.inventory.mainInventory[player.inventory.currentItem] = new ItemStack(BlockList.soul_charm,1,1);
			else{
				ItemStack toAdd = new ItemStack(BlockList.soul_charm,1,1);
				if (!player.inventory.addItemStackToInventory(toAdd))player.entityDropItem(toAdd,0F);
			}
			
			TileEntitySoulCharm tile = (TileEntitySoulCharm)world.getTileEntity(x,y,z);
			if (tile != null && is.stackTagCompound != null)tile.setEnhancementTag(is.stackTagCompound.getCompoundTag("charmEnhancements"));
		}

		return true;
	}
	
	@Override
	public void onUsingTick(ItemStack is, EntityPlayer player, int count){
		if (!player.worldObj.isRemote && player.worldObj.rand.nextInt(Math.max(1,(count-40)>>1)) == 0 && is.stackTagCompound != null){
			int[] loc = is.stackTagCompound.getIntArray("usedLoc");
			HardcoreEnderExpansion.fx.soulCharmMoving(player.worldObj,loc[0]+0.5D,loc[1]+player.worldObj.rand.nextFloat()*2D,loc[2]+0.5D,player.posX,player.posY+0.8D+player.worldObj.rand.nextDouble()*0.4D,player.posZ);
		}
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack is, World world, EntityPlayer player, int count){
		if (is.stackTagCompound != null){
			is.stackTagCompound.removeTag("usedLoc");
			if (is.stackTagCompound.hasNoTags())is.stackTagCompound = null;
		}
	}
	
	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player){
		int[] loc;
		
		if (is.stackTagCompound == null || (loc = is.stackTagCompound.getIntArray("usedLoc")).length < 3)return is;
		
		TileEntitySoulCharm tile = (TileEntitySoulCharm)world.getTileEntity(loc[0],loc[1],loc[2]);
		if (tile == null)return is;
		
		ItemStack charm = new ItemStack(BlockList.soul_charm);
		charm.stackTagCompound = new NBTTagCompound();
		charm.stackTagCompound.setTag("charmEnhancements",tile.getEnhancementTag());
		
		if (!player.capabilities.isCreativeMode && --is.stackSize <= 0)is = charm;
		else{
			is.stackTagCompound.removeTag("usedLoc");
			if (is.stackTagCompound.hasNoTags())is.stackTagCompound = null;
			
			if (!player.inventory.addItemStackToInventory(charm))player.entityDropItem(charm,0F);
		}
		
		if (world.getBlock(loc[0],loc[1],loc[2]) == BlockList.soul_charm)world.setBlockToAir(loc[0],loc[1],loc[2]);
		if (world.getBlock(loc[0],loc[1]+1,loc[2]) == BlockList.soul_charm)world.setBlockToAir(loc[0],loc[1]+1,loc[2]);
		
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage){
		return damage == 1?iconEmptySoulCharm:iconSoulCharm;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List textLines, boolean showAdvancedInfo){
		if (is.getItemDamage() != 0)return;
		
		for(Entry<SoulCharmEnhancements,Byte> entry:SoulCharmEnhancements.getEnhancements(is).entrySet()){
			if (entry.getValue() == 0)continue;
			textLines.add(new StringBuilder().append(EnumChatFormatting.RESET).append(EnumChatFormatting.RED).append(entry.getKey().name).append(" ").append(entry.getValue() == -1?"1-"+entry.getKey().maxLevel:entry.getValue()).toString());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack is){
		return EnumRarity.uncommon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconSoulCharm = iconRegister.registerIcon("hardcoreenderexpansion:soul_charm");
		iconEmptySoulCharm = iconRegister.registerIcon("hardcoreenderexpansion:soul_charm_empty");
	}
}

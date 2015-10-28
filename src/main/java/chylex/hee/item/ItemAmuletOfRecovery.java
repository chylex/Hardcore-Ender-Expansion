package chylex.hee.item;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.RespawnFile;
import chylex.hee.system.util.ItemUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAmuletOfRecovery extends ItemAbstractEnergyAcceptor{
	/**
	 * Calcuates Energy units required to restore the items: 10 + (item count / 3) + (ench level sum / 4) + (stack tag length / 100)
	 */
	public static final int calculateRequiredEnergy(ItemStack[] inventory){
		return 10+MathUtil.ceil(Arrays.stream(inventory).filter(Objects::nonNull).mapToDouble(is -> {
			int enchantmentSum = ((Map<Integer,Integer>)EnchantmentHelper.getEnchantments(is)).values().stream().mapToInt(level -> level).sum();
			int nbtLength = 0;
			
			if (is.hasTagCompound()){
				try{
					nbtLength = CompressedStreamTools.compress(is.getTagCompound()).length;
				}catch(IOException e){}
			}
			
			return 0.333F+enchantmentSum*0.25F+nbtLength*0.01F;
		}).sum());
	}
	
	public static final IInventory getAmuletInventory(@Nullable ItemStack is){
		InventoryBasic amuletInv = new InventoryBasic("",false,45);
		if (is != null)NBTUtil.readInventory(ItemUtil.getTagRoot(is,false).getTagList("amuletItems",NBT.TAG_COMPOUND),amuletInv);
		return amuletInv;
	}
	
	public static final void setAmuletInventory(ItemStack is, IInventory inv){
		ItemUtil.getTagRoot(is,true).setTag("amuletItems",NBTUtil.writeInventory(inv));
	}
	
	private static boolean hasItems(ItemStack is){
		return ItemUtil.getTagRoot(is,false).hasKey("amuletItems");
	}
	
	@Override
	public int getEnergyUsage(ItemStack is){
		return getMaxDamage(is);
	}

	@Override
	public int getEnergyAccepted(ItemStack is){
		return 1;
	}

	@Override
	public int getMaxDamage(ItemStack is){
		return hasItems(is) ? is.getTagCompound().getInteger("amuletRestoreEnergy") : 30;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote && !canAcceptEnergy(is) && hasItems(is))player.openGui(HardcoreEnderExpansion.instance,1,world,0,0,0);
		return is;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return hasItems(is);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDeath(LivingDeathEvent e){
		if (e.entity.worldObj.isRemote || !(e.entity instanceof EntityPlayer) || e.entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))return;
		
		EntityPlayer player = (EntityPlayer)e.entity;
		InventoryPlayer inv = player.inventory;
		
		// find amulet
		int amuletSlot = IntStream.range(0,inv.mainInventory.length).filter(slot -> {
			ItemStack is = inv.mainInventory[slot];
			return is != null && is.getItem() == this && !canAcceptEnergy(is) && !hasItems(is);
		}).findFirst().orElse(-1);
		
		if (amuletSlot == -1)return;
		
		ItemStack amulet = inv.mainInventory[amuletSlot].copy();
		inv.mainInventory[amuletSlot] = null;
		
		// generate inventory
		IInventory amuletInv = getAmuletInventory(null);
		ItemStack is;
		
		for(int slot = 0; slot < Math.min(36,inv.mainInventory.length); slot++){
			if ((is = inv.mainInventory[slot]) != null && is.getItem() != this){
				amuletInv.setInventorySlotContents(slot,is);
				inv.mainInventory[slot] = null;
			}
		}
		
		for(int slot = Math.min(4,inv.armorInventory.length)-1; slot > 0; slot++){
			if ((is = inv.armorInventory[slot]) != null && is.getItem() != this){
				amuletInv.setInventorySlotContents(36+slot,is);
				inv.armorInventory[slot] = null;
			}
		}
		
		// save
		setAmuletInventory(amulet,amuletInv);
		useEnergy(amulet,player);
		SaveData.player(player,RespawnFile.class).setInventoryItem(0,amulet);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerDrops(PlayerDropsEvent e){ // get whatever is left
		if (e.entity.worldObj.isRemote || e.drops.isEmpty())return;
		
		RespawnFile file = SaveData.player(e.entityPlayer,RespawnFile.class);
		
		ItemStack amulet = file.getInventoryItem(0);
		if (amulet == null)return;
		
		// get leftovers
		List<ItemStack> viableItems = e.drops.stream().map(entity -> entity.getEntityItem()).filter(is -> is.getItem() != this).collect(Collectors.toList());
		if (viableItems.isEmpty())return;
		
		// reload and fill the amulet
		IInventory amuletInv = getAmuletInventory(amulet);
		
		for(int slot = 0; slot < amuletInv.getSizeInventory(); slot++){
			if (amuletInv.getStackInSlot(slot) == null){
				amuletInv.setInventorySlotContents(slot,viableItems.remove(0));
				if (viableItems.isEmpty())break;
			}
		}
		
		// save
		setAmuletInventory(amulet,amuletInv);
		file.setInventoryItem(0,amulet);
	}
}

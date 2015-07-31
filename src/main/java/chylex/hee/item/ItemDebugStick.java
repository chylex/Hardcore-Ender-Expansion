package chylex.hee.item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemDebugStick extends Item{
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote)doDebugAction(is,ItemUtil.getTagRoot(is,false),player,null);
		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote)doDebugAction(is,ItemUtil.getTagRoot(is,false),player,Pos.at(x,y,z));
		return true;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack is){
		return "Hardcore Ender Expansion Debug Stick";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return true;
	}
	
	private void doDebugAction(ItemStack is, NBTTagCompound nbt, EntityPlayer player, Pos pos){
		switch(nbt.getString("type")){
			case "build": onDebugBuild(nbt,player,pos); break;
			case "clear": onDebugClear(nbt,player,pos); break;
			default: player.addChatMessage(new ChatComponentText("Invalid debug stick type!"));
		}
	}
	
	/* === BUILD === */
	private void onDebugBuild(NBTTagCompound nbt, EntityPlayer player, Pos pos){
		if (pos == null)nbt.removeTag("pos");
		else if (!nbt.hasKey("pos"))nbt.setLong("pos",pos.toLong());
		else if (player.isSneaking()){
			ItemStack is = player.inventory.mainInventory[7];
			Block block = is == null || !(is.getItem() instanceof ItemBlock) ? Blocks.air : ((ItemBlock)is.getItem()).field_150939_a;
			int meta = block == Blocks.air ? 0 : is.getItemDamage();
			
			Pos.forEachBlock(pos,Pos.at(nbt.getLong("pos")),blockPos -> blockPos.setBlock(player.worldObj,block,meta));
			nbt.removeTag("pos");
		}
		else{
			final List<Pair<Block,Integer>> blocks = new ArrayList<>();
			
			Arrays.stream(player.inventory.mainInventory)
			.skip(9)
			.filter(is -> is != null && is.getItem() instanceof ItemBlock)
			.forEach(is -> {
				Pair<Block,Integer> data = Pair.of(((ItemBlock)is.getItem()).field_150939_a,is.getItemDamage());
				for(int amt = 0; amt < is.stackSize; amt++)blocks.add(data);
			});
			
			final int blockCount = blocks.size();
			if (blockCount == 0)return;
			
			Pos.forEachBlock(pos,Pos.at(nbt.getLong("pos")),blockPos -> {
				Pair<Block,Integer> selectedBlock = blocks.get(player.worldObj.rand.nextInt(blockCount));
				blockPos.setBlock(player.worldObj,selectedBlock.getLeft(),selectedBlock.getRight());
			});
			
			nbt.removeTag("pos");
		}
	}

	/* === CLEAR === */
	private void onDebugClear(NBTTagCompound nbt, EntityPlayer player, Pos pos){
		if (pos == null)nbt.removeTag("pos");
		else if (!nbt.hasKey("pos"))nbt.setLong("pos",pos.toLong());
		else{
			final List<Block> blocks = Arrays.stream(player.inventory.mainInventory)
			.filter(is -> is != null && is.getItem() instanceof ItemBlock)
			.map(is -> ((ItemBlock)is.getItem()).field_150939_a)
			.collect(Collectors.toList());
			
			Pos.forEachBlock(pos,Pos.at(nbt.getLong("pos")),blockPos -> {
				if (blocks.contains(blockPos.getBlock(player.worldObj)))blockPos.setAir(player.worldObj);
				else if (player.isSneaking() && blockPos.getMaterial(player.worldObj).isLiquid())blockPos.setAir(player.worldObj);
			});
			
			nbt.removeTag("pos");
		}
	}
}

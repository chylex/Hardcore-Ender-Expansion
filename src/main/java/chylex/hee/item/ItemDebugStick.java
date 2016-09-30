package chylex.hee.item;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.logging.Log;
import com.google.common.collect.Iterables;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDebugStick extends Item{
	public static int counter = 0;
	public static Consumer<Integer> counterFunc;
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!world.isRemote && Log.isDebugEnabled())doDebugAction(is, NBT.item(is, false), player, null);
		return is;
	}
	
	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (!world.isRemote && Log.isDebugEnabled())doDebugAction(is, NBT.item(is, false), player, Pos.at(x, y, z));
		return true;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack is){
		return "HEE Debug Stick ("+NBT.item(is, false).getString("type")+")";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass){
		return true;
	}
	
	private void doDebugAction(ItemStack is, NBTCompound nbt, EntityPlayer player, Pos pos){
		switch(nbt.getString("type")){
			case "build": onDebugBuild(nbt, player, pos); break;
			case "clear": onDebugClear(nbt, player, pos); break;
			case "copy": onDebugCopy(nbt, player, pos); break;
			case "info": onDebugInfo(nbt, player, pos); break;
			case "count": onDebugCount(nbt, player, pos); break;
			default: player.addChatMessage(new ChatComponentText("Invalid debug stick type!"));
		}
	}
	
	/* === BUILD === */
	private void onDebugBuild(NBTCompound nbt, EntityPlayer player, Pos pos){
		if (pos == null)nbt.removeTag("pos");
		else if (!nbt.hasKey("pos"))nbt.setLong("pos", pos.toLong());
		else if (player.isSneaking()){
			ItemStack is = player.inventory.mainInventory[7];
			Block block = is == null || !(is.getItem() instanceof ItemBlock) ? Blocks.air : ((ItemBlock)is.getItem()).field_150939_a;
			int meta = block == Blocks.air ? 0 : is.getItemDamage();
			
			Pos.forEachBlock(pos, Pos.at(nbt.getLong("pos")), blockPos -> blockPos.setBlock(player.worldObj, block, meta));
			nbt.removeTag("pos");
		}
		else{
			final List<Pair<Block, Integer>> blocks = new ArrayList<>();
			
			Arrays.stream(player.inventory.mainInventory)
			.skip(9)
			.filter(is -> is != null && is.getItem() instanceof ItemBlock)
			.forEach(is -> {
				Pair<Block, Integer> data = Pair.of(((ItemBlock)is.getItem()).field_150939_a, is.getItemDamage());
				for(int amt = 0; amt < is.stackSize; amt++)blocks.add(data);
			});
			
			final int blockCount = blocks.size();
			if (blockCount == 0)return;
			
			Pos.forEachBlock(pos, Pos.at(nbt.getLong("pos")), blockPos -> {
				Pair<Block, Integer> selectedBlock = blocks.get(player.worldObj.rand.nextInt(blockCount));
				blockPos.setBlock(player.worldObj, selectedBlock.getLeft(), selectedBlock.getRight());
			});
			
			nbt.removeTag("pos");
		}
	}

	/* === CLEAR === */
	private void onDebugClear(NBTCompound nbt, EntityPlayer player, Pos pos){
		if (pos == null)nbt.removeTag("pos");
		else if (!nbt.hasKey("pos"))nbt.setLong("pos", pos.toLong());
		else{
			final List<Block> blocks = Arrays.stream(player.inventory.mainInventory)
			.filter(is -> is != null && is.getItem() instanceof ItemBlock)
			.map(is -> ((ItemBlock)is.getItem()).field_150939_a)
			.collect(Collectors.toList());
			
			Pos.forEachBlock(pos, Pos.at(nbt.getLong("pos")), blockPos -> {
				if (blocks.contains(blockPos.getBlock(player.worldObj)))blockPos.setAir(player.worldObj);
				else if (player.isSneaking() && blockPos.getMaterial(player.worldObj).isLiquid())blockPos.setAir(player.worldObj);
			});
			
			nbt.removeTag("pos");
		}
	}
	
	/* === COPY === */
	private void onDebugCopy(NBTCompound nbt, EntityPlayer player, Pos pos){
		if (pos == null){
			nbt.removeTag("pos1");
			nbt.removeTag("pos2");
			player.addChatMessage(new ChatComponentText("Positions reset."));
		}
		else if (!nbt.hasKey("pos1")){
			nbt.setLong("pos1", pos.toLong());
			player.addChatMessage(new ChatComponentText("Pos 1 set."));
		}
		else if (!nbt.hasKey("pos2")){
			nbt.setLong("pos2", pos.toLong());
			player.addChatMessage(new ChatComponentText("Pos 2 set."));
		}
		else{
			Pos pos1 = Pos.at(nbt.getLong("pos1")), pos2 = Pos.at(nbt.getLong("pos2"));
			List<Pair<Pos, BlockInfo>> solidBlocks = new ArrayList<>(), transparentBlocks = new ArrayList<>();
			
			Pos.forEachBlock(pos1, pos2, blockPos -> {
				BlockInfo info = blockPos.getInfo(player.worldObj);
				
				if (info.block.isNormalCube())solidBlocks.add(Pair.of(blockPos.immutable(), info));
				else transparentBlocks.add(Pair.of(blockPos.immutable(), info));
			});
			
			for(Pair<Pos, BlockInfo> data:Iterables.concat(solidBlocks, transparentBlocks)){
				pos.offset(data.getLeft().getX()-pos1.getX(), data.getLeft().getY()-pos1.getY(), data.getLeft().getZ()-pos1.getZ()).setBlock(player.worldObj, data.getRight());
			}
			
			player.addChatMessage(new ChatComponentText("Copy done."));
		}
	}

	/* === INFO === */
	private void onDebugInfo(NBTCompound nbt, EntityPlayer player, Pos pos){
		if (pos != null){
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD+"Block Type: "+EnumChatFormatting.RESET+GameData.getBlockRegistry().getNameForObject(pos.getBlock(player.worldObj))));
			player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD+"Metadata: "+EnumChatFormatting.RESET+pos.getMetadata(player.worldObj)));
			
			TileEntity tile = pos.getTileEntity(player.worldObj);
			
			if (tile instanceof TileEntitySkull){
				NBTTagCompound tag = new NBTTagCompound();
				tile.writeToNBT(tag);
				player.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD+"Skull Rotation: "+EnumChatFormatting.RESET+tag.getByte("Rot")));
			}
		}
	}
	
	/* === COUNT === */
	private void onDebugCount(NBTCompound nbt, EntityPlayer player, Pos pos){
		if (player.isSneaking())counter = 0;
		else ++counter;
		
		if (counterFunc != null)counterFunc.accept(counter);
	}
}

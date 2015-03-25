package chylex.hee.system.test.list;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.item.ItemList;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.ItemPattern;

public class ItemHandlingTests{
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemPattern(){
		List<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(Blocks.dirt));
		items.add(new ItemStack(Blocks.dirt,64));
		items.add(new ItemStack(Blocks.dirt,1,2));
		items.add(new ItemStack(Blocks.grass));
		items.add(new ItemStack(ItemList.adventurers_diary));
		items.add(new ItemStack(ItemList.ghost_amulet,1,1));
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("testValue",(byte)1);
		items.get(0).stackTagCompound = nbt;
		
		String error = "Unexpected matching item list size, expected $2, got $1.";
		
		Assert.equal(new ItemPattern().setItemName("","*").retainMatching(items).size(),6,error);
		Assert.equal(new ItemPattern().setItemName("HardcoreEnderExpansion","*").retainMatching(items).size(),2,error);
		Assert.equal(new ItemPattern().setItemName("minecraft","dirt").retainMatching(items).size(),3,error);
		Assert.equal(new ItemPattern().setItemName("minecraft","sand").retainMatching(items).size(),0,error);
		Assert.equal(new ItemPattern().setDamageValues(new int[]{ 1 }).retainMatching(items).size(),1,error);
		Assert.equal(new ItemPattern().setDamageValues(new int[]{ 1, 2 }).retainMatching(items).size(),2,error);
		Assert.equal(new ItemPattern().setNBT(nbt).retainMatching(items).size(),1,error);
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemDamagePair(){
		ItemDamagePair pair1 = new ItemDamagePair(Items.gunpowder,0);
		ItemDamagePair pair2 = new ItemDamagePair(Items.gunpowder,-1);
		
		String error1 = "Failed item damage pair test (expected true).";
		String error2 = "Failed item damage pair test (expected false).";
		
		Assert.state(pair1.check(new ItemStack(Items.gunpowder)),error1);
		Assert.state(!pair1.check(new ItemStack(Items.gunpowder,1,1)),error2);
		Assert.state(pair2.check(new ItemStack(Items.gunpowder,1,1)),error1);
		Assert.state(!pair1.check(new ItemStack(Items.dye)),error2);
		
	}
}

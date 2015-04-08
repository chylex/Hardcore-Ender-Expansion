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
	private final String matchError = "Unexpected matching item list size, expected $2, got $1.";
	
	private List<ItemStack> patternTestList = new ArrayList<>();
	private NBTTagCompound patternTestNbt;
	
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.LOADCOMPLETE)
	public void prepareItemPattern(){
		patternTestList.add(new ItemStack(Blocks.dirt));
		patternTestList.add(new ItemStack(Blocks.dirt,64));
		patternTestList.add(new ItemStack(Blocks.dirt,1,2));
		patternTestList.add(new ItemStack(Blocks.grass));
		patternTestList.add(new ItemStack(ItemList.adventurers_diary));
		patternTestList.add(new ItemStack(ItemList.ghost_amulet,1,1));
		
		patternTestNbt = new NBTTagCompound();
		patternTestNbt.setByte("testValue",(byte)1);
		patternTestList.get(0).setTagCompound(patternTestNbt);
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemPatternName(){
		Assert.equal(new ItemPattern().setItemName("","*").retainMatching(patternTestList).size(),6,matchError);
		Assert.equal(new ItemPattern().setItemName("HardcoreEnderExpansion","*").retainMatching(patternTestList).size(),2,matchError);
		Assert.equal(new ItemPattern().setItemName("minecraft","dirt").retainMatching(patternTestList).size(),3,matchError);
		Assert.equal(new ItemPattern().setItemName("minecraft","sand").retainMatching(patternTestList).size(),0,matchError);
	}

	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemPatternDamage(){
		Assert.equal(new ItemPattern().setDamageValues(new int[]{ 1 }).retainMatching(patternTestList).size(),1,matchError);
		Assert.equal(new ItemPattern().setDamageValues(new int[]{ 1, 2 }).retainMatching(patternTestList).size(),2,matchError);
	}

	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemPatternNBT(){
		Assert.equal(new ItemPattern().setNBT((NBTTagCompound)patternTestNbt.copy()).retainMatching(patternTestList).size(),1,matchError);
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testItemDamagePair(){
		ItemDamagePair pair1 = new ItemDamagePair(Items.gunpowder,0);
		ItemDamagePair pair2 = new ItemDamagePair(Items.gunpowder,-1);
		
		String error1 = "Failed item damage pair test (expected true).";
		String error2 = "Failed item damage pair test (expected false).";
		
		Assert.state(pair1.check(new ItemStack(Items.gunpowder)),"Failed item damage pair test (expected true).");
		Assert.state(!pair1.check(new ItemStack(Items.gunpowder,1,1)),"Failed item damage pair test (expected false).");
		Assert.state(pair2.check(new ItemStack(Items.gunpowder,1,1)),"Failed item damage pair test (expected true).");
		Assert.state(!pair1.check(new ItemStack(Items.dye)),"Failed item damage pair test (expected false).");
		
	}
}

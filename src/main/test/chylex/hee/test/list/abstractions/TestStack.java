package chylex.hee.test.list.abstractions;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Stack;
import chylex.hee.test.Assert;
import chylex.hee.test.UnitTest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class TestStack{ // TODO run the test
	@UnitTest
	public void testEmptyStack(){
		Stack empty = Stack.empty();
		
		Assert.isTrue(empty.isEmpty());
		Assert.isNull(empty.getItem());
		Assert.equal(empty.getDamage(), 0);
		Assert.equal(empty.getAmount(), 0);
		Assert.isFalse(empty.isBlock());
		
		Assert.isNull(empty.is());
		Assert.isFalse(empty.getItemBlock().isPresent());
		Assert.isFalse(empty.getBlock().isPresent());
		Assert.isFalse(empty.hasNBT());
		
		Assert.equal(empty.toString(), "{empty}");
		
		Assert.isTrue(empty.copy().isEmpty());
		Assert.isTrue(empty.equals((ItemStack)null, Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		
		Assert.isTrue(Stack.isEmpty(null));
		Assert.isFalse(Stack.isEmpty(new ItemStack(Items.apple)));
	}
	
	@UnitTest
	public void testBasicConstructionAndBasicGetters(){
		Stack stack;
		
		// blocks
		
		stack = Stack.of(Blocks.bedrock);
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 0);
		Assert.equal(stack.getAmount(), 1);
		Assert.isTrue(stack.isBlock());
		
		stack = Stack.of(Blocks.bedrock, 5);
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 1);
		Assert.isTrue(stack.isBlock());
		
		stack = Stack.of(Blocks.bedrock, 5, 64);
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 64);
		Assert.isTrue(stack.isBlock());
		
		stack = Stack.of(new BlockInfo(Blocks.bedrock, 5));
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 1);
		Assert.isTrue(stack.isBlock());
		
		stack = Stack.of(new BlockInfo(Blocks.bedrock, 5), 64);
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 64);
		Assert.isTrue(stack.isBlock());
		
		stack = Stack.of(new ItemStack(Blocks.bedrock, 64, 5));
		Assert.equal(stack.getItem(), Item.getItemFromBlock(Blocks.bedrock));
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 64);
		Assert.isTrue(stack.isBlock());
		
		// items
		
		stack = Stack.of(Items.apple);
		Assert.equal(stack.getItem(), Items.apple);
		Assert.equal(stack.getDamage(), 0);
		Assert.equal(stack.getAmount(), 1);
		Assert.isFalse(stack.isBlock());
		
		stack = Stack.of(Items.apple, 5);
		Assert.equal(stack.getItem(), Items.apple);
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 1);
		Assert.isFalse(stack.isBlock());
		
		stack = Stack.of(Items.apple, 5, 64);
		Assert.equal(stack.getItem(), Items.apple);
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 64);
		Assert.isFalse(stack.isBlock());
		
		stack = Stack.of(new ItemStack(Items.apple, 64, 5));
		Assert.equal(stack.getItem(), Items.apple);
		Assert.equal(stack.getDamage(), 5);
		Assert.equal(stack.getAmount(), 64);
		Assert.isFalse(stack.isBlock());
	}
	
	@UnitTest
	public void testBasicSetters(){
		// TODO
	}
	
	@UnitTest
	public void testCloning(){
		// TODO
	}
	
	@UnitTest
	public void testEquality(){
		Stack stack = Stack.of(Blocks.bookshelf, 5, 64);
		
		// self test
		
		Assert.isTrue(stack.equals(stack, Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		Assert.isTrue(stack.equals(stack.is(), Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		Assert.isTrue(stack.equals(stack.copy(), Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		
		// basic item test
		
		Assert.isTrue(stack.equals(Blocks.bookshelf, 5));
		Assert.isTrue(stack.equals(Item.getItemFromBlock(Blocks.bookshelf), 5));
		
		Assert.isFalse(stack.equals(Blocks.bookshelf, 0));
		Assert.isFalse(stack.equals(Item.getItemFromBlock(Blocks.bookshelf), 0));
		
		Assert.isFalse(stack.equals(Blocks.emerald_block, 5));
		Assert.isFalse(stack.equals(Item.getItemFromBlock(Blocks.emerald_block), 5));
		
		// basic ItemStack tests
		
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 64), Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 32), Stack.CMP_ID_DAMAGE));
		Assert.isFalse(stack.equals(new ItemStack(Blocks.bookshelf, 5, 32), Stack.CMP_ID_DAMAGE_AMOUNT_NBT));
		
		// TODO nbt
		
		// flag combinations
		
		Assert.isFalse(stack.equals(new ItemStack(Blocks.emerald_block, 5, 64), Stack.CMP_ID));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 64), Stack.CMP_ID));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 0, 64), Stack.CMP_ID));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 0, 32), Stack.CMP_ID));
		
		Assert.isTrue(stack.equals(new ItemStack(Blocks.emerald_block, 5, 64), Stack.CMP_DAMAGE));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 64), Stack.CMP_DAMAGE));
		Assert.isFalse(stack.equals(new ItemStack(Blocks.bookshelf, 0, 64), Stack.CMP_DAMAGE));
		Assert.isFalse(stack.equals(new ItemStack(Blocks.bookshelf, 0, 32), Stack.CMP_DAMAGE));
		
		Assert.isTrue(stack.equals(new ItemStack(Blocks.emerald_block, 5, 64), Stack.CMP_AMOUNT));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 64), Stack.CMP_AMOUNT));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 0, 64), Stack.CMP_AMOUNT));
		Assert.isFalse(stack.equals(new ItemStack(Blocks.bookshelf, 0, 32), Stack.CMP_AMOUNT));
		
		Assert.isTrue(stack.equals(new ItemStack(Blocks.emerald_block, 5, 64), Stack.CMP_NBT));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 5, 64), Stack.CMP_NBT));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 0, 64), Stack.CMP_NBT));
		Assert.isTrue(stack.equals(new ItemStack(Blocks.bookshelf, 0, 32), Stack.CMP_NBT));
	}
	
	@UnitTest
	public void testSpecialGetters(){
		Stack stack;
		
		// blocks
		
		stack = Stack.of(Blocks.bookshelf);
		Assert.isTrue(stack.isBlock());
		Assert.isTrue(stack.getItemBlock().isPresent());
		Assert.isTrue(stack.getBlock().isPresent());
		Assert.equal(stack.getItemBlock().get(), Item.getItemFromBlock(Blocks.bookshelf));
		Assert.equal(stack.getBlock().get(), Blocks.bookshelf);
		
		stack = Stack.of(Items.coal);
		Assert.isFalse(stack.isBlock());
		Assert.isFalse(stack.getItemBlock().isPresent());
		Assert.isFalse(stack.getBlock().isPresent());
		
		// ItemStack conversion
		
		stack = Stack.of(Blocks.bookshelf, 5, 64);
		Assert.equal(stack.is().getItem(), Item.getItemFromBlock(Blocks.bookshelf));
		Assert.equal(stack.is().getItemDamage(), 5);
		Assert.equal(stack.is().stackSize, 64);
		
		stack = Stack.of(Items.coal, 5, 64);
		Assert.equal(stack.is().getItem(), Items.coal);
		Assert.equal(stack.is().getItemDamage(), 5);
		Assert.equal(stack.is().stackSize, 64);
	}
	
	@UnitTest
	public void testNBT(){
		// TODO
	}
}

package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemKnowledgeFragment;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemMusicDisk;
import chylex.hee.mechanics.knowledge.util.FragmentWeightLists;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.loot.IItemPostProcessor;
import chylex.hee.world.loot.ItemUtil;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;

public class StructureSilverfishDungeon extends AbstractIslandStructure implements ITileEntityGenerator{
	public static WeightedLootList lootDungeon = new WeightedLootList(
		new LootItemStack(Items.paper).setAmount(1,11).setWeight(36),
		new LootItemStack(Items.book).setAmount(1,8).setWeight(25),
		new LootItemStack(Items.enchanted_book).setWeight(17),
		new LootItemStack(Items.string).setAmount(1,4).setWeight(15),
		new LootItemStack(Items.leather).setAmount(1,4).setWeight(12),
		new LootItemStack(Items.potato).setAmount(1,3).setWeight(11),
		new LootItemStack(Items.dye).setDamage(15).setWeight(8),
		new LootItemStack(Items.poisonous_potato).setAmount(1,4).setWeight(6),
		new LootItemStack(Items.rotten_flesh).setAmount(1,5).setWeight(6),
		new LootItemStack(Blocks.wool).setAmount(1,3).setDamage(0,15).setWeight(6),
		new LootItemStack(ItemList.knowledge_fragment).setWeight(5),
		new LootItemStack(Items.chicken).setAmount(1,3).setWeight(5),
		new LootItemStack(Items.porkchop).setAmount(1,3).setWeight(4),
		new LootItemStack(Items.leather_helmet).setWeight(4),
		new LootItemStack(Items.leather_chestplate).setWeight(4),
		new LootItemStack(Items.leather_leggings).setWeight(4),
		new LootItemStack(Items.leather_boots).setWeight(4),
		new LootItemStack(Items.beef).setAmount(1,3).setWeight(3),
		new LootItemStack(Items.sugar).setAmount(1,2).setWeight(3),
		new LootItemStack(ItemList.music_disk).setDamage(0,ItemMusicDisk.getRecordCount()-1).setWeight(2)
	).addItemPostProcessor(new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			if (is.getItemUseAction() == EnumAction.eat){
				ItemUtil.addLore(is,EnumChatFormatting.DARK_PURPLE.toString()+EnumChatFormatting.ITALIC+"It smells fishy...");
			}
			else if (is.getItem() == Items.enchanted_book){
				is.func_150996_a(Items.book); // OBFUSCATED set item
				EnchantmentHelper.addRandomEnchantment(rand,is,13+rand.nextInt(7));
			}
			else if (is.isItemEnchantable() && is.getItem() != Items.book){
				if (rand.nextInt(4) != 0)EnchantmentHelper.addRandomEnchantment(rand,is,16+rand.nextInt(5));
				ItemUtil.setArmorColor(is,rand.nextInt(16777216));
			}
			else if (is.getItem() == ItemList.knowledge_fragment){
				ItemKnowledgeFragment.setRandomRegistration(is,FragmentWeightLists.endSilverfishDungeons,rand);
			}
			
			return is;
		}
	});
	
	@Override
	protected boolean generate(Random rand){
		for(int xx = getRandomXZ(rand,8), zz = getRandomXZ(rand,8), yy, attempt = 0; attempt < 300; attempt++){
			yy = 10+rand.nextInt(45);
			
			// Check space
			
			boolean canSpawn = true;
			
			for(int testY = yy-1; testY > yy-5; testY--){
				for(int testX = xx-3; testX <= xx+3; testX++){
					if (world.isAir(testX,testY,zz)){
						canSpawn = false;
						break;
					}
				}
				
				for(int testZ = zz-3; testZ <= zz+3; testZ++){
					if (world.isAir(xx,testY,testZ)){
						canSpawn = false;
						break;
					}
				}
				
				if (!canSpawn)break;
			}

			if (!canSpawn)continue;
			
			if (world.isAir(xx-2,yy,zz-2) || world.isAir(xx-2,yy,zz+2)||
				world.isAir(xx+2,yy,zz-2) || world.isAir(xx+2,yy,zz+2))continue;
			
			// Cave generation
			
			double rx = 3.5D+rand.nextDouble()*1.5D, ry = 2.4D+rand.nextDouble()*0.6D, rz = 3.5D+rand.nextDouble()*1.5D;
			int lowestY = 999;
			
			for(int ix = (int)(xx-rx-1); ix <= xx+rx+1; ix++){
				for(int iy = (int)(yy-ry-1); iy <= yy+ry+1; iy++){
					for(int iz = (int)(zz-rz-1); iz <= zz+rz+1; iz++){
						if (MathUtil.square(xx-ix)/(rx*rx)+MathUtil.square(yy-iy)/(ry*ry)+MathUtil.square(zz-iz)/(rz*rz) <= 0.9D+rand.nextDouble()*0.2D){ // add in ellipsoid stuff
							world.setBlock(ix,iy,iz,Blocks.air);
							if (iy < lowestY)lowestY = iy;
						}
					}
				}
			}
			
			for(int a = 0,attempts = 70+(int)(rand.nextDouble()*rand.nextDouble()*75D); a < attempts; a++){
				int ix = xx+rand.nextInt(10)-5,
					iy = yy+1-rand.nextInt(5),
					iz = zz+rand.nextInt(10)-5;
				
				if (world.getBlock(ix,iy,iz) == Blocks.end_stone && world.isAir(ix,iy+1,iz)){
					world.setBlock(ix,iy,iz,Blocks.stonebrick,rand.nextInt(3));
				}
			}
			
			// Spawner
			
			world.setBlock(xx,lowestY,zz,BlockList.custom_spawner,1);
			
			if (/*biome.hasRareVariation(RareVariationForest.SPECIAL_DUNGEONS) &&*/ rand.nextInt(5) <= 1){ // TODO
				for(int nextSpawnerAttempt = 0,spawnerX,spawnerY,spawnerZ; nextSpawnerAttempt < 10; nextSpawnerAttempt++){
					spawnerX = xx+rand.nextInt(10)-5;
					spawnerY = yy+rand.nextInt(7)-5;
					spawnerZ = zz+rand.nextInt(10)-5;
					
					if (world.isAir(spawnerX,spawnerY,spawnerZ) && world.isAir(spawnerX,spawnerY+1,spawnerZ) && !world.isAir(spawnerX,spawnerY-1,spawnerZ)){
						world.setBlock(spawnerX,spawnerY,spawnerZ,BlockList.custom_spawner,1);
						break;
					}
				}
			}
			
			// Chest
			
			boolean placedChest = false;
			int chestX = 0,chestY = 0,chestZ = 0;
			
			for(int chestAttempt = 0; chestAttempt < 40; chestAttempt++){
				chestX = xx+rand.nextInt(10)-5;
				chestY = yy+rand.nextInt(5)-3;
				chestZ = zz+rand.nextInt(10)-5;
				
				if (world.isAir(chestX,chestY,chestZ) && world.isAir(chestX,chestY+1,chestZ) && !world.isAir(chestX,chestY-1,chestZ)){
					placedChest = true;
					break;
				}
			}
			
			if (!placedChest){
				chestX = xx;
				chestY = lowestY+1;
				chestZ = zz;
			}
			
			world.setBlock(chestX,chestY,chestZ,Blocks.chest,0);
			world.setTileEntityGenerator(chestX,chestY,chestZ,"silverfishDungeonChest",this);
			
			break;
		}
		
		return true;
	}

	@Override
	public void onTileEntityRequested(String key, TileEntity tile, Random rand){
		if (key.equals("silverfishDungeonChest") && tile instanceof TileEntityChest){
			TileEntityChest chest = (TileEntityChest)tile;
			for(int a = 0; a < 7+rand.nextInt(4)+rand.nextInt(6)+(/*biome.hasRareVariation(RareVariationForest.SPECIAL_DUNGEONS)?3+rand.nextInt(4):*/0); a++){ // TODO
				chest.setInventorySlotContents(rand.nextInt(chest.getSizeInventory()),lootDungeon.generateIS(rand));
			}
		}
	}
}

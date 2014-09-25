package chylex.hee.world.structure.island.biome.interaction;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.block.BlockList;
import chylex.hee.entity.fx.FXType;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.biome.data.AbstractBiomeInteraction;

public final class BiomeInteractionsInfestedForest{
	public static class InteractionCollapsingTrees extends AbstractBiomeInteraction{
		private byte treesLeft, timer = -1;
		private int x, y, z;
		
		@Override
		public void init(){
			treesLeft = (byte)(1+rand.nextInt(3+rand.nextInt(4+rand.nextInt(5))));
			
			for(int attempt = 0; attempt < 32; attempt++){
				x = centerX+rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize;
				z = centerZ+rand.nextInt(ComponentIsland.size)-ComponentIsland.halfSize;
				y = world.getHeightValue(x,z);
				boolean foundLog = false;
				
				for(int yy = y; yy > y-10; yy--){
					if (world.getBlock(x,y,z) == BlockList.spooky_log)foundLog = true;
					else if (foundLog){
						attempt = 33;
						y = yy+1;
						break;
					}					
				}
			}
		}
		
		@Override
		public void update(){
			if (--timer < 0){
				if (world.getBlock(x,y,z) == BlockList.spooky_log){
					if (rand.nextInt(8) == 0 && world.getGameRules().getGameRuleBooleanValue("doTileDrops")){
						EntityItem item = new EntityItem(world,x+rand.nextFloat()*0.7F+0.15F,y+rand.nextFloat()*0.7F+0.15F,z+rand.nextFloat()*0.7F+0.15F,new ItemStack(ItemList.dry_splinter));
						item.delayBeforeCanPickup = 10;
			            world.spawnEntityInWorld(item);
					}
					
					PacketPipeline.sendToAllAround(world.provider.dimensionId,x+0.5D,y+0.5D,z+0.5D,64D,new C20Effect(FXType.Basic.SPOOKY_LOG_DECAY,x,y,z));
					world.setBlockToAir(x,y,z);
					++y;
					timer = 4;
				}
				else if (--treesLeft <= 0){
					entity.setDead();
					return;
				}
				else{
					for(int attempt = 0, xx, yy, zz; attempt < 64; attempt++){
						xx = x+rand.nextInt(14)-7;
						zz = z+rand.nextInt(14)-7;
						yy = world.getHeightValue(xx,zz);
						boolean foundLog = false;
						
						for(int yAttempt = 0; yAttempt < 10; yAttempt++){
							if (world.getBlock(xx,--yy,zz) == BlockList.spooky_log)foundLog = true;
							else if (foundLog){
								attempt = 65;
								x = xx;
								y = yy+1;
								z = zz;
								break;
							}					
						}
					}
				}
			}
			
			if (timer <= -1)timer = (byte)(8+rand.nextInt(10)+rand.nextInt(6)*rand.nextInt(4));
		}

		@Override
		public void saveToNBT(NBTTagCompound nbt){
			nbt.setByte("left",treesLeft);
			nbt.setByte("tim",timer);
			nbt.setInteger("x",x);
			nbt.setInteger("y",y);
			nbt.setInteger("z",z);
		}

		@Override
		public void loadFromNBT(NBTTagCompound nbt){
			treesLeft = nbt.getByte("left");
			timer = nbt.getByte("tim");
			x = nbt.getInteger("x");
			y = nbt.getInteger("y");
			z = nbt.getInteger("z");
		}
	}
}

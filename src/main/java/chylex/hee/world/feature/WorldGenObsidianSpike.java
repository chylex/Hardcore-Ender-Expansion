package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

public class WorldGenObsidianSpike extends WorldGenBase{
	@Override
	public boolean generate(World world, Random rand, BlockPosM pos){
		BlockPosM tmpPos = pos.copy();
		
		if (tmpPos.isAir(world) && tmpPos.moveDown().getBlock(world) == Blocks.end_stone){
			int radius = rand.nextInt(4)+1;

			for(int xx = pos.x-radius; xx <= pos.x+radius; xx++){
				for(int zz = pos.z-radius; zz <= pos.z+radius; zz++){
					if (MathUtil.square(xx-pos.x)+MathUtil.square(zz-pos.z) <= radius*radius+1 && tmpPos.moveTo(xx,pos.y-1,zz).getBlock(world) != Blocks.end_stone){
						return false;
					}
				}
			}
			
			int height = rand.nextInt(32)+6;

			for(int yy = pos.y; yy < pos.y+height && yy < 128; yy++){
				for(int xx = pos.x-radius; xx <= pos.x+radius; xx++){
					for(int zz = pos.z-radius; zz <= pos.z+radius; zz++){
						if (MathUtil.square(xx-pos.x)+MathUtil.square(zz-pos.z) <= radius*radius+1){
							tmpPos.moveTo(xx,yy,zz).setBlock(world,BlockList.obsidian_falling,2);
						}
					}
				}
			}
			
			boolean bars = height > 20 && rand.nextInt(5) <= 1;
			
			if (bars){
				for(int xx = pos.x-radius; xx <= pos.x+radius; xx++){
					for(int zz = pos.z-radius; zz <= pos.z+radius; zz++){
						if (!tmpPos.moveTo(xx,pos.y+height-1,zz).isAir(world)){
							boolean hasMoreAir = false;
							
							for(int ax = -1; ax <= 1; ax++){
								for(int az = -1; az <= 1; az++){
									if (tmpPos.moveTo(xx+ax,pos.y+height-1,zz+az).isAir(world)){
										hasMoreAir = true;
										ax = 2;
										break;
									}
								}
							}
							
							if (hasMoreAir)tmpPos.moveTo(xx,pos.y+height,zz).setBlock(world,Blocks.iron_bars,2);
						}
					}
				}
			}

			EntityBlockEnderCrystal crystal = new EntityBlockEnderCrystal(world);
			crystal.setLocationAndAngles(pos.x+0.5D,pos.y+height,pos.z+0.5D,rand.nextFloat()*360F,0F);

			crystal.setCrystalType(
				bars ? EntityBlockEnderCrystal.BARS :
				(rand.nextInt(4) != 0 && height*radius*radius*Math.PI < 320D) ? EntityBlockEnderCrystal.BLAST :
				EntityBlockEnderCrystal.TNT
			);
			
			world.spawnEntityInWorld(crystal);
			tmpPos.moveTo(pos.x,pos.y+height,pos.z).setBlock(world,Blocks.bedrock,2);
			
			crystal.setCrystalKey(WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).addCrystal(pos.x,pos.y+height,pos.z));
			
			return true;
		}

		return false;
	}
}

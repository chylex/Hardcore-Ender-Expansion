package chylex.hee.world.feature.old;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.game.savedata.WorldDataHandler;
import chylex.hee.game.savedata.types.DragonSavefile;
import chylex.hee.init.BlockList;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

public class WorldGenObsidianSpike extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		BlockPosM tmpPos = BlockPosM.tmp();
		
		if (tmpPos.set(x,y,z).isAir(world) && tmpPos.moveDown().getBlock(world) == Blocks.end_stone){
			int radius = rand.nextInt(4)+1;

			for(int xx = x-radius; xx <= x+radius; ++xx){
				for(int zz = z-radius; zz <= z+radius; ++zz){
					if (MathUtil.square(xx-x)+MathUtil.square(zz-z) <= radius*radius+1 && tmpPos.set(xx,y-1,zz).getBlock(world) != Blocks.end_stone){
						return false;
					}
				}
			}
			
			int height = rand.nextInt(32)+6;

			for(int yy = y; yy < y+height && yy < 128; ++yy){
				for(int xx = x-radius; xx <= x+radius; ++xx){
					for(int zz = z-radius; zz <= z+radius; ++zz){
						if (MathUtil.square(xx-x)+MathUtil.square(zz-z) <= radius*radius+1){
							tmpPos.set(xx,yy,zz).setBlock(world,BlockList.obsidian_falling,0,2);
						}
					}
				}
			}
			
			boolean bars = height > 20 && rand.nextInt(5) <= 1;
			
			if (bars){
				for(int xx = x-radius; xx <= x+radius; ++xx){
					for(int zz = z-radius; zz <= z+radius; ++zz){
						if (!world.isAirBlock(xx,y+height-1,zz)){
							boolean hasMoreAir = false;
							
							for(int ax = -1; ax <= 1; ax++){
								for(int az = -1; az <= 1; az++){
									if (world.isAirBlock(xx+ax,y+height-1,zz+az)){
										hasMoreAir = true;
										ax = 2;
										break;
									}
								}
							}
							
							if (hasMoreAir)tmpPos.set(xx,y+height,zz).setBlock(world,Blocks.iron_bars,0,2);
						}
					}
				}
			}

			EntityBlockEnderCrystal crystal = new EntityBlockEnderCrystal(world);
			crystal.setLocationAndAngles(x+0.5D,y+height,z+0.5D,rand.nextFloat()*360F,0F);

			crystal.setCrystalType(
				bars ? EntityBlockEnderCrystal.Type.BARS :
				(rand.nextInt(4) != 0 && height*radius*radius*Math.PI < 320D) ? EntityBlockEnderCrystal.Type.BLAST :
				EntityBlockEnderCrystal.Type.TNT
			);
			
			world.spawnEntityInWorld(crystal);
			tmpPos.set(x,y+height,z).setBlock(world,Blocks.bedrock,0,2);
			
			crystal.setCrystalKey(WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).addCrystal(x,y+height,z));
			
			return true;
		}

		return false;
	}
}

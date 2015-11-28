package chylex.hee.world.structure;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos.PosMutable;

/**
 * Represents a Structure World that can be inserted into another one.
 */
public class StructureWorldPart extends StructureWorld{
	public static final IWorldPositionPredicate requireAir = (targetWorld, rand, x, y, z) -> targetWorld.isAir(x,y,z) && targetWorld.isInside(x,y,z);
	
	public StructureWorldPart(World world, int radX, int sizeY, int radZ){
		super(world,radX,sizeY,radZ);
	}
	
	public StructureWorldPart(int radX, int sizeY, int radZ){
		super(null,radX,sizeY,radZ);
	}
	
	/**
	 * Inserts everything in this StructureWorld into another one, at the specified location. Any unset blocks are ignored.
	 */
	public void insertInto(StructureWorld targetWorld, int centerX, int bottomY, int centerZ){
		PosMutable pos = new PosMutable();
		int x, y, z, index = -1;
		
		for(z = -radZ; z <= radZ; z++){
			for(x = -radX; x <= radX; x++){
				for(y = 0; y < sizeY; y++){
					if (blocks[++index] != null){
						targetWorld.setBlock(centerX+x,bottomY+y,centerZ+z,blocks[index],metadata[index]);
					}
				}
			}
		}
		
		attentionWhores.forEachEntry((ind, value) -> {
			toPos(ind,pos);
			targetWorld.setAttentionWhore(centerX+pos.x,bottomY+pos.y,centerZ+pos.z,value);
			return true;
		});
		
		tileEntityMap.forEachEntry((ind, value) -> {
			toPos(ind,pos);
			targetWorld.setTileEntity(centerX+pos.x,bottomY+pos.y,centerZ+pos.z,value);
			return true;
		});
		
		scheduledUpdates.forEach(ind -> {
			toPos(ind,pos);
			pos.move(centerX,bottomY,centerZ);
			if (targetWorld.isInside(pos.x,pos.y,pos.z))targetWorld.scheduledUpdates.add(targetWorld.toIndex(pos.x,pos.y,pos.z));
			return true;
		});
		
		entityList.forEach(info -> {
			Entity entity = info.getKey();
			entity.setPosition(centerX+entity.posX,bottomY+entity.posY,centerZ+entity.posZ);
			targetWorld.entityList.add(info);
		});
	}
	
	public boolean insertIf(StructureWorld targetWorld, Random rand, int centerX, int bottomY, int centerZ, @Nullable IWorldPositionPredicate predicate){
		if (predicate != null){
			int x, y, z, index = -1;
			
			for(z = -radZ; z <= radZ; z++){
				for(x = -radX; x <= radX; x++){
					for(y = 0; y < sizeY; y++){
						if (blocks[++index] != null && !predicate.check(targetWorld,rand,centerX+x,bottomY+y,centerZ+z)){
							return false;
						}
					}
				}
			}
		}
		
		insertInto(targetWorld,centerX,bottomY,centerZ);
		return true;
	}
}

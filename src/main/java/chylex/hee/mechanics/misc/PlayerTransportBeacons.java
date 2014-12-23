package chylex.hee.mechanics.misc;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.misc.PlayerDataHandler.IExtendedPropertyInitializer;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.MathUtil;

public class PlayerTransportBeacons implements IExtendedEntityProperties{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~TransportBeacons";
	
	public static void register(){
		PlayerDataHandler.registerProperty(playerPropertyIdentifier,new IExtendedPropertyInitializer<PlayerTransportBeacons>(){
			@Override
			public PlayerTransportBeacons createNew(Entity entity){
				return new PlayerTransportBeacons();
			}
		});
	}
	
	public static PlayerTransportBeacons getInstance(EntityPlayer player){
		return ((PlayerTransportBeacons)player.getExtendedProperties(playerPropertyIdentifier));
	}
	
	private Set<LocationXZ> locations = new HashSet<>();
	
	public boolean addBeacon(int x, int z){
		return locations.add(new LocationXZ(x,z));
	}
	
	public Set<LocationXZ> getOffsets(int x, int z){
		LocationXZ center = new LocationXZ(x,z);
		Set<LocationXZ> near = new HashSet<>();
		
		for(LocationXZ loc:locations){
			if (loc.distance(center) <= 1024D)near.add(new LocationXZ(loc.x-x,loc.z-z));
		}
		
		return near;
	}

	@Override
	public void init(Entity entity, World world){}

	@Override
	public void saveNBTData(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		for(LocationXZ loc:locations)list.appendTag(new NBTTagIntArray(new int[]{ loc.x, loc.z }));
		nbt.setTag("HEE_TB_locs",list);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt){
		locations.clear();
		
		NBTTagList list = nbt.getTagList("HEE_TB_locs",NBT.TAG_INT_ARRAY);
		
		for(int a = 0; a < list.tagCount(); a++){
			int[] loc = list.func_150306_c(a);
			
			if (loc.length == 2)locations.add(new LocationXZ(loc[0],loc[1]));
			else Log.error("Corrupted data found when loading transport beacon, expected an int array of size 2, got $0",loc.length);
		}
	}
	
	public static final class LocationXZ{
		public final int x, z;
		
		public LocationXZ(int x, int z){
			this.x = x;
			this.z = z;
		}
		
		public double distance(LocationXZ loc){
			return MathUtil.distance(loc.x-x,loc.z-z);
		}
		
		@Override
		public boolean equals(Object obj){
			if (obj != null && obj.getClass() == LocationXZ.class){
				LocationXZ loc = (LocationXZ)obj;
				return loc.x == x && loc.z == z;
			}
			else return false;
		}
		
		@Override
		public int hashCode(){
			return x*6666689+z;
		}
	}
}

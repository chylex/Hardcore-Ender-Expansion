package chylex.hee.mechanics.misc;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import chylex.hee.mechanics.misc.PlayerDataHandler.IExtendedPropertyInitializer;

public class PlayerTransportBeacons implements IExtendedEntityProperties{
	private static final String playerPropertyIdentifier = "HardcoreEnderExpansion~TransportBeacons";
	
	static void register(){
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
	
	/*public Set<LocationXZ> getNear(int x, int z){
		
	}*/

	@Override
	public void init(Entity entity, World world){}

	@Override
	public void saveNBTData(NBTTagCompound nbt){}

	@Override
	public void loadNBTData(NBTTagCompound nbt){}
	
	public static final class LocationXZ{
		public final int x, z;
		
		LocationXZ(int x, int z){
			this.x = x;
			this.z = z;
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

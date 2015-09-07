package chylex.hee.system;
import java.util.HashSet;
import java.util.Set;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent.MissingMapping;

public final class ModTransition{
	public static void refreshMappings(FMLMissingMappingsEvent e){
		final String id = "HardcoreEnderExpansion:";
		final Set<String> discard = new HashSet<>();
		
		discard.add(id+"transport_beacon");
		discard.add(id+"temple_end_portal");
		discard.add(id+"biome_compass");
		discard.add(id+"adventurers_diary");
		discard.add(id+"altar_nexus");
		discard.add(id+"temple_caller");
		
		e.get().stream().filter(mapping -> discard.contains(mapping.name)).forEach(MissingMapping::ignore);
	}
	
	private ModTransition(){}
}

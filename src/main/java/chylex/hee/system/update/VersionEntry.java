package chylex.hee.system.update;
import chylex.hee.system.util.DragonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class VersionEntry implements Comparable<VersionEntry>{
	final String versionIdentifier;
	final String modVersionName;
	final String modVersion;
	final String[] mcVersions;
	final String releaseDate;
	private final Short orderId;
	
	VersionEntry(String versionIdentifier, JsonObject node){
		this.versionIdentifier = versionIdentifier;
		modVersion = node.get("modVersion").getAsString();
		
		JsonArray array = node.get("mcVersions").getAsJsonArray();
		mcVersions = new String[array.size()];
		int a = -1;
		for(JsonElement mcVersionNode:array)mcVersions[++a] = mcVersionNode.getAsString();
		
		releaseDate = node.get("releaseDate").getAsString();
		
		short i = 0;
		String tmp = modVersion;
		String[] idSplit = versionIdentifier.split(" - ");
		
		if (idSplit.length != 2)DragonUtil.warning("Incorrect version identifier: "+versionIdentifier);
		else{
			tmp = idSplit[1];
			
			try{
				i = Short.parseShort(idSplit[0]);
			}catch(NumberFormatException e){
				DragonUtil.warning("Incorrect version identifier: "+versionIdentifier);
			}
		}
		orderId = i;
		modVersionName = tmp;
	}
	
	boolean isSupportedByMC(String mcVersion){
		for(String version:mcVersions){
			if (version.equals(mcVersion))return true;
		}
		return false;
	}

	@Override
	public int compareTo(VersionEntry o){
		return o.orderId.compareTo(orderId);
	}
}

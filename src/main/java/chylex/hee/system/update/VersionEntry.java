package chylex.hee.system.update;
import chylex.hee.system.logging.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class VersionEntry implements Comparable<VersionEntry>{
	public final String versionIdentifier;
	public final String modVersionName;
	public final String modVersion;
	public final String[] mcVersions;
	public final String releaseDate;
	private final Byte orderId;
	
	VersionEntry(String versionIdentifier, JsonObject node){
		this.versionIdentifier = versionIdentifier;
		modVersion = node.get("modVersion").getAsString();
		
		JsonArray array = node.get("mcVersions").getAsJsonArray();
		mcVersions = new String[array.size()];
		int a = -1;
		for(JsonElement mcVersionNode:array)mcVersions[++a] = mcVersionNode.getAsString();
		
		releaseDate = node.get("releaseDate").getAsString();
		
		byte i = 0;
		String tmp = modVersion;
		String[] idSplit = versionIdentifier.split(" - ");
		
		if (idSplit.length != 2)Log.error("Incorrect version identifier: $0",versionIdentifier);
		else{
			tmp = idSplit[1];
			
			try{
				i = Byte.parseByte(idSplit[0]);
			}catch(NumberFormatException e){
				Log.error("Incorrect version identifier: $0",versionIdentifier);
			}
		}
		
		orderId = Byte.valueOf(i);
		modVersionName = tmp;
	}
	
	public boolean isSupportedByMC(String mcVersion){
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

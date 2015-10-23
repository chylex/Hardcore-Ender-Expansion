package chylex.hee.mechanics.compendium.content.fragments;

public enum FragmentType{
	/**
	 * Always unlocked as long as the Knowledge Object is visible.
	 */
	VISIBLE,
	
	/**
	 * Unlocked when a parent Knowledge Object is discovered.
	 */
	ESSENTIAL,
	
	/**
	 * Unlocked when the owning Knowledge Object is discovered.
	 */
	DISCOVERY,
	
	/**
	 * Unlocked on a custom event.
	 */
	HINT,
	
	/**
	 * Unlocked using Knowledge Points.
	 */
	SECRET
}
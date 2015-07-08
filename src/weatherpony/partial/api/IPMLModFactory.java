package weatherpony.partial.api;

import java.io.File;

import weatherpony.pml.implementorapi.ExternalLibraryDependancy;

/**
 * This is the starting point for mods using my loader. This class is responsible for creating the mod objects themselves, technically allowing for self-updating mods.
 * @author The_WeatherPony
 */
public abstract class IPMLModFactory{
	private IPMLFactoryLoadAPI loadAPI;
	public final void setPMLFactoryLoadAPI(IPMLFactoryLoadAPI loadAPI){
		if(this.loadAPI != null && this.loadAPI!=loadAPI)
			throw new IllegalStateException();
		this.loadAPI = loadAPI;
	}
	protected IPMLFactoryLoadAPI getFactoryLoadAPI(){
		return this.loadAPI;
	}
	public final void preinit(){
		this.preinit(getFactoryLoadAPI());
	}
	protected abstract void preinit(IPMLFactoryLoadAPI loadAPI);
	public ExternalLibraryDependancy[] externalDependancies(){
		return null;
	}
	public abstract File[] loadModFrom(File loadedThisFrom);//this lets the mod load from another file, allowing for self-updating mods, if the main part of the mod is in another zip
	public abstract String[] noASMOn_prefixs();
	public abstract IPMLMod[] loadMod();
}

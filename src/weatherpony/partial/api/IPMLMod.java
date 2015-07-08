package weatherpony.partial.api;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;

import weatherpony.partial.hook.ReflectionAssistance;
import weatherpony.partial.launch.PMLMain;
import weatherpony.pml.implementorapi.ExternalLibraryDependancy;
import weatherpony.pml.implementorapi.PMLSetup;
import weatherpony.pml.implementorapi.PMLSetup.DependancyException;

/**
 * This is the primary mod class used in my loader. While it isn't an interface anymore, it still acts like one, so the 'I' remains in it's name.  
 * @author The_WeatherPony
 */
public abstract class IPMLMod implements Callable<String>{
	public final String modName;
	protected IPMLMod(String modName){
		this.modName = modName;
	}
	private IPMLModLoadAPI modLoadAPI;
	public final void giveModLoadAPI(IPMLModLoadAPI loadAPI){
		if(this.modLoadAPI != null && this.modLoadAPI != loadAPI)
			throw new IllegalStateException();
		this.modLoadAPI = loadAPI;
	}
	protected final IPMLModLoadAPI getModLoadAPI(){
		return this.modLoadAPI;
	}
	public void init(){
		this.init(this.getModLoadAPI());
	}
	public void init(IPMLModLoadAPI loadAPI){ }
	
	public void givePMLMods(List<IPMLMod> mods){ }
	public void alertOfSelfError(){ }
	public void alertOfErroredMods(List<IPMLMod> mods){ }
	
	public void postinit(){ }
	
	public void applicationRecommendedLoadTime(ClassLoader loader){
		
	}
	public static void loadExternalDependancy(ExternalLibraryDependancy lib, ClassLoader loader) throws DependancyException{
		PMLSetup.getSetup().loadExternalDependancy(lib, loader);
	}
	public static <T> T makeObject(ClassLoader loader, String className){
		try{
			return (T) loader.loadClass(className).newInstance();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	@Deprecated //not yet ready
	public static <T> T makeObject(ClassLoader loader, String className, Object...constructorParameters){
		try{
			Class clazz = loader.loadClass(className);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return null;
	}
	public static <T> T makeObject(ClassLoader loader, String className, Class[] constructorParameterTypes, Object... constructorParameters){
		try {
			return (T) loader.loadClass(className).getConstructor(constructorParameterTypes).newInstance(constructorParameters);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	//PML Mod communications, just in case you need it. This only provides a means, not a manner. It's up to implementers to work out how to use it.
	protected abstract Object interpretMessage(IPMLMod fromMod, Object message);
	protected final Object sendMessage(IPMLMod toMod, Object message){
		return toMod.interpretMessage(this, message);
	}
	@Override
	public String call() throws Exception{
		return this.modName;
	}
	public static ReflectionAssistance getReflectionHelper(){
		return PMLMain.reflectHelper;
	}
}

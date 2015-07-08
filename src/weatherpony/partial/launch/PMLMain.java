package weatherpony.partial.launch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import weatherpony.partial.HookRegistration;
import weatherpony.partial.api.IHookRegistrar;
import weatherpony.partial.api.IObfuscationHelper;
import weatherpony.partial.api.IObfuscationRegistrar;
import weatherpony.partial.api.IPMLFactoryLoadAPI;
import weatherpony.partial.api.IPMLModLoadAPI;
import weatherpony.partial.asmedit.ExtentionListenerTransformer;
import weatherpony.partial.asmedit.HookInjectorTransformer;
import weatherpony.partial.asmedit.MethodInjectionAndSuperCorrectionTransformer;
import weatherpony.partial.hook.ReflectionAssistance;
import weatherpony.partial.internal.ClassData;
import weatherpony.partial.internal.GeneralHookManager;
import weatherpony.partial.internal.ObfuscationHelper3;
import weatherpony.partial.internal.OverridingManager;
import weatherpony.partial.modloading.PMLModManager;
import weatherpony.pml.implementorapi.IProgramInformation;
import weatherpony.pml.implementorapi.PMLSetup;
import weatherpony.pml.launch.IClassManipulator;
import weatherpony.pml.launch.PMLRoot;

public class PMLMain implements Callable<Void>{
	public static final int PML_majorVersion = 8;
	public static final int PML_minorVersion = 2;
	public static PMLMain instance;
	public static GeneralHookManager hookmanager;
	
	public PMLMain(){
		System.out.println("PMLMain being initialized");
		instance = this;
		
		classdata = new ClassData();
		overridingmanager = new OverridingManager();
		ObfuscationHelper3 obfhelper = new ObfuscationHelper3();
		this.obfhelp = obfhelper;
		this.obfreg = obfhelper;
		this.hookmanager = new GeneralHookManager(obfhelper);
		this.hookRegistrar = new HookRegistration();
	}
	@Override
	public Void call() throws Exception{
		List<IClassManipulator> transformers = new ArrayList();
		transformers.add(new ExtentionListenerTransformer());//learn about class hierarchy
		
		PMLSetup appsetup = PMLSetup.getSetup();
		IProgramInformation appinfo = appsetup.getPMLApplicationAPI();
		IPMLModLoadAPI modAPI = new ModLoadAPIImplementation(this.PML_majorVersion, this.PML_minorVersion, appinfo, this.obfreg, this.obfhelp, this.hookRegistrar);
		IPMLFactoryLoadAPI factoryAPI = new FactoryLoadAPIImplementation(this.PML_majorVersion, this.PML_minorVersion, appinfo, modAPI);
		
		modmanager = new PMLModManager(appsetup.getModLocator(), factoryAPI, modAPI);
		this.reflectHelper = new ReflectionAssistance(this.obfhelp);
		
		
		transformers.add(new MethodInjectionAndSuperCorrectionTransformer());
		
		transformers.add(new HookInjectorTransformer());//add hooks
		
		PMLRoot.addManipulators(transformers);
		
		return null;
	}
	public static ReflectionAssistance reflectHelper;
	
	
	public IObfuscationHelper getObfHelper(){
		return this.obfhelp;
	}

	private final IObfuscationRegistrar obfreg;
	private final IObfuscationHelper obfhelp;
	
	private final IHookRegistrar hookRegistrar;
	
	private ClassData classdata;
	private OverridingManager overridingmanager;
	private PMLModManager modmanager;
}

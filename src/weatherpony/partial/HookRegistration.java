package weatherpony.partial;

import java.lang.reflect.Method;
import java.util.List;

import weatherpony.partial.api.IHookRegistrar;
import weatherpony.partial.internal.GeneralHookManager;
import weatherpony.partial.internal.OverridingManager;

//this is currently unfinished. Most of it works, at least
public class HookRegistration implements IHookRegistrar{
	//public static final HookRegistration INSTANCE = new HookRegistration();
	/**
	 * This is to register calls annotated with {@link#CallHook}
	 * @param mod - the name of your mod
	 * @param instance - the object to parse
	 */
	public void register(String mod, Object instance){
		if(instance == null)
			return;
		//List<CallWrapper> hooks = HookListing.INSTANCE.getMod_make(mod);
		Class clazz = instance.getClass();
		Method[] methods = clazz.getMethods();//must be public, can be from a superclass or interface.
		for(Method each : methods){
			CallHook hook = each.getAnnotation(CallHook.class);
			if(hook != null){
				Class inClass = each.getDeclaringClass();
				String methodName = each.getName();
				final String params = "Lweatherpony/partial/HookListenerHelper;";
				//TODO - finish support for annotated-method based hooks
			}
		}
	}
	@Override
	public void register(CallData data, ICallListener call, String mod){
		this.register(mod, new CallWrapperI(data, call));
	}
	@Override
	public void register(String mod, CallData data, ICallListener call){
		this.register(data, call, mod);
	}
	@Override
	public void register(String mod, CallWrapper cw){
		this.register(cw, mod);
	}
	@Override
	public void register(CallWrapper cw, String mod){
		if(cw == null)
			return;
		GeneralHookManager.register(mod, cw);
		//List<CallWrapper> hooks = HookListing.INSTANCE.getMod_make(mod);
		//hooks.add(cw);
	}
	@Override
	public void register(List<CallWrapper> cws, String mod){
		if(cws == null || cws.isEmpty())
			return;
		GeneralHookManager.register(mod, cws);
		//List<CallWrapper> hooks = HookListing.INSTANCE.getMod_make(mod);
		//hooks.addAll(cws);	
	}
	@Override
	public void register(String mod, List<CallWrapper> cws){
		this.register(cws, mod);
	}
	@Override
	public void registerForSuperOverride(String inClass, String method,	String desc, String expectedToOverrideFrom, String originalSource){
		OverridingManager.addRequest(inClass, method, desc.replace('.', '/'), expectedToOverrideFrom, originalSource);
	}
}

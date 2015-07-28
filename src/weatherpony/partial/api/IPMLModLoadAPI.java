package weatherpony.partial.api;


public interface IPMLModLoadAPI extends IPMLLoadAPI{
	public static final int currentAPIVersionNumber = 5;
	static final int[] compatibleAPIVersionNumbers = {4,5};
	//PML obfuscation registration
	public IObfuscationRegistrar getObfuscationRegistrar();
	//PML obfuscation help for reflection
	public IObfuscationHelper getObfuscationHelper();
	//PML dynamic hook registration
	public IHookRegistrar getHookRegistrar();
	//PML sub-class method override registration
}

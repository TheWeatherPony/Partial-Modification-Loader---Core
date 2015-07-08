package weatherpony.partial.api;

import weatherpony.pml.implementorapi.IProgramInformation;

public interface IPMLLoadAPI{
	//PML API version number. Make sure this is what you're expecting before doing anything else. This is different for the factory and mod load APIs, but uses this same method
	public int apiVersionNumber();
	//PML API version compatibility check. This is for if the above isn't what you're expecting. You could also just check here first.
	public boolean isDirectlyCompatible(int otherAPINumber);
	
	//PML version information. You shouldn't need to care about this, but it's here just in case.
	public int majorVersionNumber();
	public int minorVersionNumber();
	
	public IProgramInformation getProgramInformation();
}

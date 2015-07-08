package weatherpony.partial.launch;

import weatherpony.partial.api.IPMLLoadAPI;
import weatherpony.pml.implementorapi.IProgramInformation;

public abstract class LoadAPIImplementation implements IPMLLoadAPI{
	LoadAPIImplementation(int pmlv1, int pmlv2, IProgramInformation appinfo){
		this.pmlv1 = pmlv1;
		this.pmlv2 = pmlv2;
		this.appinfo = appinfo;
	}
	final int pmlv1, pmlv2;
	final IProgramInformation appinfo;
	@Override
	public int majorVersionNumber() {
		return this.pmlv2;
	}
	@Override
	public int minorVersionNumber() {
		return this.pmlv1;
	}
	@Override
	public IProgramInformation getProgramInformation(){
		return this.appinfo;
	}
}

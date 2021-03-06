package weatherpony.partial.launch;

import weatherpony.partial.api.IHookRegistrar;
import weatherpony.partial.api.IObfuscationHelper;
import weatherpony.partial.api.IObfuscationRegistrar;
import weatherpony.partial.api.IPMLModLoadAPI;
import weatherpony.pml.implementorapi.IProgramInformation;

public class ModLoadAPIImplementation extends LoadAPIImplementation implements IPMLModLoadAPI{
	
	ModLoadAPIImplementation(int pmlv1, int pmlv2, IProgramInformation appinfo, IObfuscationRegistrar obfreg, IObfuscationHelper obfhelp, IHookRegistrar hookRegistrar){
		super(pmlv1, pmlv2, appinfo);
		this.obfreg = obfreg;
		this.obfhelp = obfhelp;
		this.hookRegistrar = hookRegistrar;
	}
	final IObfuscationRegistrar obfreg;
	final IObfuscationHelper obfhelp;
	final IHookRegistrar hookRegistrar;
	@Override
	public int apiVersionNumber(){
		return this.currentAPIVersionNumber;
	}
	@Override
	public boolean isDirectlyCompatible(int otherAPINumber){
		for(int comp : this.compatibleAPIVersionNumbers){
			if(comp == otherAPINumber)
				return true;
		}
		return false;
	}
	@Override
	public IObfuscationRegistrar getObfuscationRegistrar(){
		return this.obfreg;
	}
	@Override
	public IObfuscationHelper getObfuscationHelper(){
		return this.obfhelp;
	}
	@Override
	public IHookRegistrar getHookRegistrar(){
		return this.hookRegistrar;
	}
}

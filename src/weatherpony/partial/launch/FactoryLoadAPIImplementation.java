package weatherpony.partial.launch;

import weatherpony.partial.api.IPMLFactoryLoadAPI;
import weatherpony.partial.api.IPMLModLoadAPI;
import weatherpony.pml.implementorapi.IProgramInformation;

public class FactoryLoadAPIImplementation extends LoadAPIImplementation implements IPMLFactoryLoadAPI{
	FactoryLoadAPIImplementation(int pmlv1, int pmlv2, IProgramInformation appinfo, IPMLModLoadAPI modAPI) {
		super(pmlv1, pmlv2, appinfo);
		this.modAPI = modAPI;
	}
	final IPMLModLoadAPI modAPI;
	@Override
	public int apiVersionNumber() {
		return this.currentAPIVersionNumber;
	}
	@Override
	public boolean isDirectlyCompatible(int otherAPINumber) {
		for(int comp : this.compatibleAPIVersionNumbers){
			if(comp == otherAPINumber)
				return true;
		}
		return false;
	}
	@Override
	public int modAPIVersionNumber() {
		return this.modAPI.apiVersionNumber();
	}
	@Override
	public boolean modAPIVersionNumberDirectlyCompatible(int modAPINumber) {
		return this.modAPI.isDirectlyCompatible(modAPINumber);
	}
}

package weatherpony.partial.api;

public interface IPMLFactoryLoadAPI extends IPMLLoadAPI{
	public static final int currentAPIVersionNumber = 4;
	static final int[] compatibleAPIVersionNumbers = {4};
	public int modAPIVersionNumber();
	public boolean modAPIVersionNumberDirectlyCompatible(int modAPINumber);
}

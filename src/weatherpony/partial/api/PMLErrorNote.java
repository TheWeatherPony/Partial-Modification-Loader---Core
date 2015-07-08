package weatherpony.partial.api;

public class PMLErrorNote{
	public PMLErrorNote(IPMLMod errored, Throwable error){
		this.errored = errored;
		this.error = error;
	}
	public final IPMLMod errored;
	public final Throwable error;
	
}

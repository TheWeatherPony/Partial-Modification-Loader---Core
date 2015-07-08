package weatherpony.partial;

//This functions like Google's Throwables.propagate(e), but is it's own type of RuntimeException, which helps distinguish different problems
public class WrappedException extends RuntimeException {
	private WrappedException(Throwable e){
		super(e);//just in case it shows up in traces 
		this.e = e;
	}
	public final Throwable e;
	public static WrappedException wrap(Throwable e){
		if(e instanceof WrappedException)
			return (WrappedException) e;
		return new WrappedException(e);
	}
}

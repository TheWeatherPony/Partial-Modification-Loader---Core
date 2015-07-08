package weatherpony.partial.api;

/**
 * Only throw an instance of this during setup if PML cannot continue loading.
 * Throw something else if it's just that your mod can't continue, but PML can. (Using this isn't suggested, but it's here if needed.)
 */
public class PMLCriticalError extends Error{
	public PMLCriticalError(){
		super();
	}
	public PMLCriticalError(String message){
		super(message);
	}
	public PMLCriticalError(Throwable cause){
		super(cause);
	}
	public PMLCriticalError(String message, Throwable cause){
		super(message, cause);
	}
}

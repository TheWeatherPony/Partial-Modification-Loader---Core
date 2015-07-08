package weatherpony.partial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.google.common.base.Throwables;

public class ErrorLogger{
	private static final PrintWriter out;
	static{
		File file = new File("PML-internalErrorLog.txt");
		try {
			out = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw Throwables.propagate(e);
		}
	}
	private static String calledFrom(){
		return PMLSecurityManager.getStackClass(2).getName();
	}
	public static void addToLog(String line){
		out.append(calledFrom()).append(": ").append(line).println();
		out.flush();
	}
	public static <E extends Throwable> E addToLog(E error){
		out.append(calledFrom()).append(": ").println();
		out.append('\t').append(error.getClass().getName()).println();
		for(StackTraceElement each : error.getStackTrace()){
			out.append('\t').append(each.toString()).println();
		}
		Throwable e = error.getCause();
		while(e != null){
			out.append("  ").append("Caused by:").append(' ').append(e.getClass().getName()).println();
			for(StackTraceElement each : e.getStackTrace()){
				out.append('\t').append(each.toString()).println();
			}
			e = e.getCause();
		}
		out.flush();
		return error;
	}
	public static void addToLog(StackTraceElement[] trace){
		out.append(calledFrom()).append(": ").println();
		for(StackTraceElement each : trace){
			out.append('\t').append(each.toString()).println();
		}
		out.flush();
	}
}

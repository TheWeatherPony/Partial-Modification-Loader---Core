package weatherpony.partial.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;

import weatherpony.pml.implementorapi.PMLSetup;
import weatherpony.pml.launch.PMLLoadFocuser;
import weatherpony.pml.launch.PMLRoot;

public class PMLStart implements Callable<Callable<Void>>{
	public PMLStart(){
		System.out.println("PMLStart loading");
		Class pmlsetup = null;
		try {
			pmlsetup = Class.forName("weatherpony.pml.implementorapi.PMLSetup");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClassLoader setuploader = pmlsetup.getClassLoader();
		PMLSetup setup = PMLSetup.getSetup();
		PMLRoot.addSecondManipulators(new EnviornmentCuller(setup.getEnviornmentLoader(), (Enum)setup.getApplicationEnviornment()));
	}
	@Override
	public Callable<Void> call() throws Exception{
		return (Callable<Void>) Thread.currentThread().getContextClassLoader().loadClass("weatherpony.partial.launch.PMLMain").getConstructor().newInstance();
	}
}

package weatherpony.partial.modloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import weatherpony.partial.api.IPMLFactoryLoadAPI;
import weatherpony.partial.api.IPMLMod;
import weatherpony.partial.api.IPMLModFactory;
import weatherpony.partial.api.IPMLModLoadAPI;
import weatherpony.partial.api.PMLCriticalError;
import weatherpony.partial.api.PMLErrorNote;
import weatherpony.partial.internal.GeneralHookManager;
import weatherpony.pml.implementorapi.IPMLModLocator;
import weatherpony.pml.implementorapi.IPMLPluginManagement;
import weatherpony.pml.implementorapi.IPMLPluginManagementListener;
import weatherpony.pml.implementorapi.IPMLLoadDirector;
import weatherpony.pml.implementorapi.PMLSetup;
import weatherpony.pml.launch.PMLRoot;

import com.google.common.collect.Iterators;

public class PMLModManager implements IPMLPluginManagement{
	private List<IPMLMod> mods;
	private final IPMLModLoadAPI modloadAPI;
	private class Finder implements IPMLLoadDirector{
		List<File> likelyCandidates = new ArrayList();
		List<String> headers = new ArrayList();
		
		@Override
		public void searchDirectoryForMods(File folder, final boolean everythingIsAPMLMod) {
			File[] subFiles = folder.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name){
					if(name.toLowerCase().endsWith(".pmlm"))//.Partial Modification Loader Mod
						return true;
					if(everythingIsAPMLMod)
						return new File(dir, name).isFile();
					return false;
				}
			});
			if(subFiles != null)
				likelyCandidates.addAll(Arrays.asList(subFiles));
		}

		@Override
		public void loadMod(File modlocation) {
			likelyCandidates.add(modlocation);
		}

		@Override
		public void searchForLocalMod(String headerName) {
			headers.add(headerName);
		}
		
	}
	public PMLModManager(IPMLModLocator locator, IPMLFactoryLoadAPI factoryLoadAPI, IPMLModLoadAPI modLoadAPI){
		System.out.println("PML Mod Manager being created");
		this.modloadAPI = modLoadAPI;
		URLClassLoader ucl = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		
		ArrayList<IPMLMod> mods = new ArrayList();
		
		PMLSetup.getSetup().givePluginManager(this);
		
		Finder finder = new Finder();
		locator.onLoad(finder);
		
		for(File each : finder.likelyCandidates){
			if(!each.exists())
				throw new IllegalArgumentException();
		}
		
		finder.likelyCandidates.add(0, null);
		
		
		
		Iterator<? extends Object> iter = Iterators.concat(finder.headers.iterator(), finder.likelyCandidates.iterator());
		ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
		while(iter.hasNext()){
			Object eachelem = iter.next();
			BufferedReader guideFile = null;
			ZipFile zip = null;
			File each = null;
			String header = "pml.info";
			if(eachelem instanceof File){
				each = (File)eachelem;
			}else if(eachelem instanceof String){
				header = (String)eachelem;
			}else if(eachelem == null){
				
			}else{
				throw new IllegalStateException();
			}
			
			System.out.println("PML Mod Manager is looking at "+(each == null ? "<main path>" : each.getAbsolutePath())+" : "+header);
			
			
			if(each == null){
				InputStream ins = ucl.getResourceAsStream(header);
				if(ins != null){
					guideFile = new BufferedReader(new InputStreamReader(ins));
					System.out.println("opening "+header+" from path");
				}
			}else if(each.isDirectory()){
				File guide = new File(each, header);
				if(guide.exists() && guide.isFile()){
					try {
						guideFile = new BufferedReader(new FileReader(guide));
						System.out.println("opening "+header+" from directory: "+each.getAbsolutePath());
					}catch (FileNotFoundException e){
						continue;
					}
				}
			}else{//each.isFile()
				try{
					zip = new ZipFile(each);
				}catch (Exception e){
					continue;
				}
				ZipEntry entry = zip.getEntry(header);
				if(entry != null)
					try {
						guideFile = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
						System.out.println("opening "+header+" from .pmlm: "+each.getAbsolutePath());
					} catch (IOException e) {
						if(zip != null)
							try{
								zip.close();
							}catch(Exception e1){
							}
					}
			}
			if(guideFile != null){
				try {
					boolean hasReadAtLeastOne = false;
					List<IPMLModFactory> factories = new ArrayList();
					while(guideFile.ready()){
						String factoryClass = guideFile.readLine();
						System.out.println("PML Mod Manager read about Factory '"+factoryClass+"'");
						
						if(factoryClass != null){
							
							if(each != null && !hasReadAtLeastOne){
								try {
									Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
									m.setAccessible(true);
									m.invoke(ucl, each.toURI().toURL());
								} catch (MalformedURLException e){
									e.printStackTrace();
									continue;
								}
							}
							hasReadAtLeastOne = true;
							try{
								factories.add((weatherpony.partial.api.IPMLModFactory)(Class.forName(factoryClass).newInstance()));
								System.out.println("PML Mod Manager found mod factory \""+factoryClass+"\" in: "+(each == null? "<main path>" : each.getAbsolutePath()));
							}catch(Throwable e){
								if(e instanceof PMLCriticalError)
									throw (PMLCriticalError)e;
								e.printStackTrace();
								continue;
							}
						}
					}
					if(!hasReadAtLeastOne){
						System.out.println("PML Mod Manager found a PML-based mod, but couldn't figure out anything about it. :/ (in: "+(each == null? "<main path>" : each.getAbsolutePath())+" )");
					}else{
						for(IPMLModFactory factory : factories){
							factory.setPMLFactoryLoadAPI(factoryLoadAPI);
							factory.preinit();
							
							String[] noASM = factory.noASMOn_prefixs();
							if(noASM != null){
								for(String eachnoasm : noASM){
									PMLRoot.addTransformationException(eachnoasm);
								}
							}
							IPMLMod[] newmods = factory.loadMod();
							if(newmods != null){
								mods.addAll(Arrays.asList(newmods));
							}
						}
					}
				}catch(Throwable e){
					if(e instanceof PMLCriticalError)
						throw (PMLCriticalError)e;
					System.out.println("got error");
					e.printStackTrace(System.out);
					continue;
				}finally{
					if(zip != null)
						try{
							zip.close();
						}catch(IOException e){
						}
				}
			}
		}
		
		this.mods = Collections.unmodifiableList(mods);
		
		ArrayList<PMLErrorNote> errors = new ArrayList();
		for(IPMLMod eachmod : mods){
			try{
				eachmod.giveModLoadAPI(modLoadAPI);
				eachmod.init();
				eachmod.givePMLMods(this.mods);
			}catch(Throwable e){
				if(e instanceof PMLCriticalError)
					throw (PMLCriticalError)e;
				errors.add(new PMLErrorNote(eachmod,e));
			}
		}
		
		//TODO - deal with the errors, and continue writing error handling
		
		for(IPMLMod eachmod : mods){
			try{
				GeneralHookManager.addMod(eachmod.modName, true);
			}catch(Throwable e){
				if(e instanceof PMLCriticalError)
					throw (PMLCriticalError)e;
				errors.add(new PMLErrorNote(eachmod,e));
			}
		}
		if(!errors.isEmpty()){
			System.out.println("found errored mods:");
			for(PMLErrorNote each : errors){
				System.out.println(" "+each.errored.modName+": ");
				each.error.printStackTrace(System.out);
			}
		}
	}
	@Override
	public void applicationRecommendedLoadTime(ClassLoader loader){
		for(IPMLMod eachmod : mods){
			try{
				eachmod.applicationRecommendedLoadTime(loader);
			}catch(Throwable e){
				if(e instanceof PMLCriticalError)
					throw (PMLCriticalError)e;
			}
		}
	}
	private IPMLPluginManagementListener listener;
	private boolean listening;
	@Override
	public void giveListener(IPMLPluginManagementListener listener){
		if(this.listening && this.listener != listener)
			throw new IllegalArgumentException();
		this.listener = listener;
		this.listening = true;
	}
	@Override
	public List<IPMLMod> getMods(){
		return this.mods;
	}
	@Override
	public boolean isModActive(Callable<String> mod){
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void enableMod(Callable<String> mod){
		// TODO Auto-generated method stub
		
	}
	@Override
	public void disableMod(Callable<String> mod){
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setModEnabledState(Callable<String> mod, boolean state) {
		// TODO Auto-generated method stub
		
	}
}

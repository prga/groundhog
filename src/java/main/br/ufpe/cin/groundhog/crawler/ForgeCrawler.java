package br.ufpe.cin.groundhog.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import br.ufpe.cin.groundhog.Project;
import br.ufpe.cin.groundhog.main.JsonInput;

/**
 * An abstract class that defines the forge crawl functionality.
 * A forge crawler downloads projects from a certain forge
 * to a given folder. 
 * @author fjsj, gustavopinto
 *
 */
public abstract class ForgeCrawler {
	
	private ExecutorService ex;
	
	/**
	 * Constructs a new ForgeCrawler with a given destinationFolder.
	 */
	protected ForgeCrawler() {
		this.ex = Executors.newFixedThreadPool(JsonInput.getMaxThreads());
	}
	
	/**
	 * Downloads a single project and returns its repository folder. A project repository folder is useful to be
	 * manipulated by CodeHistory class and to be parsed by Parser class. 
	 * 
	 * @param project a ForgeProject.
	 * @return a repository folder (if has SCM as Git or SVN) or a project folder (which contains archived/compressed files)
	 * @throws Exception when something nasty happens
	 */
	protected abstract File downloadProject(Project project) throws Exception;
	
	/**
	 * Downloads the given list of projects, returning futures with files objects.
	 * Each file represents the repository folder of a project,
	 * according to ForgeProject list ordering. To get a future result, 
	 * just call its .get() method. A project repository folder is useful to be
	 * manipulated by CodeHistory class and to be parsed by Parser class.
	 * 
	 * @param projects list of ForgeProjects, usually given by a ForgeSearch subclass
	 * @return list of futures with repository folders as File objects
	 */
	public List<Future<File>> downloadProjects(List<Project> projects) {
		List<Future<File>> fs = new ArrayList<Future<File>>();
		
		for (final Project p : projects) {
			Future<File> f = ex.submit(new Callable<File>() {
				public File call() throws Exception {
					return downloadProject(p);
				}
			});
			fs.add(f);
		}
		
		shutdown();
		
		return fs;
	}
	
	/**
	 * Guarantees downloads to be executed, but no new downloads will be accepted.
	 * Should be called after downloadProjects.
	 */
	private void shutdown() {
		ex.shutdownNow();
	}
	
}
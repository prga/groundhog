package br.ufpe.cin.groundhog.crawler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.groundhog.Project;
import br.ufpe.cin.groundhog.search.SearchGitHub;
import br.ufpe.cin.groundhog.search.SearchModule;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CrawlGitHubTest {

	private SearchGitHub searchGitHub;

	@Before
	public void setup() {
		Injector injector = Guice.createInjector(new SearchModule());
		searchGitHub = injector.getInstance(SearchGitHub.class);
	}

	@Test
	public void testCrawlGithub() {
		long time = System.nanoTime();

		try {

			Project playframework = searchGitHub.getProjects("playframework", 1).get(0);
			List<Project> projects = Arrays.asList(playframework);
			CrawlGitHub crawl = new CrawlGitHub(Files.createTempDir());
			List<Future<File>> fs = crawl.downloadProjects(projects);
			crawl.shutdown();
			for (Future<File> f : fs) {
				File file = f.get();
				Assert.assertNotNull(file);
			}
			
			System.out.printf("Elapsed: %.2f",
					(System.nanoTime() - time) / 1000000000.0);
			
		} catch (Exception e) {
			Assert.fail();
		}
	}
}

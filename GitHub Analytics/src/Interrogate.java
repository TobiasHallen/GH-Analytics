import java.io.IOException;

import org.eclipse.egit.github.core.*;
import com.google.gson.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

public class Interrogate 
{
	public static void main(String[] args) throws IOException 
	{
		GitHubClient client = new GitHubClient();
		client.setCredentials("GithubTesteroni", "examplepassword123");

		RepositoryService service = new RepositoryService();
		for (Repository repo : service.getRepositories("GithubTesteroni"))
		  System.out.println(repo.getName() + " Watchers: " + repo.getWatchers());

	}
	
}

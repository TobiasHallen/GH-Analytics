import java.io.IOException;
import java.util.List;

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
		Repository repo = service.getRepository("eclipse", "egit-github");
		List<Contributor> contributors = service.getContributors(repo, false);
		for(int i = 0;i<contributors.size();i++)
		{
			System.out.println(contributors.get(i).getLogin());
		}
	}
	
}

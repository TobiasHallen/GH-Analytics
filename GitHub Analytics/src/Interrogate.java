import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.Spring;
import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import org.eclipse.egit.github.core.*;
import com.google.gson.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;


public class Interrogate 
{
	static GitHubClient client = new GitHubClient();
	public static String DESTINATION = "";
	
	public static void main(String[] args) throws IOException 
	{
		String userName = JOptionPane.showInputDialog("Please enter the Login of the user you would like to view (leave blank for default user):");
		if ((userName == null)) 
		{
			System.exit(0);	
		}
		if(userName.equals(""))
		{
			userName = "GithubTesteroni";
		}
		String code = JOptionPane.showInputDialog("Please enter that user's personal access token (leave blank for default user token):");
		if ((code == null)) 
		{
			System.exit(0);	
		}
		if(code.equals(""))
		{
				code = "a7316ed0fdfc9d8bdfe96c2642953a45cae7e49a";
		}	
		client.setCredentials(userName, code);
        DESTINATION = new File("").getAbsolutePath() + "/data";		
		for(int i = 50; i<=300;i+=50)
		{
			buildDatabase(userName, i);
		}
	}

	public static List<String> getFollows(String user) throws IOException
	{
		UserService service = new UserService(client);
		List<User> followingList = service.getFollowing(user);
		List<String> stringList = new ArrayList<String>();
		for(int i=0;i<followingList.size();i++)
		{
			stringList.add(followingList.get(i).getLogin());
		}
		return stringList;
	}

	public static int getRepos(String user) throws IOException
	{
		UserService service = new UserService(client);
		User u = service.getUser(user);
		return u.getPublicRepos()+u.getTotalPrivateRepos();
	}

	public static void buildDatabase(String user, int limit) throws IOException
	{
		int i = 0;
		List<LinkIndex> linkIndex = new ArrayList<LinkIndex>();
		Queue<String> q = new LinkedList<>();
		Map<String, Integer> userIndex = new HashMap<>();
		ToJSON tj = new ToJSON();
		q.add(user);
		userIndex.put(user, i);
		tj.users.add(new UserData(user, getRepos(user)));

		i++;
		while(!q.isEmpty()&&i<limit)
		{
			user = q.remove();
			int x = getRepos(user);
			List<String> followings = getFollows(user);
			for(String follow: followings)
			{
				if(!userIndex.containsKey(follow))
				{
					q.add(follow);
					userIndex.put(follow, i);
					x = getRepos(follow);
					tj.users.add(new UserData(follow, x));
					i++;
					if(i==limit)
					{
						linkIndex.add(new LinkIndex(userIndex.get(user), userIndex.get(follow)));
						break;
					}				
				}
				linkIndex.add(new LinkIndex(userIndex.get(user), userIndex.get(follow)));
			}
		}
		
		tj.index = linkIndex;
		writeJSON(tj, limit);
	}
	
	static void writeJSON(ToJSON tj, int limit) throws IOException
	{
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String output = mapper.writeValueAsString(tj);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DESTINATION + "/" +limit+ "graph" + ".json"))) 
        {
            bw.write(output);
        } 
        catch (Exception e) 
        {
            System.out.println("Error creating file.");
        }
       
	}
}

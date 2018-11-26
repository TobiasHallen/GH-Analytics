import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.Spring;

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
	public static String CURRENT_DATE = "";
	
	public static void main(String[] args) throws IOException 
	{
		client.setCredentials("", "a7316ed0fdfc9d8bdfe96c2642953a45cae7e49a");
        DESTINATION = new File("").getAbsolutePath() + "/";
		String user = "";	//set default user	
		for(int i = 150; i<=250;i+=50)
		{
			buildDatabase(user, i);
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
		int limit = 50;
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
//		for (String key : userIndex.keySet()) {
//			System.out.print(key+": ");
//			System.out.println(userIndex.get(key));
//		}
//		System.out.println();
//		for(LinkIndex x : linkIndex)
//		{
//			System.out.println(x.source+" : "+x.to);
//		}
		
		tj.index = linkIndex;
		System.out.println(tj.users.size());
		System.out.println(tj.index.size());
		writeJSON(tj, limit);
	}
	
	static void writeJSON(ToJSON tj, int limit) throws IOException
	{
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String output = mapper.writeValueAsString(tj);
        BufferedWriter bw = new BufferedWriter(new FileWriter(DESTINATION + "/" +limit+ "graph" + ".json"));
            bw.write(output);
       
	}
}

package jeliot.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class NetworkUtils {

	// From Adapt^2 protocols
	public static String getContent(String urlName) throws Exception
	{
		URL url = new URL (urlName);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		String responce = new String();
		//      receive the responce
		responce = readFromConnection(connection);
		connection.disconnect();
		return responce;
	}

	// From Adapt^2 protocols
	public static String readFromConnection(HttpURLConnection con)
		throws Exception
	{
		BufferedReader in = new BufferedReader(
			new InputStreamReader(con.getInputStream()));
		String inputLine = new String();
		String tmp = new String();
		
		while ((tmp = in.readLine()) != null)
		{
			inputLine = inputLine.concat(tmp);
			inputLine = inputLine.concat("\n");
		}
		in.close();
		return inputLine;
	}

}

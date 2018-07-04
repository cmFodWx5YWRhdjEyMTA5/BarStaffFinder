import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AndroidClient{

	private static String serverAddress;
	private static int serverPort;

	static {
		serverAddress = "127.0.0.1";
		serverPort = 1133;
	}

	private AndroidClient(){}


	static void getUsers(String page) throws MalformedURLException, IOException {
		URL url = new URL("http://"+ serverAddress + ":" + serverPort + "/" + page);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");	
		conn.setRequestProperty("Accept", "application/json");


		if(conn.getResponseCode() != 200){
			throw new RuntimeException("Failed: HTTP error code : " + conn.getResponseCode());
		}
			
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));


		String output = null;
		String tmp;
		System.out.println("Output from server ....");
		while((tmp = br.readLine()) != null){
			output = tmp;
		}

		System.out.println(output);

		conn.disconnect();
	}
}

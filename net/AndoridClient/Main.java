import java.net.MalformedURLException;
import java.io.IOException;

public class Main{
	public static void main(String[] args){
		try{
			AndroidClient.getUsers("staff/robert");
		} catch(MalformedURLException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}

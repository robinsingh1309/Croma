package service;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

public class ConnectToCroma {

	public String getJsonResponseOfCategoryUrl(String webUrl) throws IOException 
	{
		Response response = Jsoup.connect(webUrl)
				.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36")
				.referrer("https://www.croma.com/")
				.header("accept", "application/json, text/plain, */*")
				.timeout(10000)
				.method(Connection.Method.GET)
				.ignoreContentType(true)
				.execute();
		
		return response.body();
	}
	
}

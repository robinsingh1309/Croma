package extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import cromaEnum.CromaWebUrlEnum;
import service.ConnectToCroma;

public class CromaCatWebUrlExtractor {

	private static final Logger logger = Logger.getLogger(CromaCatWebUrlExtractor.class.getName());
	
	private final String croma_url = CromaWebUrlEnum.CROMA_URL_TO_EXTRACT_CATEGORY_URL.getValue();
	private final String filePath = "/home/robin/eclipse-workspace/TataCroma/src/csv/category_urls.txt";
	
	private final ConnectToCroma urlConnection;
	
	public CromaCatWebUrlExtractor() 
	{
		urlConnection = new ConnectToCroma();
	}
	
	public void getCategoryWebUrl() throws IOException 
	{
		logger.info("Starting extraction of category web URLs...");
		
		try (FileWriter writer = new FileWriter(new File(filePath)))
		{
			logger.info("Fetching JSON response from: " + croma_url);
			String jsonResponseCategoryUrl = urlConnection.getJsonResponseOfCategoryUrl(croma_url);
			
			logger.fine("Raw JSON response received, length: " + jsonResponseCategoryUrl.length());

			JSONObject jsonObject = new JSONObject(jsonResponseCategoryUrl);
			JSONObject contentSlots = jsonObject.getJSONObject("contentSlots");
			JSONArray contentSlot = contentSlots.getJSONArray("contentSlot");
			
			logger.info("Number of content slots: " + contentSlot.length());
			
			// since all my category url is in the first object
			JSONObject currentContentSlotObject = contentSlot.getJSONObject(0);
			JSONObject components = currentContentSlotObject.getJSONObject("components");
			JSONArray component = components.getJSONArray("component");
			
            logger.info("Number of components in slot: " + component.length());
			
			// since it has only one object
			JSONObject currentComponentObject = component.getJSONObject(0);
			JSONObject navigationNode = currentComponentObject.getJSONObject("navigationNode");
			JSONArray entries = navigationNode.getJSONArray("entries");
			
            logger.info("Number of entries found: " + entries.length());
			
            // i don't want the last object
			for(int i = 0; i < entries.length()-1; i++) 
			{
				JSONObject currentEntriesObject = entries.getJSONObject(i);
				String url = "https://www.croma.com" + currentEntriesObject.getString("url");
				
				writer.write(url + "\n");
				
				logger.fine("Extracted URL: " + url);
			}
			
			logger.info("Successfully written category URLs to: " + filePath);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error occurred while extracting category URLs", e);
		}	
	}	
}
package extractor;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class CromaDataExtractor {

	private final String filePath = "/home/robin/eclipse-workspace/TataCroma/src/csv/%s.txt";
	
	public void getCromaData(String jsonResponse, String fileName)
	{
		Map<Integer, String> codeWithName = new HashMap<Integer, String>();
		
		JSONObject jsonResponseObject = new JSONObject(jsonResponse);
		
		try (FileWriter writer = new FileWriter(new File(String.format(filePath, fileName))))
		{
			
			JSONArray facets = jsonResponseObject.getJSONArray("facets");
			
			// only the first object has all the information like what product is it with its code
			JSONObject firstFacetsObject = facets.getJSONObject(0);
			
			JSONArray values = firstFacetsObject.getJSONArray("values");
			
			for(int i = 0; i < values.length(); i++) 
			{	
				JSONObject currentValueObject = values.getJSONObject(i);
				
				String name = currentValueObject.getString("name");
				int code = Integer.parseInt(currentValueObject.getString("code"));
				
				if( !codeWithName.containsKey(code) )
					codeWithName.put(code, name);
				
			}
			
			JSONArray products = jsonResponseObject.getJSONArray("products");
			
			for(int j = 0; j < products.length(); j++) 
			{
				JSONObject currentProductsObject = products.getJSONObject(j);
				
				String product_manufacturer = currentProductsObject.getString("manufacturer");
				String product_name = currentProductsObject.getString("name");
				String quickViewDesc = currentProductsObject.getString("quickViewDesc");
				
				JSONObject mrp = currentProductsObject.getJSONObject("mrp");
				int product_mrp = mrp.getInt("value");
				
				String categoryL2 = String.valueOf(currentProductsObject.getString("categoryL2"));
				String type = codeWithName.get(categoryL2);
				
				writer.write(product_name + "," + product_manufacturer + "," + product_mrp + "," + type + "," + quickViewDesc + "\n");	
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		
	}
	
	public int getPageCount(String jsonResponse) 
	{
		JSONObject jsonResponseObject = new JSONObject(jsonResponse);
		
		JSONObject pagination = jsonResponseObject.getJSONObject("pagination");
		
		int totalPages = pagination.getInt("totalPages");
		
		return totalPages;
	}
	
}
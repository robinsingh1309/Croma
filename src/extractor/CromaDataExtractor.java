package extractor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CromaDataExtractor {

	private static final Logger logger = Logger.getLogger(CromaDataExtractor.class.getName());


	public void getCromaData(String jsonResponse, BufferedWriter writer) {

		Map<Integer, String> codeWithName = new HashMap<>();

		try {

			JSONObject jsonResponseObject = new JSONObject(jsonResponse);

			JSONArray facets = jsonResponseObject.optJSONArray("facets");

			if (facets != null && facets.length() > 0) {

				JSONObject firstFacetsObject = facets.getJSONObject(0);
				JSONArray values = firstFacetsObject.optJSONArray("values");

				if (values != null) {

					for (int i = 0; i < values.length(); i++) 
					{
						JSONObject currentValueObject = values.getJSONObject(i);

						String name = currentValueObject.optString("name", "");
						int code = Integer.parseInt(currentValueObject.optString("code", "0"));

						codeWithName.putIfAbsent(code, name);
					}
				}
			}

			JSONArray products = jsonResponseObject.optJSONArray("products");

			if (products == null) {
				logger.warning("No products found in response");
				return;
			}

			for (int j = 0; j < products.length(); j++) 
			{
				JSONObject currentProductsObject = products.getJSONObject(j);

				String product_manufacturer = currentProductsObject.optString("manufacturer", "N/A");
				String product_name = currentProductsObject.optString("name", "N/A");

				String quickViewDesc = currentProductsObject.optString("quickViewDesc", "N/A");
				StringBuilder descBuilder = new StringBuilder();

				if (!quickViewDesc.isEmpty()) 
				{
					/**
					 * since the description of product is stored in the form of HTML
					 * That's why i am using the jsoup here to extract the info from the html page which we are receiving from JSON response
					 */
					Document doc = Jsoup.parse(quickViewDesc);
					Elements listItems = doc.select("li");

					for (int i = 0; i < listItems.size(); i++) {

						descBuilder.append(listItems.get(i).text());

						// the below line is to make sure we don't add unnecessary pipe('|') in the end.
						if (i < listItems.size() - 1) {
							descBuilder.append(" | ");
						}
					}

					JSONObject mrp = currentProductsObject.optJSONObject("mrp");
					int product_mrp = (mrp != null) ? mrp.optInt("value", 0) : 0;

					int categoryL2 = Integer.parseInt(currentProductsObject.optString("categoryL2", "0"));
					String type = codeWithName.getOrDefault(categoryL2, "Unknown");



					writer.write(product_name + "," + product_manufacturer + "," +
							product_mrp + "," + type + "," + descBuilder.toString() + "\n");
				}

				writer.flush();
				logger.info("Data extraction & writing completed successfully");
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, "I/O error while writing product data", e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unexpected error while extracting product data", e);
		}
	}

	public int getPageCount(String jsonResponse) {

		try 
		{
			JSONObject jsonResponseObject = new JSONObject(jsonResponse);
			JSONObject pagination = jsonResponseObject.getJSONObject("pagination");

			return pagination.optInt("totalPages", 1);

		} catch (Exception e) 
		{
			logger.log(Level.WARNING, "Error extracting page count", e);

			return 1;
		}
	}
}

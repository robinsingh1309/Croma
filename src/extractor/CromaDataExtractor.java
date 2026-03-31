package extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CromaDataExtractor {

    private static final Logger logger = Logger.getLogger(CromaDataExtractor.class.getName());

    // Returns the next available row number
    public int getCromaData(String jsonResponse, XSSFSheet sheet, int rowNum) {

        Map<Integer, String> codeWithName = new HashMap<>();

        try {

            JSONObject jsonResponseObject = new JSONObject(jsonResponse);

            JSONArray facets = jsonResponseObject.optJSONArray("facets");

            if (facets != null && facets.length() > 0) {

                JSONObject firstFacetsObject = facets.getJSONObject(0);
                JSONArray values = firstFacetsObject.optJSONArray("values");

                if (values != null) {

                    for (int i = 0; i < values.length(); i++) {
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
                return rowNum;
            }

            for (int j = 0; j < products.length(); j++) {

                JSONObject currentProductsObject = products.getJSONObject(j);

                String product_manufacturer = currentProductsObject.optString("manufacturer", "");
                String product_name = currentProductsObject.optString("name", "");
                String product_image = currentProductsObject.optString("plpImage", "");
                
                String quickViewDesc = currentProductsObject.optString("quickViewDesc", "");
                StringBuilder descBuilder = new StringBuilder();

                if (!quickViewDesc.isEmpty()) {
                    
                    Document doc = Jsoup.parse(quickViewDesc);
                    Elements listItems = doc.select("li");

                    for (int i = 0; i < listItems.size(); i++) {
                        descBuilder.append(listItems.get(i).text());
                        
                        if (i < listItems.size() - 1)
                            descBuilder.append(" | ");
                    }
                }

                JSONObject mrp = currentProductsObject.optJSONObject("mrp");
                int product_mrp = (mrp != null) ? mrp.optInt("value", 0) : 0;

                int categoryL2 = Integer.parseInt(currentProductsObject.optString("categoryL2", "0"));
                String type = codeWithName.getOrDefault(categoryL2, "");

                // Write to Excel row
                Row row = sheet.createRow(rowNum++);
                int col = 0;
                row.createCell(col++).setCellValue(type);
                row.createCell(col++).setCellValue(product_name);
                row.createCell(col++).setCellValue(descBuilder.toString());
                row.createCell(col++).setCellValue(product_image);
                row.createCell(col++).setCellValue(product_manufacturer);
                row.createCell(col++).setCellValue(product_mrp);
                
            }

            logger.info("Data extraction completed for " + products.length() + " products");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error while extracting product data", e);
        }

        return rowNum;
    }

    public int getPageCount(String jsonResponse) {
        
        try {
        
            JSONObject jsonResponseObject = new JSONObject(jsonResponse);
            JSONObject pagination = jsonResponseObject.getJSONObject("pagination");
            
            return pagination.optInt("totalPages", 1);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error extracting page count", e);
            return 1;
        }
    }
}

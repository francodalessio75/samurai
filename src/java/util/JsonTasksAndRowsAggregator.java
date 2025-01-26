
package util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Map;
import java.util.TreeMap;
import com.google.gson.JsonElement;


/**
 *
 * @author franc
 */
public class JsonTasksAndRowsAggregator {
    
    public static JsonArray processAndGroupJsonTasksArray(JsonArray tasksJsonArray) {

        // Group by "denomination" and "code" and sum the "cost"
        Map<String, Double> groupedData = new TreeMap<>();

        for (JsonElement element : tasksJsonArray) {
            JsonObject node = element.getAsJsonObject();
            String denomination = node.get("denomination").getAsString();
            String code = node.get("code").getAsString();
            double cost = node.get("cost").getAsDouble();

            String key = denomination + "|" + code; // Combine fields as a key
            groupedData.put(key, groupedData.getOrDefault(key, 0.0) + cost);
        }

        // Prepare the sorted output array
        JsonArray resultArray = new JsonArray();

        for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String denomination = keyParts[0];
            String code = keyParts[1];
            double totalCost = entry.getValue();

            JsonObject groupedObject = new JsonObject();
            groupedObject.addProperty("denomination", denomination);
            groupedObject.addProperty("code", code);
            groupedObject.addProperty("cost", totalCost);

            resultArray.add(groupedObject);
        }

        return resultArray;
    }
    public static JsonArray processAndGroupJsonRowsArray(JsonArray rowsJsonArray) {

        Map<String, Double> groupedData = new TreeMap<>();

        for (JsonElement element : rowsJsonArray) {
            JsonObject node = element.getAsJsonObject();
            String denomination = node.get("denomination").getAsString();
            String code = node.get("code").getAsString();
            if( code.isEmpty()) 
                code = "noCode";
            double amount = node.get("amount").getAsDouble();

            String key = denomination + "|" + code; // Combine fields as a key
            groupedData.put(key, groupedData.getOrDefault(key, 0.0) + amount);
        }

        // Prepare the sorted output array
        JsonArray resultArray = new JsonArray();

        for (Map.Entry<String, Double> entry : groupedData.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String denomination = keyParts[0];
            String code = keyParts[1];
            double totalAmount = entry.getValue();

            JsonObject groupedObject = new JsonObject();
            groupedObject.addProperty("denomination", denomination);
            groupedObject.addProperty("code", code);
            groupedObject.addProperty("amount", totalAmount);

            resultArray.add(groupedObject);
        }

        return resultArray;
    }
    
    public static JsonArray mergeRowsAndTasks(JsonArray rows, JsonArray tasks) {
        JsonArray resultArray = new JsonArray();

        // Iterate through the first array
        for (JsonElement element1 : rows) {
            JsonObject obj1 = element1.getAsJsonObject();
            String denomination = obj1.get("denomination").getAsString();
            String code = obj1.get("code").getAsString();
            double amount = obj1.get("amount").getAsDouble();

            // Search for a matching object in the second array
            double cost = 0;
            for (JsonElement element2 : tasks) {
                JsonObject obj2 = element2.getAsJsonObject();
                if (denomination.equals(obj2.get("denomination").getAsString()) &&
                        code.equals(obj2.get("code").getAsString())) {
                    cost = obj2.get("cost").getAsDouble();
                    break;
                }
            }

            // Calculate the margin
            double margin = 0;
            if( amount != 0 && cost != 0 ){
                margin = ((amount - cost) / amount) * 100;
            }
            else if( amount == 0 && cost >= 0 ){
                margin = 0;
            }
            else if( amount > 0 && cost == 0 ){
                margin = 100;
            }

            // Create the merged object
            JsonObject mergedObject = new JsonObject();
            mergedObject.addProperty("denomination", denomination);
            mergedObject.addProperty("code", code);
            mergedObject.addProperty("cost", cost);
            mergedObject.addProperty("amount", amount);
            mergedObject.addProperty("margin", margin);

            // Add to the result array
            resultArray.add(mergedObject);
        }

        return resultArray;
    }

}

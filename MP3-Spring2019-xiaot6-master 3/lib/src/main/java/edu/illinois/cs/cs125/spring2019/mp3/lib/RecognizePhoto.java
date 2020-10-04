package edu.illinois.cs.cs125.spring2019.mp3.lib;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonNull;

/**
 * my class.
 */
public class RecognizePhoto {
    /**
     * @param json input;
     * @return output;
     */
    public static int getWidth(final java.lang.String json) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return 0;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return 0;
        }
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        if (metadata ==  null) {
            return 0;
        }
        JsonElement width = metadata.get("width");
        if (width == null) {
            return 0;
        }
        int number = width.getAsInt();
        return number;
    }

    /**
     * @param json -input
     * @return -output;
     */
    public static int getHeight(final java.lang.String json) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return 0;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return 0;
        }
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        if (metadata ==  null) {
            return 0;
        }
        JsonElement height = metadata.get("height");
        if (height == null) {
            return 0;
        }
        int number = height.getAsInt();
        return number;
    }
    /**
     * @param json -input
     * @return -output;
     */
    public static java.lang.String getFormat(final java.lang.String json) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return null;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return null;
        }
        JsonObject metadata = result.get("metadata").getAsJsonObject();
        if (metadata ==  null) {
            return null;
        }
        JsonElement format = metadata.get("format");
        if (format == null) {
            return null;
        }
        java.lang.String number = format.getAsString();
        return number;
    }

    /**
     *
     * @param json -input
     * @return -output;
     */
    public static java.lang.String getCaption(final java.lang.String json) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return null;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return null;
        }
        JsonObject description = result.get("description").getAsJsonObject();
        if (description == null) {
            return null;
        }
        JsonArray captions = description.get("captions").getAsJsonArray();
        if (captions == null) {
            return null;
        }
        JsonObject ele = captions.get(0).getAsJsonObject();
        if (ele == null) {
            return null;
        }
        java.lang.String text = ele.get("text").getAsString();
        return text;
    }

    /**
     *
     * @param json -input
     * @param minConfidence -input
     * @return -output;
     */
    public static boolean isADog(final java.lang.String json, final double minConfidence) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return false;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return false;
        }
        JsonArray tags = result.get("tags").getAsJsonArray();
        if (tags == null) {
            return false;
        }
        for (int i = 0; i < tags.size(); i++) {
            JsonObject[] nameArray = new JsonObject[tags.size()];
            nameArray[i] = tags.get(i).getAsJsonObject();
            if (nameArray[i] == null) {
                return false;
            }
            if (nameArray[i].get("name").getAsString().equals("dog")) {
                if (nameArray[i].get("confidence").getAsDouble() >= minConfidence) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param json -input
     * @param minConfidence -input
     * @return -output;
     */
    public static boolean isACat(final java.lang.String json, final double minConfidence) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return false;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return false;
        }
        JsonArray tags = result.get("tags").getAsJsonArray();
        if (tags == null) {
            return false;
        }
        for (int i = 0; i < tags.size(); i++) {
            JsonObject[] nameArray = new JsonObject[tags.size()];
            nameArray[i] = tags.get(i).getAsJsonObject();
            if (nameArray[i] == null) {
                return false;
            }
            if (nameArray[i].get("name").getAsString().equals("cat")) {
                if (nameArray[i].get("confidence").getAsDouble() >= minConfidence) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param json -input
     * @return -output;
     */
    public static boolean isRick(final java.lang.String json) {
        JsonParser parser = new JsonParser();
        if (json == null) {
            return false;
        }
        JsonObject result = parser.parse(json).getAsJsonObject();
        if (result == null) {
            return false;
        }
        JsonObject description = result.get("description").getAsJsonObject();
        if (description == null) {
            return false;
        }
        JsonArray captions = description.get("captions").getAsJsonArray();
        if (captions == null) {
            return false;
        }
        JsonObject ele = captions.get(0).getAsJsonObject();
        if (ele == null) {
            return false;
        }
        java.lang.String text = ele.get("text").getAsString();
        if (text.indexOf("Rick Astley") >= 0) {
            return true;
        }
        return false;
    }

}

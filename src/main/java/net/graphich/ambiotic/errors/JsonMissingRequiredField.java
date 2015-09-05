package net.graphich.ambiotic.errors;

/**
 * Created by jim on 9/5/2015.
 */
public class JsonMissingRequiredField extends JsonError {
    public JsonMissingRequiredField(String fieldName) {
        super("Definition missing required field '"+fieldName+"'");
    }
}

package net.graphich.ambiotic.errors;

public class JsonMissingRequiredField extends JsonError {
    public JsonMissingRequiredField(String fieldName) {
        super("Definition missing required field '"+fieldName+"'");
    }
}

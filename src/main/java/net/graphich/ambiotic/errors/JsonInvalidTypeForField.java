package net.graphich.ambiotic.errors;

/**
 * Created by jim on 9/5/2015.
 */
public class JsonInvalidTypeForField extends JsonError {
    public JsonInvalidTypeForField(String fieldName, String validTypes) {
        super("Invalid JSON type for field '"+fieldName+"', must be "+validTypes);
    }
}

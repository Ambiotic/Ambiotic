package net.graphich.ambiotic.errors;

public class JsonInvalidTypeForField extends JsonError {
    public JsonInvalidTypeForField(String fieldName, String validTypes) {
        super("Invalid JSON type for field '"+fieldName+"', must be "+validTypes);
    }
}

package net.graphich.ambiotic.errors;

public class JsonInvalidTypeForListElement extends JsonError {
    public JsonInvalidTypeForListElement(int element, String validTypes) {
        super("Invalid JSON type for list element '"+element+"', must be "+validTypes);
    }
}

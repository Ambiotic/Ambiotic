package net.graphich.ambiotic.errors;

public class JsonInvalidResource extends JsonError {
    public JsonInvalidResource(String field, String resource) {
        super("Invalid resource '" + resource + "' specified for "+field);
    }
}

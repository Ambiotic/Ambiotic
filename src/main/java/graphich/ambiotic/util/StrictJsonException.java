package graphich.ambiotic.util;

import com.google.gson.JsonParseException;

public class StrictJsonException extends JsonParseException {
    public StrictJsonException(String msg) {
        super(msg);
    }
}

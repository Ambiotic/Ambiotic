package net.graphich.ambiotic.util;

public interface StrictJson {

    public void validate() throws StrictJsonException;
    public void initialize();

}

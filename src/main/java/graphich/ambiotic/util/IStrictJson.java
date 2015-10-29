package graphich.ambiotic.util;

public interface IStrictJson {
    public void validate() throws StrictJsonException;
    public void initialize();
}

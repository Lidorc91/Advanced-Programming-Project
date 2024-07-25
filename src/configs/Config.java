package configs;

/**
 * This interface defines the methods that a configuration class must implement.
 */

public interface Config {
    void create();
    String getName();
    int getVersion();
    void close();
}

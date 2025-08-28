package co.com.crediya.model.user.gateways;

public interface LoggerService {
    void info(String message, Object... args);
    void debug(String message, Object... args);
    void error(String message, Throwable t);
}

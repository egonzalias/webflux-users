package co.com.crediya.model.user.gateways;

public interface PasswordService {

    // BCrypt operations (encode, matches) are fast, CPU-bound, and synchronous.
    // There's no I/O or blocking involved, so wrapping them in Mono/Flux is unnecessary
    // and would add complexity without performance benefit.
    // If needed, they can be wrapped reactively at higher layers using Mono.fromCallable(...).
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}

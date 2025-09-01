package co.com.crediya.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String document_number;
    private String first_name;
    private String last_name;
    private LocalDate birth_date;
    private String address;
    private String phone;
    private String email;
    private BigDecimal base_salary;
    private String password;
    private String role;
    private String jwtToken;
    private Date expiresAt;
}

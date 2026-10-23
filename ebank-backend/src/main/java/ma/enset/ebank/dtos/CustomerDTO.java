package ma.enset.ebank.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
    private String createdBy;
}

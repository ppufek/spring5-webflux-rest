package guru.springframework.spring5webfluxrest.apimodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorUpdate {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}

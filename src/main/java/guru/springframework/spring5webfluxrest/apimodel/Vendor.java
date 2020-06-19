package guru.springframework.spring5webfluxrest.apimodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Vendor {
    private String id;
    private String firstName;
    private String lastName;
}

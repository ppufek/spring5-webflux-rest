package guru.springframework.spring5webfluxrest.apimodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
@JsonInclude(NON_NULL) //ignore null values
public class Category {
    private String id;
    private String description;
}

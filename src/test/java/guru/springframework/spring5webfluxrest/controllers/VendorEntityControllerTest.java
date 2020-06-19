package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.CategoryEntity;
import guru.springframework.spring5webfluxrest.domain.VendorEntity;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = VendorController.class, properties = "logging.level.org.springframework=DEBUG")
public class VendorEntityControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    VendorRepository vendorRepository;

    @Test
    public void list() {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(VendorEntity.builder().firstName("Fred").lastName("Flintstone").build(),
                        VendorEntity.builder().firstName("Barney").lastName("Rubble").build()));

//        String responseBody = webTestClient.get()
//                .uri("/api/v1/vendors")
//                .exchange()
//                .expectBody(String.class)
//                .returnResult().getResponseBody();
//        System.out.println(responseBody);

        webTestClient.get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBody().json("[{\"firstName\":\"Fred\",\"lastName\":\"Flintstone\"},{\"firstName\":\"Barney\",\"lastName\":\"Rubble\"}]\n");

        //.expectBodyList(VendorEntity.class)
        //.hasSize(2)
    }

    @Test
    public void getById() {
        given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(VendorEntity.builder().firstName("Jimmy").lastName("Johns").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(VendorEntity.class);
    }

    @Test
    public void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(VendorEntity.builder().build()));

        Mono<VendorEntity> vendorToSaveMono = Mono.just(VendorEntity.builder().firstName("First Name")
                .lastName("Last Name").build());
        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, VendorEntity.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {
        when(vendorRepository.findById("test"))
                .thenReturn(Mono.just(VendorEntity.builder().firstName("Paula").lastName("Pufek").build()));
        given(vendorRepository.save(any(VendorEntity.class)))
                .willReturn(Mono.just(VendorEntity.builder().firstName("Paula").lastName("Pufekkkk").build()));

        webTestClient.put()
                .uri("/api/v1/vendors/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"firstName\":\"Paulllla\", \"lastName\": \"Pufek\"}")//??still passing
                .exchange()
                .expectStatus()
                .isOk().expectBody().json("{\"firstName\":\"Paula\", \"lastName\": \"Pufekkkk\"}");
    }

    @Test
    public void testPatchWithChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(VendorEntity.builder().firstName("Jimmy").build()));
        given(vendorRepository.save(any(VendorEntity.class)))
                .willReturn(Mono.just(VendorEntity.builder().build()));
        Mono<VendorEntity> vendorMonoToUpdate = Mono.just(VendorEntity.builder().firstName("Jim").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, VendorEntity.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchNoChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(VendorEntity.builder().firstName("Jimmy").build()));
        given(vendorRepository.save(any(VendorEntity.class)))
                .willReturn(Mono.just(VendorEntity.builder().build()));
        Mono<VendorEntity> vendorMonoToUpdate = Mono.just(VendorEntity.builder().firstName("Jimmy").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, VendorEntity.class)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody().json("{\"message\":\"Values did not change\"}");

        verify(vendorRepository, Mockito.times(0)).save(any());
    }

}
package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.apimodel.CategoryUpdate;
import guru.springframework.spring5webfluxrest.domain.CategoryEntity;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
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
@WebFluxTest(controllers = CategoryController.class)
public class CategoryEntityControllerTest {
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    CategoryRepository categoryRepository;

    @Test
    public void list() {
        //given
        when(categoryRepository.findAll())
                .thenReturn(Flux.just(CategoryEntity.builder().id("h").description("Cat1").build(),
                        CategoryEntity.builder().description("Cat2").build()));
        //when
        webTestClient.get()
                .uri("/api/v1/categories")
                .exchange()
                //then
                .expectBody().json("[{\"description\":\"Cat1\"},{\"description\":\"Cat2\"}]");
    }

    @Test
    public void getById() {
        //given
        when(categoryRepository.findById("someid"))
                .thenReturn(Mono.just(CategoryEntity.builder().description("Cat").build()));

        webTestClient.get()
                .uri("/api/v1/categories/someid")
                .exchange()
                .expectBody(CategoryEntity.class);
    }

    @Test
    public void testCreateCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(CategoryEntity.builder().description("descrp").build()));

        webTestClient.post()
                .uri("/api/v1/categories")
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateCategory() {
        when(categoryRepository.findById("test"))
                .thenReturn(Mono.just(CategoryEntity.builder().description("Cat").build()));
        given(categoryRepository.save(any(CategoryEntity.class)))
                .willReturn(Mono.just(CategoryEntity.builder().description("New description").build()));

        webTestClient.put()
                .uri("/api/v1/categories/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"description\":\"New description\"}")
                .exchange()
                .expectStatus()
                .isOk().expectBody().json("{\"description\":\"New description\"}");

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchWithChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(CategoryEntity.builder().description("my description").build()));

        given(categoryRepository.save(any(CategoryEntity.class)))
                .willReturn(Mono.just(CategoryEntity.builder().build()));

        Mono<CategoryUpdate> categoryToUpdateMono = Mono.just(new CategoryUpdate("New Description"));

        webTestClient.patch()
                .uri("/api/v1/categories/aasdfgh")
                .body(categoryToUpdateMono, CategoryUpdate.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchNoChanges() {
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(CategoryEntity.builder().description("New description").build()));

        given(categoryRepository.save(any(CategoryEntity.class)))
                .willReturn(Mono.just(CategoryEntity.builder().build()));

        Mono<CategoryUpdate> categoryToUpdateMono = Mono.just(new CategoryUpdate("New description"));
        //expect 400
        webTestClient.patch()
                .uri("/api/v1/categories/aasdfgh")
                .body(categoryToUpdateMono, CategoryUpdate.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().json("{\"message\":\"Values did not change\"}");
    }

    @Test
    public void testUpdateCategoryWithBadObject() {
        webTestClient.put()
                .uri("/api/v1/categories/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"wrong_field\":\"Wrong value\"}")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

}
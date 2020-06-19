package guru.springframework.spring5webfluxrest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring5webfluxrest.apimodel.Category;
import guru.springframework.spring5webfluxrest.apimodel.CategoryUpdate;
import guru.springframework.spring5webfluxrest.domain.CategoryEntity;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private ObjectMapper objectMapper;

    public CategoryController(CategoryRepository categoryRepository, ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/v1/categories")
    Flux<Category> list() {
        return categoryRepository.findAll().map(categoryEntity -> entityToApi(categoryEntity));
    }

    @GetMapping("/api/v1/categories/{id}")
    Mono<Category> getById(@PathVariable String id) {
        return categoryRepository.findById(id).map(foundEntity -> entityToApi(foundEntity));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/categories")
    Mono<Void> create(@RequestBody Publisher<CategoryEntity> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("/api/v1/categories/{id}")
    Mono<Category> update(@PathVariable String id, @Validated @RequestBody CategoryUpdate categoryUpdate) {
        return categoryRepository.findById(id).flatMap(foundCategoryEntity -> {
            foundCategoryEntity.setDescription(categoryUpdate.getDescription());
            return categoryRepository.save(foundCategoryEntity).map(savedEntity -> entityToApi(savedEntity));
        });
    }

    @PatchMapping("/api/v1/categories/{id}")
    Mono<ResponseEntity<String>> patch(@PathVariable String id, @RequestBody CategoryUpdate categoryUpdate) {
        // flatMap() --> actions with DB
        // map() --> transformation of objects
        return categoryRepository.findById(id).flatMap(foundCategoryEntity -> {
                    if (!foundCategoryEntity.getDescription().equals(categoryUpdate.getDescription())) {
                        foundCategoryEntity.setDescription(categoryUpdate.getDescription());
                        return categoryRepository.save(foundCategoryEntity).map(savedEntity -> {
                            try {
                                return new ResponseEntity<>(objectMapper.writeValueAsString(entityToApi(savedEntity)), HttpStatus.OK);
                            } catch (JsonProcessingException e) {
                                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        });
                    }
                    return Mono.just(new ResponseEntity<>("{\"message\":\"Values did not change\"}", HttpStatus.BAD_REQUEST));
                    //return Mono.error(new Exception("Values didn't change"));
                }
        );
    }

    private Category entityToApi(CategoryEntity categoryEntity) {
        return new Category(categoryEntity.getId(), categoryEntity.getDescription());
    }
}

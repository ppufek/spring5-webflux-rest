package guru.springframework.spring5webfluxrest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring5webfluxrest.apimodel.Vendor;
import guru.springframework.spring5webfluxrest.apimodel.VendorUpdate;
import guru.springframework.spring5webfluxrest.domain.VendorEntity;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class VendorController {
    private final VendorRepository vendorRepository;

    private ObjectMapper objectMapper;

    public VendorController(VendorRepository vendorRepository, ObjectMapper objectMapper) {
        this.vendorRepository = vendorRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/v1/vendors")
    Flux<Vendor> list() {
        return vendorRepository.findAll().map(vendorEntity -> entityToApi(vendorEntity));
    }

    @GetMapping("/api/v1/vendors/{id}")
    Mono<Vendor> getById(@PathVariable String id) {
        return vendorRepository.findById(id).map(foundEntity -> entityToApi(foundEntity));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/vendors")
    Mono<Void> create(@RequestBody Publisher<VendorEntity> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then(); //then() -> returns back Mono<Void>
    }

    @PutMapping("/api/v1/vendors/{id}")
    Mono<Vendor> update(@PathVariable String id, @Validated @RequestBody VendorUpdate vendorUpdate) {
        return vendorRepository.findById(id).flatMap(foundVendorEntity -> {
            foundVendorEntity.setFirstName(vendorUpdate.getFirstName());
            foundVendorEntity.setLastName(vendorUpdate.getLastName());
            return vendorRepository.save(foundVendorEntity).map(savedEntity -> entityToApi(savedEntity));
        });
    }

    @PatchMapping("/api/v1/vendors/{id}")
    Mono<ResponseEntity<String>> patch(@PathVariable String id, @RequestBody VendorUpdate vendorUpdate) {
        return vendorRepository.findById(id).flatMap(foundVendorEntity -> {
            if (!(foundVendorEntity.getFirstName().equals(vendorUpdate.getFirstName()))) {
                foundVendorEntity.setFirstName(vendorUpdate.getFirstName());
                return vendorRepository.save(foundVendorEntity).map(savedVendorEntity -> {
                    try {
                        return new ResponseEntity<>(objectMapper.writeValueAsString(entityToApi(savedVendorEntity)), HttpStatus.OK);
                    } catch (JsonProcessingException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
            }
            return Mono.just(new ResponseEntity<>("{\"message\":\"Values did not change\"}", HttpStatus.BAD_REQUEST));
            }
        );
    }

    private Vendor entityToApi(VendorEntity vendorEntity) {
        return new Vendor(vendorEntity.getId(), vendorEntity.getFirstName(), vendorEntity.getLastName());
    }

}

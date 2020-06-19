package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.CategoryEntity;
import guru.springframework.spring5webfluxrest.domain.VendorEntity;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.count().block() == 0) {
            //load data
            System.out.println("#### Loading data on Bootstrap ####");

            categoryRepository.save(CategoryEntity.builder()
                    .description("Fruits").build()).block();
            categoryRepository.save(CategoryEntity.builder()
                    .description("Nuts").build()).block();
            categoryRepository.save(CategoryEntity.builder()
                    .description("Breads").build()).block();
            categoryRepository.save(CategoryEntity.builder()
                    .description("Eggs").build()).block();

            System.out.println("Loaded Categories: " + categoryRepository.count().block()); //BLOCK REQUIRED BC OF REACTIVE VALUES

            vendorRepository.save(VendorEntity.builder()
                    .firstName("Joe")
                    .lastName("Buck").build()).block();
            vendorRepository.save(VendorEntity.builder()
                    .firstName("Michael")
                    .lastName("Weston").build()).block();
            vendorRepository.save(VendorEntity.builder()
                    .firstName("Jessie")
                    .lastName("Waters").build()).block();
            vendorRepository.save(VendorEntity.builder()
                    .firstName("Bill")
                    .lastName("Nershi").build()).block();

            System.out.println("Loaded Vendors: " + vendorRepository.count().block());
        }

    }
}

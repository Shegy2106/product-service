package com.springmicroservices.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springmicroservices.productservice.dto.ProductRequest;
import com.springmicroservices.productservice.model.Product;
import com.springmicroservices.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();

        String productRequestString = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        // Assuming you have some initial products in your repository
        List<Product> initialProducts = Arrays.asList(
                Product.builder()
                        .name("Product 1")
                        .description("Product 1")
                        .price(BigDecimal.valueOf(10.0))
                        .build(),
                Product.builder()
                        .name("Product 2")
                        .description("Product 2")
                        .price(BigDecimal.valueOf(20.0))
                        .build(),
                Product.builder()
                        .name("Product 3")
                        .description("Product 3")
                        .price(BigDecimal.valueOf(30.0))
                        .build()
        );

        for (Product initialProduct : initialProducts) {
            String productRequestString = objectMapper.writeValueAsString(initialProduct);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productRequestString))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(initialProducts.size()));
    }

    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1200))
                .build();
    }


}

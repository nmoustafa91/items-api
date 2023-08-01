package com.items.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.items.ItemsApplication;
import com.items.db.repository.ItemRepository;
import com.items.integration.helper.ItemsHelper;
import com.items.mapper.ItemMapper;
import com.items.service.ItemService;

@SpringBootTest(classes = ItemsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
public class AbstractIT {

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected ItemMapper itemMapper;
    @Autowired
    protected ItemService itemService;

    protected ItemsHelper itemsHelper;

    @BeforeEach
    protected void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        itemsHelper = new ItemsHelper(restTemplate);
    }

}

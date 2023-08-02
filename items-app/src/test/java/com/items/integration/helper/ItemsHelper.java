package com.items.integration.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.items.model.ApiErrorResponseDTO;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;

public class ItemsHelper extends AbstractRestCallHelper {

  private static final String ITEMS_URL = "/items";
  private static final String ITEM_URL = "/items/{itemId}";
  private static final String ITEM_STATUS_URL = "/items/{itemId}/status";
  private static final String ITEM_DESCRIPTION_URL = "/items/{itemId}/description";

  public ItemsHelper(TestRestTemplate testRestTemplate) {
    super(testRestTemplate);
  }

  public ResponseEntity<ItemDTO> create(CreateItemRequestDTO request) {
    return testRestTemplate.postForEntity(ITEMS_URL, request, ItemDTO.class);
  }

  public ResponseEntity<ItemDTO> getItem(UUID itemId) {
    return testRestTemplate.getForEntity(ITEM_URL, ItemDTO.class, itemId);
  }

  public void getItem(UUID itemId, HttpStatus expectedStatus) {
    var response = testRestTemplate.getForEntity(ITEM_URL, Object.class, itemId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ItemDTO> update(UpdateItemRequestDTO request, UUID itemId) {
    String version = getItem(itemId).getHeaders().getETag();
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", version);
    return testRestTemplate.exchange(ITEM_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), ItemDTO.class, itemId);
  }

  public void update(UpdateItemRequestDTO request, UUID itemId, HttpStatus expectedStatus) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", "0");
    var response = testRestTemplate.exchange(ITEM_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), Object.class, itemId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ItemDTO> updateStatus(UpdateItemStatusRequestDTO request, UUID itemId) {
    String version = getItem(itemId).getHeaders().getETag();
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", version);
    return testRestTemplate.exchange(ITEM_STATUS_URL, HttpMethod.PATCH, new HttpEntity<>(request, headers), ItemDTO.class, itemId);
  }

  public void updateStatus(UpdateItemStatusRequestDTO request, UUID itemId, HttpStatus expectedStatus) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", "0");
    var response = testRestTemplate.exchange(ITEM_STATUS_URL, HttpMethod.PATCH, new HttpEntity<>(request, headers), Object.class, itemId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ItemDTO> updateDescription(UpdateItemDescriptionRequestDTO request, UUID itemId) {
    String version = getItem(itemId).getHeaders().getETag();
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", version);
    return testRestTemplate.exchange(ITEM_DESCRIPTION_URL, HttpMethod.PATCH, new HttpEntity<>(request, headers), ItemDTO.class, itemId);
  }

  public void updateDescription(UpdateItemDescriptionRequestDTO request, UUID itemId, HttpStatus expectedStatus) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", "0");
    var response = testRestTemplate.exchange(ITEM_DESCRIPTION_URL, HttpMethod.PATCH, new HttpEntity<>(request, headers), Object.class, itemId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ListItemsResponseDTO> getItems(String name, List<String> itemIds, List<String> itemStatuses, String completionDateFrom,
      String completionDateTo, String search, String notDone, Pageable pageRequest) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(ITEMS_URL + "?");
    addQueryParamIfNotNull(requestParameters, urlBuilder, NAME, name);
    addQueryParamIfNotNull(requestParameters, urlBuilder, ITEM_IDS, itemIds);
    addQueryParamIfNotNull(requestParameters, urlBuilder, ITEM_STATUSES, itemStatuses);
    addQueryParamIfNotNull(requestParameters, urlBuilder, COMPLETION_DATE_FROM, completionDateFrom);
    addQueryParamIfNotNull(requestParameters, urlBuilder, COMPLETION_DATE_TO, completionDateTo);
    addQueryParamIfNotNull(requestParameters, urlBuilder, SEARCH, search);
    addQueryParamIfNotNull(requestParameters, urlBuilder, NOT_DONE, notDone);
    if (pageRequest != null) {
      urlBuilder.append(PAGE_NUMBER + "={" + PAGE_NUMBER + "}&");
      urlBuilder.append(PAGE_SIZE + "={" + PAGE_SIZE + "}&");
      requestParameters.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
      requestParameters.put(PAGE_SIZE, String.valueOf(pageRequest.getPageSize()));
    }
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, ListItemsResponseDTO.class, requestParameters);
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, List<String> paramValues) {
    if (!CollectionUtils.isEmpty(paramValues)) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, String.join(",", paramValues));
    }
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, String paramValue) {
    if (paramValue != null) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, paramValue);
    }
  }

  public void  deleteItem(UUID itemId) {
    testRestTemplate.delete(ITEM_URL, itemId);
  }
}

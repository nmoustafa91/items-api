package com.items.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;

public class ItemsApiControllerIT extends AbstractIT {

	private static final String ITEM_1 = "ITEM1";
	private static final String ITEM_2 = "ITEM 2";
	private static final String ITEM_3 = "ITEM 3";
	private static final String ITEM_4 = "NAME 4";

	@BeforeEach
	void init() {
		itemRepository.deleteAll();
	}

	@Test
	void createItem() {
		var request = new CreateItemRequestDTO().name(ITEM_1).description("desc");
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		assertEquals(ITEM_1, itemDTO.getName());
		assertEquals(ItemStatusDTO.NOTDONE, itemDTO.getStatus());
		assertEquals(request.getDescription(), itemDTO.getDescription());
		assertNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getId());
		assertNotNull(itemDTO.getCreated());
		assertNotNull(itemDTO.getCreatedBy());
	}

	@Test
	void updateItem_notFound() {
		var request = new UpdateItemRequestDTO().name(ITEM_1);
		itemsHelper.update(request, UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void updateItem() {
		var request = new CreateItemRequestDTO().name(ITEM_1);
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		var updateRequest = new UpdateItemRequestDTO().name("updated").description("desc");
		response = itemsHelper.update(updateRequest, itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		itemDTO = response.getBody();
		assertEquals(updateRequest.getName(), itemDTO.getName());
		assertEquals(ItemStatusDTO.NOTDONE, itemDTO.getStatus());
		assertEquals(updateRequest.getDescription(), itemDTO.getDescription());
		assertNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getId());
		assertNotNull(itemDTO.getCreated());
		assertNotNull(itemDTO.getCreatedBy());
		assertNotNull(itemDTO.getLastModified());
		assertNotNull(itemDTO.getLastModifiedBy());
	}

	@Test
	void updateItemStatus_itemNotFound() {
		var request = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE);
		itemsHelper.updateStatus(request, UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void updateItem_toDone() {
		var request = new CreateItemRequestDTO().name(ITEM_1);
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		assertEquals(ItemStatusDTO.NOTDONE, itemDTO.getStatus());
		assertNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getId());
		assertNotNull(itemDTO.getCreated());
		assertNotNull(itemDTO.getCreatedBy());
		var updateRequest = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE);
		response = itemsHelper.updateStatus(updateRequest, itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		itemDTO = response.getBody();
		assertEquals(ItemStatusDTO.DONE, itemDTO.getStatus());
		assertNotNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getLastModified());
		assertNotNull(itemDTO.getLastModifiedBy());
	}

	@Test
	void updateItem_toNotDone() {
		var request = new CreateItemRequestDTO().name(ITEM_1);
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		assertEquals(ItemStatusDTO.NOTDONE, itemDTO.getStatus());
		assertNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getId());
		assertNotNull(itemDTO.getCreated());
		assertNotNull(itemDTO.getCreatedBy());
		var updateRequest = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE);
		response = itemsHelper.updateStatus(updateRequest, itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		itemDTO = response.getBody();
		assertEquals(ItemStatusDTO.DONE, itemDTO.getStatus());
		assertNotNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getLastModified());
		assertNotNull(itemDTO.getLastModifiedBy());
		updateRequest = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.NOTDONE);
		response = itemsHelper.updateStatus(updateRequest, itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		itemDTO = response.getBody();
		assertEquals(ItemStatusDTO.NOTDONE, itemDTO.getStatus());
		assertNull(itemDTO.getCompletionDate());
		assertNotNull(itemDTO.getLastModified());
		assertNotNull(itemDTO.getLastModifiedBy());
	}

	@Test
	void updateItemDescription_itemNotFound() {
		var request = new UpdateItemDescriptionRequestDTO().description("updated");
		itemsHelper.updateDescription(request, UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void updateItemDescription() {
		var request = new CreateItemRequestDTO().name(ITEM_1);
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		assertNull(itemDTO.getDescription());
		assertNotNull(itemDTO.getId());
		assertNotNull(itemDTO.getCreated());
		assertNotNull(itemDTO.getCreatedBy());
		var updateRequest = new UpdateItemDescriptionRequestDTO().description("desc");
		response = itemsHelper.updateDescription(updateRequest, itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		itemDTO = response.getBody();
		assertNotNull(itemDTO.getDescription());
		assertEquals(updateRequest.getDescription(), itemDTO.getDescription());
		assertNotNull(itemDTO.getLastModified());
		assertNotNull(itemDTO.getLastModifiedBy());
	}

	@Test
	void getItem_NotFound() {
		itemsHelper.getItem(UUID.randomUUID(), HttpStatus.NOT_FOUND);
	}

	@Test
	void getItem() {
		var request = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		response = itemsHelper.getItem(itemDTO.getId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var getItemResponse = response.getBody();
		assertEquals(itemDTO.getId(), getItemResponse.getId());
		assertEquals(itemDTO.getName(), getItemResponse.getName());
		assertEquals(itemDTO.getLastModified(), getItemResponse.getLastModified());
		assertEquals(itemDTO.getCreatedBy(), getItemResponse.getCreatedBy());
		assertEquals(itemDTO.getLastModifiedBy(), getItemResponse.getLastModifiedBy());
		assertEquals(itemDTO.getDescription(), getItemResponse.getDescription());
		assertEquals(itemDTO.getStatus(), getItemResponse.getStatus());
		assertEquals(itemDTO.getCompletionDate(), getItemResponse.getCompletionDate());
	}

	@Test
	void deleteItem() {
		var request = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var response = itemsHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var itemDTO = response.getBody();
		itemsHelper.deleteItem(itemDTO.getId());
		assertFalse(itemRepository.existsById(itemDTO.getId()));
	}

	@Test
	void listItems_Unfiltered() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest2 = new CreateItemRequestDTO().name(ITEM_2).description("desc2");
		var createRequest3 = new CreateItemRequestDTO().name(ITEM_3).description("desc3");
		var createRequest4 = new CreateItemRequestDTO().name(ITEM_4).description("desc4");

		itemsHelper.create(createRequest1);
		itemsHelper.create(createRequest2);
		itemsHelper.create(createRequest3);
		itemsHelper.create(createRequest4);

		var response = itemsHelper.getItems(null, null, null, null, null, null, null, null);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());
	}

	@Test
	void listItems_filteredByName() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest2 = new CreateItemRequestDTO().name(ITEM_2).description("desc2");
		var createRequest3 = new CreateItemRequestDTO().name(ITEM_3).description("desc3");
		var createRequest4 = new CreateItemRequestDTO().name(ITEM_4).description("desc4");

		itemsHelper.create(createRequest1);
		itemsHelper.create(createRequest2);
		itemsHelper.create(createRequest3);
		itemsHelper.create(createRequest4);

		var response = itemsHelper.getItems("NAME", null, null, null, null, null, String.valueOf(Boolean.FALSE),
				PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
		response = itemsHelper.getItems("ITEM", null, null, null, null, null, String.valueOf(Boolean.FALSE),
				PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(3, response.getBody().getResults().size());
		response = itemsHelper.getItems("ITEM4", null, null, null, null, null, String.valueOf(Boolean.FALSE),
				PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void listItems_filterNotDoneItems() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest2 = new CreateItemRequestDTO().name(ITEM_2).description("desc2");
		var createRequest3 = new CreateItemRequestDTO().name(ITEM_3).description("desc3");
		var createRequest4 = new CreateItemRequestDTO().name(ITEM_4).description("desc4");

		var item1 = itemsHelper.create(createRequest1).getBody();
		var item2 = itemsHelper.create(createRequest2).getBody();
		var item3 = itemsHelper.create(createRequest3).getBody();
		var item4 = itemsHelper.create(createRequest4).getBody();

		var response = itemsHelper.getItems(null, null, List.of(ItemStatusDTO.DONE.getValue()), null, null, null,
				String.valueOf(Boolean.FALSE), PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());

		itemsHelper.updateStatus(new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE), item2.getId());
		itemsHelper.updateStatus(new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE), item4.getId());
		response = itemsHelper.getItems(null, null, null, null, null, null, String.valueOf(Boolean.TRUE),
				PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());
		List<UUID> itemsIds = response.getBody().getResults().stream().map((ItemDTO::getId)).toList();
		assertThat(itemsIds, not(containsInAnyOrder(item2.getId(), item4.getId())));
	}

	@Test
	void listItems_filterByCompletionDate() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest3 = new CreateItemRequestDTO().name(ITEM_3).description("desc3");

		var item1 = itemsHelper.create(createRequest1).getBody();
		var item3 = itemsHelper.create(createRequest3).getBody();

		var response = itemsHelper.getItems(null, null, null, LocalDate.now().minusWeeks(1L).toString(),
				LocalDate.now().minusDays(1L).toString(), null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());

		itemsHelper.updateStatus(new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE), item1.getId());
		itemsHelper.updateStatus(new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE), item3.getId());

		response = itemsHelper.getItems(null, null, null, LocalDate.now().minusDays(1L).toString(),
				LocalDate.now().plusDays(1L).toString(), null, null, PageRequest.of(0, 10));
		List<UUID> itemsIds = response.getBody().getResults().stream().map((ItemDTO::getId)).toList();
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());
		assertThat(itemsIds, containsInAnyOrder(item1.getId(), item3.getId()));
	}

	@Test
	void listItems_filterByItemIds() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest4 = new CreateItemRequestDTO().name(ITEM_4).description("desc4");

		var item1 = itemsHelper.create(createRequest1).getBody();
		var item4 = itemsHelper.create(createRequest4).getBody();

		var response = itemsHelper.getItems(null, List.of(item1.getId().toString(), item4.getId().toString()), null,
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());
	}

	@Test
	void listItems_filterBySearch() {
		var createRequest1 = new CreateItemRequestDTO().name(ITEM_1).description("desc1");
		var createRequest2 = new CreateItemRequestDTO().name(ITEM_2).description("desc2");
		var createRequest3 = new CreateItemRequestDTO().name(ITEM_3).description("desc3");
		var createRequest4 = new CreateItemRequestDTO().name(ITEM_4).description("desc4");

		itemsHelper.create(createRequest1).getBody();
		itemsHelper.create(createRequest2).getBody();
		itemsHelper.create(createRequest3).getBody();
		var item4 = itemsHelper.create(createRequest4).getBody();

		var response = itemsHelper.getItems(null, null, null, null, null, item4.getId().toString(), null,
				PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
		response = itemsHelper.getItems(null, null, null, null, null, "ITEM", null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(3, response.getBody().getResults().size());
	}
}

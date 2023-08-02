package com.items.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.items.db.model.ItemEntity;
import com.items.db.model.ItemStatusEnum;
import com.items.db.model.VersionModel;
import com.items.exception.ItemNotFoundException;
import com.items.integration.AbstractIT;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;

public class ItemsServiceIT extends AbstractIT {

	private static final ItemEntity ITEM_ENTITY = ItemEntity.builder().name("name").description("desc").build();

	@Autowired
	private ItemService itemService;

	@BeforeEach
	void cleanUp() {
		itemRepository.deleteAll();
	}

	@Test
	void createItemTest() {
		CreateItemRequestDTO itemRequest = new CreateItemRequestDTO().name("name").description("desc");
		ItemDTO created = itemService.createItem(itemRequest);
		assertEquals(ItemStatusDTO.NOTDONE, created.getStatus());
		assertNull(created.getCompletionDate());
		assertTrue(itemRepository.existsById(created.getId()));
		ItemEntity itemEntity = itemRepository.findById(created.getId()).get();
		assertNotNull(itemEntity.getVersion());
		assertEquals(created.getId(), itemEntity.getId());
		assertEquals(created.getName(), itemEntity.getName());
		assertEquals(created.getDescription(), itemEntity.getDescription());
		assertEquals(created.getCompletionDate(), itemEntity.getCompletionDate());
		assertEquals(created.getStatus().getValue(), itemEntity.getStatus().getValue());
		assertEquals(created.getCreatedBy(), itemEntity.getCreatedBy());
		assertEquals(created.getLastModifiedBy(), itemEntity.getUpdatedBy());
	}

	@Test
	void createItemTest_shouldNotThrowException() {
		CreateItemRequestDTO itemRequest = new CreateItemRequestDTO().name("name").description("desc");
		assertDoesNotThrow(() -> itemService.createItem(itemRequest));
	}

	@Test
	void getItemTest() {
		ItemEntity created = itemRepository.save(ITEM_ENTITY);
		VersionModel<ItemDTO> retrievedVersionModel = itemService.getItem(created.getId());
		assertNotNull(retrievedVersionModel.getVersion());
		assertEquals(0, retrievedVersionModel.getVersion());
		ItemDTO retrieved = retrievedVersionModel.getBody();
		assertEquals(ItemStatusDTO.NOTDONE, retrieved.getStatus());
		assertNull(retrieved.getCompletionDate());
		assertEquals(created.getId(), retrieved.getId());
		assertEquals(created.getName(), retrieved.getName());
		assertEquals(created.getDescription(), retrieved.getDescription());
		assertEquals(created.getCompletionDate(), retrieved.getCompletionDate());
		assertEquals(created.getStatus().getValue(), retrieved.getStatus().getValue());
		assertEquals(created.getCreatedBy(), retrieved.getCreatedBy());
		assertEquals(created.getUpdatedBy(), retrieved.getLastModifiedBy());
	}

	@Test
	void getItemTest_itemNotFound() {
		assertThrows(ItemNotFoundException.class, () -> itemService.getItem(UUID.randomUUID()));
	}

	@Test
	void updateItemTest() {
		ItemEntity created = itemRepository.save(ITEM_ENTITY);

		assertEquals(0, created.getVersion());

		VersionModel<ItemDTO> retrievedVersionModel = itemService.getItem(created.getId());
		final UpdateItemRequestDTO itemRequestDTO = new UpdateItemRequestDTO().name("updated").description("updated");
		itemService.updateItem(itemRequestDTO, created.getId(), getEtag(retrievedVersionModel));

		retrievedVersionModel = itemService.getItem(created.getId());
		ItemDTO retrieved = retrievedVersionModel.getBody();

		assertEquals(ItemStatusDTO.NOTDONE, retrieved.getStatus());
		assertNull(retrieved.getCompletionDate());
		assertEquals(created.getId(), retrieved.getId());
		assertEquals(itemRequestDTO.getName(), retrieved.getName());
		assertEquals(itemRequestDTO.getDescription(), retrieved.getDescription());
		assertEquals(created.getCompletionDate(), retrieved.getCompletionDate());
		assertEquals(created.getStatus().getValue(), retrieved.getStatus().getValue());
		assertNotEquals(created.getVersion(), retrievedVersionModel.getVersion());
	}

	@Test
	void updateItemTest_itemNotFound() {
		assertThrows(ItemNotFoundException.class,
				() -> itemService.updateItem(new UpdateItemRequestDTO().name("updated"), UUID.randomUUID(), "0"));
	}

	@Test
	void updateItemDescriptionTest() {
		ItemEntity created = itemRepository.save(ITEM_ENTITY);

		VersionModel<ItemDTO> retrievedVersionModel = itemService.getItem(created.getId());
		final UpdateItemDescriptionRequestDTO itemRequestDTO = new UpdateItemDescriptionRequestDTO()
				.description("updatedDesc");
		itemService.updateItemDescription(itemRequestDTO, created.getId(), getEtag(retrievedVersionModel));

		retrievedVersionModel = itemService.getItem(created.getId());
		ItemDTO retrieved = retrievedVersionModel.getBody();

		assertEquals(ItemStatusDTO.NOTDONE, retrieved.getStatus());
		assertNull(retrieved.getCompletionDate());
		assertEquals(itemRequestDTO.getDescription(), retrieved.getDescription());
		assertEquals(created.getCompletionDate(), retrieved.getCompletionDate());
		assertEquals(created.getStatus().getValue(), retrieved.getStatus().getValue());
		assertNotEquals(created.getVersion(), retrievedVersionModel.getVersion());
	}

	@Test
	void updateItemDescriptionTest_itemNotFound() {
		assertThrows(ItemNotFoundException.class,
				() -> itemService.updateItemDescription(new UpdateItemDescriptionRequestDTO().description("desc"),
						UUID.randomUUID(), "0"));
	}

	@Test
	void updateItemStatusTest() {
		ItemEntity created = itemRepository.save(ITEM_ENTITY);

		VersionModel<ItemDTO> retrievedVersionModel = itemService.getItem(created.getId());
		final UpdateItemStatusRequestDTO itemRequestDTO = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE);
		itemService.updateItemStatus(itemRequestDTO, created.getId(), getEtag(retrievedVersionModel));

		retrievedVersionModel = itemService.getItem(created.getId());
		ItemDTO retrieved = retrievedVersionModel.getBody();

		assertEquals(ItemStatusDTO.DONE, retrieved.getStatus());
		assertNotNull(retrieved.getCompletionDate());
		assertNotEquals(created.getVersion(), retrievedVersionModel.getVersion());
	}

	@Test
	void updateItemStatusTest_itemNotFound() {
		assertThrows(ItemNotFoundException.class, () -> itemService
				.updateItemStatus(new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE), UUID.randomUUID(), "0"));
	}

	@Test
	void getItems_filterByName() {
		prepareDB();
		ListItemsResponseDTO itemsResponseDTO = itemService.getItems("name", null, null, null, null, null, null,
				PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(4, itemsResponseDTO.getResults().size());
		itemsResponseDTO = itemService.getItems("test", null, null, null, null, null, null, PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(1, itemsResponseDTO.getResults().size());
		itemsResponseDTO = itemService.getItems("item", null, null, null, null, null, null, PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(0, itemsResponseDTO.getResults().size());
	}

	@Test
	void getItems_filterByNotDone() {
		prepareDB();
		// all the list should be returned
		ListItemsResponseDTO itemsResponseDTO = itemService.getItems(null, null, null, null, null, null, Boolean.FALSE,
				PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(5, itemsResponseDTO.getResults().size());
		itemsResponseDTO = itemService.getItems(null, null, null, null, null, null, Boolean.TRUE,
				PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(3, itemsResponseDTO.getResults().size());
	}

	@Test
	void getItems_filterByStatusAndCompletionDate() {
		prepareDB();
		// all the list should be returned
		ListItemsResponseDTO itemsResponseDTO = itemService.getItems(null, null, List.of(ItemStatusDTO.DONE),
				LocalDate.now(), LocalDate.now().plusDays(2), null, null, PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(1, itemsResponseDTO.getResults().size());

		itemsResponseDTO = itemService.getItems(null, null, List.of(ItemStatusDTO.DONE), LocalDate.now().minusWeeks(1),
				LocalDate.now().plusDays(2), null, null, PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(2, itemsResponseDTO.getResults().size());

		itemsResponseDTO = itemService.getItems(null, null, List.of(ItemStatusDTO.DONE), LocalDate.now().minusWeeks(5),
				LocalDate.now().minusWeeks(4), null, null, PageRequest.of(0, 20));
		assertNotNull(itemsResponseDTO);
		assertEquals(0, itemsResponseDTO.getResults().size());
	}

	@Test
	void deleteItemTest() {
		ItemEntity created = itemRepository.save(ITEM_ENTITY);
		assertDoesNotThrow(() -> itemService.deleteItem(created.getId()));
	}

	@Test
	void deleteItemTest_itemNotFound() {
		assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(UUID.randomUUID()));
	}

	private void prepareDB() {
		itemRepository.save(ItemEntity.builder().name("name1").description("desc1")
				.status(ItemStatusEnum.DONE).completionDate(OffsetDateTime.now().minusDays(1L)).build());
		itemRepository.save(ItemEntity.builder().name("name2").description("desc2").status(ItemStatusEnum.DONE)
				.completionDate(OffsetDateTime.now()).build());
		itemRepository
				.save(ItemEntity.builder().name("name3").description("des3").status(ItemStatusEnum.NOTDONE).build());
		itemRepository
				.save(ItemEntity.builder().name("name1").description("desc").status(ItemStatusEnum.NOTDONE).build());
		itemRepository.save(ItemEntity.builder().name("test").status(ItemStatusEnum.NOTDONE).build());

	}

	private String getEtag(VersionModel<ItemDTO> retrievedVersionModel) {
		return "\"" + retrievedVersionModel.getVersion() + "\"";
	}
}

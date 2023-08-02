package com.items.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.items.api.ItemsApi;
import com.items.db.model.VersionModel;
import com.items.etag.ItemETagResponseEntity;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;
import com.items.service.ItemService;

import lombok.RequiredArgsConstructor;

/**
 * This is the controller layer to handle the client request for the items api.
 */
@RestController
@RequiredArgsConstructor
public class ItemsController implements ItemsApi {

	private final ItemService itemService;

	/**
	 * Retrieves an item by its id.
	 *
	 * @param itemId (required)
	 * @return
	 */
	@Override
	public ResponseEntity<ItemDTO> getItem(UUID itemId) {
		VersionModel<ItemDTO> itemDTOVersionModel = itemService.getItem(itemId);

		return new ItemETagResponseEntity<>(itemDTOVersionModel).ok();
	}

	/**
	 * It is a general filtering endpoint. The two requests which list the items
	 * should be handled through it. Not done flag manages this.
	 *
	 * @param name               Item name query parameter (optional)
	 * @param itemIds            Items ids query parameter, can be comma-separated
	 *                           list to include multiple values (optional)
	 * @param itemStatuses       Items statuses query parameter, can be
	 *                           comma-separated list to include multiple values
	 *                           (optional)
	 * @param completionDateFrom (optional)
	 * @param completionDateTo   (optional)
	 * @param search             Provides full text search on Items. Searches in
	 *                           following parameters: * id * name (optional)
	 * @param notDone            Filter by not done status. (optional)
	 * @param pageNumber         Page number, default is 0 (optional, default to 0)
	 * @param pageSize           Number of items in a page, default page size is 20,
	 *                           maximum 50 (optional, default to 20)
	 * @param sort               Sort criteria, format:
	 *                           &#39;?sort&#x3D;&amp;lt;propertyA&amp;gt;[,&amp;lt;propertyB&amp;gt;][,(asc|desc)]&#39;,
	 *                           sort parameter can be used several times in one
	 *                           query (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ListItemsResponseDTO> getItems(String name, List<UUID> itemIds,
			List<ItemStatusDTO> itemStatuses, LocalDate completionDateFrom, LocalDate completionDateTo, String search,
			Boolean notDone, Integer pageNumber, Integer pageSize, String sort) {
		ListItemsResponseDTO listItemsResponseDTO = itemService.getItems(name, itemIds, itemStatuses,
				completionDateFrom, completionDateTo, search, notDone,
				PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listItemsResponseDTO);
	}

	@Override
	public ResponseEntity<ListItemsResponseDTO> getAllItems(Boolean notDone, Integer pageNumber, Integer pageSize, String sort) {
		ListItemsResponseDTO listItemsResponseDTO = itemService.getAllItems(notDone,
				PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listItemsResponseDTO);
	}

	/**
	 * An endpoint for item creation.
	 *
	 * @param createItemRequestDTO (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ItemDTO> createItem(CreateItemRequestDTO createItemRequestDTO) {
		ItemDTO itemDTO = itemService.createItem(createItemRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(itemDTO);
	}

	/**
	 * A PUT Endpoint for item entity update
	 *
	 * @param itemId               (required)
	 * @param ifMatch              ETag header value from getItem call (required)
	 * @param updateItemRequestDTO (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ItemDTO> updateItem(UUID itemId, String ifMatch, UpdateItemRequestDTO updateItemRequestDTO) {
		ItemDTO itemDTO = itemService.updateItem(updateItemRequestDTO, itemId, ifMatch);

		return ResponseEntity.ok(itemDTO);
	}

	/**
	 * A PATCH Endpoint which update the status and the completion date according as
	 * mentioned in the task.
	 *
	 * @param itemId                     (required)
	 * @param ifMatch                    ETag header value from getItem call
	 *                                   (required)
	 * @param updateItemStatusRequestDTO (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ItemDTO> updateItemStatus(UUID itemId, String ifMatch,
			UpdateItemStatusRequestDTO updateItemStatusRequestDTO) {
		ItemDTO itemDTO = itemService.updateItemStatus(updateItemStatusRequestDTO, itemId, ifMatch);

		return ResponseEntity.ok(itemDTO);
	}

	/**
	 * Update the item description so it handles a partial update (PATCH)
	 *
	 * @param itemId                          (required)
	 * @param ifMatch                         ETag header value from getItem call
	 *                                        (required)
	 * @param updateItemDescriptionRequestDTO (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ItemDTO> updateItemDescription(UUID itemId, String ifMatch,
			UpdateItemDescriptionRequestDTO updateItemDescriptionRequestDTO) {
		ItemDTO itemDTO = itemService.updateItemDescription(updateItemDescriptionRequestDTO, itemId, ifMatch);

		return ResponseEntity.ok(itemDTO);
	}

	/**
	 * An endpoint for item deletion.
	 *
	 * @param itemId (required)
	 * @return
	 */
	@Override
	public ResponseEntity<Void> deleteItem(UUID itemId) {
		itemService.deleteItem(itemId);

		return ResponseEntity.noContent().build();
	}

}
package com.items.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.items.db.model.VersionModel;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;

public interface ItemService {

  VersionModel<ItemDTO> getItem(UUID itemId);

  ListItemsResponseDTO getItems(String name, List<UUID> itemIds, List<ItemStatusDTO> itemStatuses, LocalDate completionDateFrom,
      LocalDate completionDateTo, String search, Boolean notDone, Pageable pageRequest);

  ItemDTO createItem(CreateItemRequestDTO createItemRequestDTO);

  ItemDTO updateItem(UpdateItemRequestDTO itemRequestDTO, UUID itemId, String ifMatch);

  ItemDTO updateItemStatus(UpdateItemStatusRequestDTO updateItemStatusRequestDTO, UUID itemId, String ifMatch);

  ItemDTO updateItemDescription(UpdateItemDescriptionRequestDTO updateItemDescriptionRequestDTO, UUID itemId, String ifMatch);

  void deleteItem(UUID itemId);

  ListItemsResponseDTO getAllItems(Boolean notDone, PageRequest of);
}

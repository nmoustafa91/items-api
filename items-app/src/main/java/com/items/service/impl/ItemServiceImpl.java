package com.items.service.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.items.config.properties.ItemsApiProperties;
import com.items.db.model.ItemEntity;
import com.items.db.model.ItemStatusEnum;
import com.items.db.model.VersionModel;
import com.items.db.repository.ItemRepository;
import com.items.db.repository.ItemSpecificationHelper;
import com.items.domain.ItemFilter;
import com.items.etag.utils.ETagUtils;
import com.items.exception.ItemNotFoundException;
import com.items.exception.general.ApplicationError;
import com.items.exception.general.ErrorCode;
import com.items.mapper.ItemMapper;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;
import com.items.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;
	private final ItemMapper itemMapper;

	private final ItemsApiProperties itemsApiProperties;
	private final Clock clock;

	@Override
	public VersionModel<ItemDTO> getItem(UUID itemId) {
		ItemEntity itemEntity = getItemById(itemId);

		return itemMapper.entityToVersionModel(itemEntity);
	}

	@Cacheable("items")
	@Override
	public ListItemsResponseDTO getItems(String name, List<UUID> itemIds, List<ItemStatusDTO> itemStatuses,
			LocalDate completionDateFrom, LocalDate completionDateTo, String search, Boolean notDone,
			Pageable pageRequest) {
		final ItemFilter filter = ItemFilter.builder().name(name).itemsIds(itemIds)
				.itemStatuses(itemMapper.fromStatusDTO(itemStatuses)).search(search).notDone(notDone)
				.completionFrom(completionDateFrom).completionTo(completionDateTo).build();
		
		Page<ItemEntity> page = itemRepository.findAll(ItemSpecificationHelper.createFilter(filter,
				itemsApiProperties.getDefaultZoneId().getRules().getOffset(clock.instant())), pageRequest);
		return itemMapper.pageToItemsResponseDTO(page);
	}

	/*
	 * @CacheEvict(value = "items", allEntries = true)
	 * 
	 * @Scheduled(fixedRateString = "${caching.spring.itemsCacheTTL}") public void
	 * emptyItemsCache() { log.info("emptying items cache"); }
	 */

	@Override
	public ItemDTO createItem(CreateItemRequestDTO createItemRequestDTO) {
		ItemEntity itemEntity = itemMapper.fromCreateBodyToEntity(createItemRequestDTO);

		return itemMapper.fromEntity(itemRepository.save(itemEntity));
	}

	@Override
	public ItemDTO updateItem(UpdateItemRequestDTO itemRequestDTO, UUID itemId, String ifMatch) {
		ItemEntity itemEntity = getItemById(itemId);

		ETagUtils.checkETag(itemEntity, ifMatch);

		itemEntity = itemMapper.updateEntityFromModel(itemRequestDTO, itemEntity);

		return itemMapper.fromEntity(itemRepository.save(itemEntity));
	}

	@Override
	public ItemDTO updateItemStatus(UpdateItemStatusRequestDTO updateItemStatusRequestDTO, UUID itemId,
			String ifMatch) {
		ItemEntity itemEntity = getItemById(itemId);

		ETagUtils.checkETag(itemEntity, ifMatch);

		final ItemStatusEnum itemStatus = itemMapper.fromStatusDTO(updateItemStatusRequestDTO.getStatus());
		itemEntity.setStatus(itemStatus);
		if (itemStatus == ItemStatusEnum.DONE) {
			itemEntity.setCompletionDate(OffsetDateTime.now());
		} else {
			itemEntity.setCompletionDate(null);
		}

		return itemMapper.fromEntity(itemRepository.save(itemEntity));
	}

	@Override
	public ItemDTO updateItemDescription(UpdateItemDescriptionRequestDTO updateItemDescriptionRequestDTO, UUID itemId,
			String ifMatch) {
		ItemEntity itemEntity = getItemById(itemId);

		ETagUtils.checkETag(itemEntity, ifMatch);

		itemEntity.setDescription(updateItemDescriptionRequestDTO.getDescription());

		return itemMapper.fromEntity(itemRepository.save(itemEntity));
	}

	@Override
	public void deleteItem(UUID itemId) {
		ItemEntity item = getItemById(itemId);
		itemRepository.delete(item);
	}

	@Override
	public ListItemsResponseDTO getAllItems(Boolean notDone, PageRequest pageRequest) {
		Page<ItemEntity> page = (Boolean.TRUE.equals(notDone)) ?
				itemRepository.findAllByStatus(ItemStatusEnum.NOTDONE, pageRequest) : itemRepository.findAll(pageRequest);
		return itemMapper.pageToItemsResponseDTO(page);
	}

	private ItemEntity getItemById(UUID itemId) {
		return itemRepository.findById(itemId).orElseThrow(
				() -> new ItemNotFoundException(new ApplicationError().setCodeAndMessage(ErrorCode.ITEM_NOT_FOUND)));
	}
}

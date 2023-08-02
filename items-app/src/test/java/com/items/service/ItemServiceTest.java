package com.items.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.items.config.properties.ItemsApiProperties;
import com.items.db.model.ItemEntity;
import com.items.db.model.ItemStatusEnum;
import com.items.db.model.VersionModel;
import com.items.db.repository.ItemRepository;
import com.items.exception.ItemNotFoundException;
import com.items.mapper.ItemMapper;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.PagingDTO;
import com.items.model.UpdateItemDescriptionRequestDTO;
import com.items.model.UpdateItemRequestDTO;
import com.items.model.UpdateItemStatusRequestDTO;
import com.items.service.impl.ItemServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

  public static final String ITEM_NAME = "name";
  public static final String ITEM_DESC = "desc";
  public static final UUID ITEM_ID = UUID.randomUUID();
  public static final ItemDTO ITEM_DTO = new ItemDTO().id(ITEM_ID).name(ITEM_NAME).description(ITEM_DESC)
      .status(ItemStatusDTO.NOTDONE);
  private static final ItemEntity ITEM_ENTITY = ItemEntity.builder().id(ITEM_ID).status(ItemStatusEnum.NOTDONE).name(
      ITEM_NAME).description(ITEM_DESC).version(1L).build();

  @Mock
  private ItemRepository itemRepository;

  @Mock
  private ItemMapper itemMapper;

  @Mock
  private ItemsApiProperties itemsApiProperties;

  @Mock
  private Clock clock;

  @InjectMocks
  private ItemServiceImpl itemService;

  @Test
  void createItem() {
    CreateItemRequestDTO itemRequest = new CreateItemRequestDTO().name(ITEM_NAME).description(ITEM_DESC);
    when(itemMapper.fromCreateBodyToEntity(any())).thenReturn(ITEM_ENTITY);
    itemService.createItem(itemRequest);

    verify(itemRepository).save(any(ItemEntity.class));
  }

  @Test
  void createItem_checkItemProperties() {
    CreateItemRequestDTO itemRequest = new CreateItemRequestDTO().name(ITEM_NAME).description(ITEM_DESC);
    when(itemMapper.fromCreateBodyToEntity(any())).thenReturn(ITEM_ENTITY);
    when(itemMapper.fromEntity(any(ItemEntity.class))).thenReturn(ITEM_DTO);

    mockSaveItem();

    ItemDTO itemDTO = itemService.createItem(itemRequest);

    assertNotNull(itemDTO);
    assertEquals(itemDTO.getId(), ITEM_ID);
    assertEquals(itemDTO.getName(), ITEM_NAME);
    assertEquals(itemDTO.getDescription(), ITEM_DESC);
    assertEquals(itemDTO.getStatus(), ItemStatusDTO.NOTDONE);
  }

  @Test
  void getItem() {
    when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(ITEM_ENTITY));
    when(itemMapper.entityToVersionModel(any())).thenReturn(new VersionModel<>(1L, ITEM_DTO));

    assertDoesNotThrow(() -> itemService.getItem(ITEM_ID));

    VersionModel<ItemDTO> itemDTOVersionModel = itemService.getItem(ITEM_ID);
    assertNotNull(itemDTOVersionModel);
    assertEquals(itemDTOVersionModel.getVersion(), 1L);
    final var body = itemDTOVersionModel.getBody();
    assertNotNull(body);
    assertEquals(body.getId(), ITEM_ID);
    assertEquals(body.getName(), ITEM_NAME);
    assertEquals(body.getDescription(), ITEM_DESC);
    assertEquals(body.getStatus(), ItemStatusDTO.NOTDONE);
  }

  @Test
  void getItem_throwsNotFoundException() {
    when(itemRepository.findById(any())).thenReturn(Optional.empty());

    assertThrows(ItemNotFoundException.class, () -> itemService.deleteItem(ITEM_ID));
  }

  @Test
  void filterItems() {
    Page<ItemEntity> page = new PageImpl<>(List.of(ITEM_ENTITY));
    when(itemRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(page);
    when(itemMapper.pageToItemsResponseDTO(eq(page)))
        .thenReturn(new ListItemsResponseDTO()
            .results(List.of(new ItemDTO().id(ITEM_ID).name(ITEM_NAME).description(ITEM_DESC).status(ItemStatusDTO.NOTDONE)))
            .paging(new PagingDTO().pageNumber(1).pageSize(20)));
    when(itemsApiProperties.getDefaultZoneId()).thenReturn(ZoneId.of("Europe/Berlin"));
    when(clock.instant()).thenReturn(Instant.now());

    ListItemsResponseDTO itemsResponseDTO = itemService.getItems(ITEM_NAME, List.of(ITEM_ID), null, null, null, null,
        null, PageRequest.of(0, 20));
    assertNotNull(itemsResponseDTO);
    assertNotNull(itemsResponseDTO.getResults());
    assertNotNull(itemsResponseDTO.getPaging());
  }

  @Test
  void updateItem() {
    UpdateItemRequestDTO updateItemRequestDTO = new UpdateItemRequestDTO().description("updated").name("name_");
    when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(ITEM_ENTITY));
    when(itemMapper.updateEntityFromModel(eq(updateItemRequestDTO), any()))
        .thenReturn(ITEM_ENTITY);

    itemService.updateItem(updateItemRequestDTO, ITEM_ID, "\"1\"");
    verify(itemRepository).save(any(ItemEntity.class));
  }

  @Test
  void updateItemDesc() {
    UpdateItemDescriptionRequestDTO updateItemRequestDTO = new UpdateItemDescriptionRequestDTO().description("updated");
    when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(ITEM_ENTITY));

    itemService.updateItemDescription(updateItemRequestDTO, ITEM_ID, "\"1\"");
    verify(itemRepository).save(any(ItemEntity.class));
  }

  @Test
  void updateItemStatus() {
    UpdateItemStatusRequestDTO updateItemRequestDTO = new UpdateItemStatusRequestDTO().status(ItemStatusDTO.DONE);
    when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(ITEM_ENTITY));

    itemService.updateItemStatus(updateItemRequestDTO, ITEM_ID, "\"1\"");
    verify(itemRepository).save(any(ItemEntity.class));
  }

  @Test
  void deleteItem() {
    when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(ITEM_ENTITY));

    itemService.deleteItem(ITEM_ID);
    verify(itemRepository).delete(any(ItemEntity.class));
  }

  private void mockSaveItem() {
    doAnswer(answer((ItemEntity item) -> item))
        .when(itemRepository).save(any(ItemEntity.class));
  }

}

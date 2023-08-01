package com.items.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import com.items.db.model.ItemEntity;
import com.items.db.model.ItemStatusEnum;
import com.items.db.model.VersionModel;
import com.items.model.CreateItemRequestDTO;
import com.items.model.ItemDTO;
import com.items.model.ItemStatusDTO;
import com.items.model.ListItemsResponseDTO;
import com.items.model.PagingDTO;
import com.items.model.UpdateItemRequestDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {


  @Mapping(target = "lastModifiedBy", source = "updatedBy")
  ItemDTO fromEntity(ItemEntity entity);

  List<ItemStatusEnum> fromStatusDTO(List<ItemStatusDTO> itemStatusDTOList);

  @Mapping(target="status", constant="NOTDONE")
  ItemEntity fromCreateBodyToEntity(CreateItemRequestDTO createItemRequestDTO);

  ItemStatusEnum fromStatusDTO(ItemStatusDTO itemStatusDTO);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  ItemEntity updateEntityFromModel(UpdateItemRequestDTO dto,
      @MappingTarget ItemEntity entity);

  default VersionModel<ItemDTO> entityToVersionModel(ItemEntity entity) {
    return new VersionModel<>(entity.getVersion(),
        fromEntity(entity));
  }

  default ListItemsResponseDTO pageToItemsResponseDTO(Page<ItemEntity> page) {
    return new ListItemsResponseDTO()
        .results(page.get().map(this::fromEntity).collect(Collectors.toList()))
        .paging(createPagingResponseFromPage(page));
  }


  default PagingDTO createPagingResponseFromPage(Page<?> page) {
    return new PagingDTO()
        .pageNumber(page.getNumber())
        .pageSize(page.getSize())
        .pageCount(page.getTotalPages())
        .totalElements(page.getTotalElements());
  }
}

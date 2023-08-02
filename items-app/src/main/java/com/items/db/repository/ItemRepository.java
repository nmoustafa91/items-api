package com.items.db.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.items.db.model.ItemEntity;
import com.items.db.model.ItemStatusEnum;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, UUID>,
    PagingAndSortingRepository<ItemEntity, UUID>, JpaSpecificationExecutor<ItemEntity> {

  Page<ItemEntity> findAllByStatus(ItemStatusEnum notdone, PageRequest pageRequest);
}

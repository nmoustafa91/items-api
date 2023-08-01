package com.items.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.items.db.model.ItemStatusEnum;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ItemFilter {

  private String name;
  private String description;
  private String search;
  private LocalDate completionFrom;
  private LocalDate completionTo;
  private List<ItemStatusEnum> itemStatuses;
  private List<UUID> itemsIds;
  private Boolean notDone;
}

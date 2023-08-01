package com.items.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemStatusEnum {

  DONE("DONE"),
  NOTDONE("NOTDONE");

  @Getter
  private String value;

  public static ItemStatusEnum fromValue(String value) {
    for (ItemStatusEnum o : ItemStatusEnum.values()) {
      if (o.value.equalsIgnoreCase(value)) {
        return o;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }


}

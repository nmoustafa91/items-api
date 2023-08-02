package com.items.exception.general;

import lombok.ToString;

@ToString(callSuper=true)
public class ItemsException extends RuntimeException {

  public ItemsException(String message, Throwable cause) {
    super(message, cause);
  }

}

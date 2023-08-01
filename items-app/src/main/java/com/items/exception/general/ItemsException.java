package com.items.exception.general;

import lombok.ToString;

@ToString(callSuper=true)
public class ItemsException extends RuntimeException {

  public ItemsException() {
  }

  public ItemsException(String message) {
    super(message);
  }

  public ItemsException(String message, Throwable cause) {
    super(message, cause);
  }

  public ItemsException(Throwable cause) {
    super(cause);
  }

  public ItemsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

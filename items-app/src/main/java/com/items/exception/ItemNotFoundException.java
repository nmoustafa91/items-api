package com.items.exception;

import com.items.exception.general.ApplicationError;

public class ItemNotFoundException extends NotFoundException {
  public ItemNotFoundException(ApplicationError error) {
    super(error);
  }

  public ItemNotFoundException(ApplicationError error, Throwable cause) {
    super(error, cause);
  }
}

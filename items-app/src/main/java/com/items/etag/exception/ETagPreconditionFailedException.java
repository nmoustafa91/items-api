package com.items.etag.exception;

import com.items.exception.ValidationException;
import com.items.exception.general.ApplicationError;

public class ETagPreconditionFailedException extends ValidationException {
  public ETagPreconditionFailedException(ApplicationError error) {
    super(error);
  }
}

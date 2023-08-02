package com.items.exception;

import com.items.exception.general.ApplicationError;
import com.items.exception.general.ApplicationErrorException;

public class ValidationException extends ApplicationErrorException {

  public ValidationException(final ApplicationError error) { super(error); }

}

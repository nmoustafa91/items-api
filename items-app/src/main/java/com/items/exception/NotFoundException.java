package com.items.exception;

import com.items.exception.general.ApplicationError;
import com.items.exception.general.ApplicationErrorException;

public class NotFoundException extends ApplicationErrorException {

  public NotFoundException(final ApplicationError error) { super(error); }

}

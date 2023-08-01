package com.items.exception.general;

import lombok.ToString;

/**
 * Exception that utilizes {@link ApplicationError}.
 *
 */
@ToString(callSuper=true)
public class ApplicationErrorException extends ItemsException {

    private final ApplicationError applicationError;

    public ApplicationErrorException(final ApplicationError applicationError) {
        this(applicationError, null);
    }

    public ApplicationErrorException(final ApplicationError applicationError, final Throwable cause) {
        super(applicationError.toString(), cause);
        this.applicationError = applicationError;
    }

    public ApplicationError getApplicationError() {
        return applicationError;
    }
}

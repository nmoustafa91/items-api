package com.items.exception.general;

import lombok.Getter;

/**
 * Application domain errors.
 */
@Getter
public enum ErrorCode {


    /**
     * Item error codes (14xx)
     */
    ITEM_NOT_FOUND("ITEMS_ERR_1401_ITEM_NOT_FOUND", "Item not found."),
    
    ;

    ErrorCode(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    private String errorCode;
    private String message;
}

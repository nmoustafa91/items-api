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
    ITEM_WITH_CODE_EXISTS("ITEMS_ERR_1402_ITEM_WITH_CODE_EXISTS", "Item with given code already exists."),
    INVALID_ITEM_PRICE("ITEMS_ERR_1403_INVALID_ITEM_PRICE", "The given item price is invalid, it should be a positive number."),
    
    ;

    ErrorCode(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    private String errorCode;
    private String message;
}

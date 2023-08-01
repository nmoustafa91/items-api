package com.items.exception.general;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import com.items.model.ApiErrorResponseDTO;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ErrorResponseMapper {

  ApiErrorResponseDTO errorToDTO(ApplicationError error);
}

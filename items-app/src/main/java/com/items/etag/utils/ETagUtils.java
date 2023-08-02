package com.items.etag.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.items.db.model.ItemEntity;
import com.items.etag.exception.ETagPreconditionFailedException;
import com.items.exception.general.ApplicationError;

import jakarta.annotation.Nullable;
import lombok.NonNull;

public class ETagUtils {

  private static final Pattern ETAG_PATTERN = Pattern.compile("^(W\\/)?\"(.*)\"");

  private ETagUtils() {
  }

  public static void checkETag(@NonNull ItemEntity entity, @Nullable String eTag) {
    if (eTag == null) {
      throw new ETagPreconditionFailedException(new ApplicationError().setDetails(List.of("ETag is null")));
    }

    Matcher matcher = ETAG_PATTERN.matcher(eTag);
    if (!matcher.matches() || !matcher.group(2).equals(String.valueOf(entity.getVersion()))) {
      throw new ETagPreconditionFailedException(new ApplicationError().setDetails(
          List.of("ETag version does not match! createETagResponse=" + eTag + ", entity=" + entity)));
    }
  }

  public static String formatVersionToETag(Long version) {
    return "\"" + version + "\"";
  }

}

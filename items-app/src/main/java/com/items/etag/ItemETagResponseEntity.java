package com.items.etag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.items.db.model.VersionModel;
import com.items.etag.utils.ETagUtils;

import lombok.Data;

@Data
public class ItemETagResponseEntity<T> implements ETagResponseEntity<T> {

	private String eTag;
	private T body;

	public ItemETagResponseEntity(VersionModel<T> versionModel) {
		this.eTag = ETagUtils.formatVersionToETag(versionModel.getVersion());
		this.body = versionModel.getBody();
	}

	@Override
	public ResponseEntity<T> ok() {
		return httpStatus(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<T> httpStatus(HttpStatus httpStatus) {
		return ResponseEntity.status(httpStatus).eTag(eTag).body(body);
	}

}

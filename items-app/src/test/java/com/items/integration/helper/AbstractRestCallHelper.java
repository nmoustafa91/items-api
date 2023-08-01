package com.items.integration.helper;

import org.springframework.boot.test.web.client.TestRestTemplate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractRestCallHelper {

	static final String PAGE_NUMBER = "pageNumber";
	static final String PAGE_SIZE = "pageSize";
	static final String NOT_DONE = "notDone";
	static final String SEARCH = "search";
	static final String COMPLETION_DATE_TO = "completionDateTo";
	static final String COMPLETION_DATE_FROM = "completionDateFrom";
	static final String ITEM_STATUSES = "itemStatuses";
	static final String ITEM_IDS = "itemIds";
	static final String NAME = "name";

	protected final TestRestTemplate testRestTemplate;

}
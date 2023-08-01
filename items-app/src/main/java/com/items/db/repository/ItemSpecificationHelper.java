package com.items.db.repository;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.items.db.model.ItemEntity;
import com.items.db.model.ItemEntity_;
import com.items.db.model.ItemStatusEnum;
import com.items.domain.ItemFilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemSpecificationHelper {

	public Specification<ItemEntity> createFilter(ItemFilter itemFilter, ZoneOffset zoneOffset) {
		return (root, query, cb) -> {
			final List<Predicate> predicates = new LinkedList<>();

			// name
			if (StringUtils.hasText(itemFilter.getName())) {
				predicates.add(cb.like(cb.lower(root.get(ItemEntity_.NAME)),
						"%" + itemFilter.getName().toLowerCase(Locale.ROOT) + "%"));
			}

			// completionDate
			if (itemFilter.getCompletionFrom() != null && itemFilter.getCompletionTo() != null) {
				predicates.add(cb.between(root.get(ItemEntity_.COMPLETION_DATE),
						OffsetDateTime.of(itemFilter.getCompletionFrom(), LocalTime.MIN, zoneOffset),
						OffsetDateTime.of(itemFilter.getCompletionTo(), LocalTime.MAX, zoneOffset)));
			} else {
				if (itemFilter.getCompletionFrom() != null) {
					predicates.add(cb.greaterThanOrEqualTo(root.get(ItemEntity_.COMPLETION_DATE),
							OffsetDateTime.of(itemFilter.getCompletionFrom(), LocalTime.MIN, zoneOffset)));
				}
				if (itemFilter.getCompletionTo() != null) {
					predicates.add(cb.lessThanOrEqualTo(root.get(ItemEntity_.COMPLETION_DATE),
							OffsetDateTime.of(itemFilter.getCompletionTo(), LocalTime.MAX, zoneOffset)));
				}
			}

			// status
			if (Boolean.TRUE.equals(itemFilter.getNotDone())) {
				predicates.add(cb.equal(root.get(ItemEntity_.STATUS), ItemStatusEnum.NOTDONE));
			} else {
				if (!CollectionUtils.isEmpty(itemFilter.getItemStatuses())) {
					predicates.add(root.get(ItemEntity_.STATUS).in(itemFilter.getItemStatuses()));
				}
			}

			// ids
			if (!CollectionUtils.isEmpty(itemFilter.getItemsIds())) {
				predicates.add(root.get(ItemEntity_.ID).in(itemFilter.getItemsIds()));
			}

			// Search
			if (StringUtils.hasText(itemFilter.getSearch())) {
				predicates.add(cb.or(cb.equal(root.get(ItemEntity_.ID).as(String.class), itemFilter.getSearch()),
						lowercaseLike(cb, root.get(ItemEntity_.NAME), itemFilter.getSearch())));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Predicate lowercaseLike(final CriteriaBuilder cb, Expression<String> attribute, final String fieldValue) {
		return cb.like(cb.lower(attribute), "%" + fieldValue.toLowerCase(Locale.getDefault()) + "%");
	}
}

package com.items.config;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.items.infrastructure.persistence.audit.AuditorAwareImpl;

@Configuration
@EnableJpaRepositories(basePackages = "com.items.db.repository")
@EnableJpaAuditing(modifyOnCreate = false, dateTimeProviderRef = "auditingDateTimeProvider")
public class PersistenceConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	@Bean(name = "auditingDateTimeProvider")
	public DateTimeProvider dateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}

	@Bean
	@ConditionalOnMissingBean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}

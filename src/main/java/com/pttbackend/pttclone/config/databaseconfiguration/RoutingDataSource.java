package com.pttbackend.pttclone.config.databaseconfiguration;

import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;


/**
 * <p> The RoutingDataSource responsible for routing to the appropriate DataSource <p>
 */
@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {
    // Routing The Specific DataSource
    @Nullable
    @Override
	protected Object determineCurrentLookupKey() {
        log.info("_________________________________"+DataSourceContextHolder.isReadOnly());
        //return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? DataSourceType.SLAVE : DataSourceType.MASTER;
        return DataSourceContextHolder.isReadOnly() ? DataSourceType.SLAVE : DataSourceType.MASTER;
	}
}

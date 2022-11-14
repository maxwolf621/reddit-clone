package com.pttbackend.pttclone.config.databaseconfiguration;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Scope;

/**
 * <p> Thread to access database </p>
 */

@Slf4j
public class DataSourceContextHolder {

	private static final ThreadLocal<AtomicInteger> threadLocal = ThreadLocal.withInitial(() -> new AtomicInteger(0));


    // set thread to use specific database
    //public void setBranchContext(DataSourceType dataSourceType) {
    //    Assert.notNull(dataSourceType, "DataSource Cant Not Be Null");
	//	threadLocal.set(dataSourceType);
	//}

	public static boolean isReadOnly() {
        return threadLocal.get().get() > 0;
    }

	public static void createThread() {
		threadLocal.get().incrementAndGet();
	}

	public static void releaseThread() {
		threadLocal.get().decrementAndGet();
	}
}

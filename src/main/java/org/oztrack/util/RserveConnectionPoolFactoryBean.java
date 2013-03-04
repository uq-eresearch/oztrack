package org.oztrack.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.FactoryBean;

public class RserveConnectionPoolFactoryBean implements FactoryBean<ObjectPool<RConnection>> {
    protected final Log logger = LogFactory.getLog(getClass());

    private final int numConnections;

    public RserveConnectionPoolFactoryBean(int numConnections) {
        this.numConnections = numConnections;
    }

    @Override
    public ObjectPool<RConnection> getObject() throws Exception {
        logger.info("Creating Rserve connection pool");
        RserveConnectionFactory factory = new RserveConnectionFactory();
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = numConnections;
        config.maxIdle = numConnections;
        config.minIdle = numConnections;
        config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
        config.testOnBorrow = true;
        config.testOnReturn = true;
        config.testWhileIdle = true;
        config.timeBetweenEvictionRunsMillis = 1000; // enables eviction thread
        config.minEvictableIdleTimeMillis = -1; // disable eviction due to idle time
        config.numTestsPerEvictionRun = -1; // test all idle objects
        config.lifo = false;
        ObjectPool<RConnection> connectionPool = new GenericObjectPool<RConnection>(factory, config);
        return connectionPool;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

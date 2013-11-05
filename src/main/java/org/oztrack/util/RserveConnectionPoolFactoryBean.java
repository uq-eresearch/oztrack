package org.oztrack.util;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.log4j.Logger;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.FactoryBean;

public class RserveConnectionPoolFactoryBean implements FactoryBean<ObjectPool<RConnection>> {
    private final Logger logger = Logger.getLogger(getClass());

    private final String[] hosts;
    private final int numConnections;

    public RserveConnectionPoolFactoryBean(String[] hosts, int numConnections) {
        this.hosts = hosts;
        this.numConnections = numConnections;
    }

    @Override
    public ObjectPool<RConnection> getObject() throws Exception {
        logger.info("Creating Rserve connection pool");

        final RserveConnectionFactory keyedConnectionFactory = new RserveConnectionFactory();

        final GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        config.maxActive = numConnections;
        config.maxIdle = numConnections;
        config.minIdle = numConnections;
        config.whenExhaustedAction = GenericKeyedObjectPool.WHEN_EXHAUSTED_BLOCK;
        config.testOnBorrow = true;
        config.testOnReturn = true;
        config.testWhileIdle = true;
        config.timeBetweenEvictionRunsMillis = 1000; // enables eviction thread
        config.minEvictableIdleTimeMillis = -1; // disable eviction due to idle time
        config.numTestsPerEvictionRun = -1; // test all idle objects
        config.lifo = false;

        final GenericKeyedObjectPool<String, RConnection> keyedConnectionPool =
            new GenericKeyedObjectPool<String, RConnection>(keyedConnectionFactory, config);

        // Initialise keyed object pool with each host
        for (String host : hosts) {
            keyedConnectionPool.preparePool(host, true);
        }

        // Initialise blocking queue with elements representing potential host connections.
        // There are n elements per host, where n is the number of connections permitted per host.
        // When connections are obtained, we take the host from the head of the queue and use this as the key to the pool.
        // When connections are released, we return the host to the tail of the queue so future connections can be obtained.
        // This implements a first-available-first-used (or first-in-first-out) model for host connections.
        final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(hosts.length * numConnections, true);
        for (String host : hosts) {
            for (int i = 0; i < numConnections; i++) {
                queue.add(host);
            }
        }

        return new ObjectPool<RConnection>() {
            @Override
            public RConnection borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
                String host = queue.take();
                return keyedConnectionPool.borrowObject(host);
            }

            @Override
            public void returnObject(RConnection connection) throws Exception {
                Field hostField = connection.getClass().getDeclaredField("host");
                hostField.setAccessible(true);
                String host = (String) hostField.get(connection);
                keyedConnectionPool.returnObject(host, connection);
                queue.put(host);
            }

            @Override
            public void invalidateObject(RConnection connection) throws Exception {
                Field hostField = connection.getClass().getDeclaredField("host");
                hostField.setAccessible(true);
                String host = (String) hostField.get(connection);
                keyedConnectionPool.invalidateObject(host, connection);
                queue.put(host);
            }

            @Override
            public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getNumIdle() throws UnsupportedOperationException {
                return keyedConnectionPool.getNumIdle();
            }

            @Override
            public int getNumActive() throws UnsupportedOperationException {
                return keyedConnectionPool.getNumActive();
            }

            @Override
            public void clear() throws Exception, UnsupportedOperationException {
                keyedConnectionPool.clear();
            }

            @Override
            public void close() throws Exception {
                keyedConnectionPool.close();
            }

            @Override
            public void setFactory(PoolableObjectFactory<RConnection> factory) throws IllegalStateException, UnsupportedOperationException {
                throw new UnsupportedOperationException();
            }

        };
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

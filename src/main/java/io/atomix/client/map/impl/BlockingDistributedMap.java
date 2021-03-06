/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.client.map.impl;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Throwables;
import io.atomix.client.PrimitiveException;
import io.atomix.client.PrimitiveState;
import io.atomix.client.Synchronous;
import io.atomix.client.collection.DistributedCollection;
import io.atomix.client.collection.impl.BlockingDistributedCollection;
import io.atomix.client.map.AsyncDistributedMap;
import io.atomix.client.map.DistributedMap;
import io.atomix.client.map.MapEventListener;
import io.atomix.client.set.DistributedSet;
import io.atomix.client.set.impl.BlockingDistributedSet;

/**
 * Default implementation of {@code ConsistentMap}.
 *
 * @param <K> type of key.
 * @param <V> type of value.
 */
public class BlockingDistributedMap<K, V> extends Synchronous<AsyncDistributedMap<K, V>> implements DistributedMap<K, V> {

    private static final int MAX_DELAY_BETWEEN_RETRY_MILLS = 50;
    private final AsyncDistributedMap<K, V> asyncMap;
    private final long operationTimeoutMillis;

    public BlockingDistributedMap(AsyncDistributedMap<K, V> asyncMap, long operationTimeoutMillis) {
        super(asyncMap);
        this.asyncMap = asyncMap;
        this.operationTimeoutMillis = operationTimeoutMillis;
    }

    @Override
    public int size() {
        return complete(asyncMap.size());
    }

    @Override
    public boolean isEmpty() {
        return complete(asyncMap.isEmpty());
    }

    @Override
    public boolean containsKey(Object key) {
        return complete(asyncMap.containsKey((K) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return complete(asyncMap.containsValue((V) value));
    }

    @Override
    public V put(K key, V value) {
        return complete(asyncMap.put(key, value));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        complete(asyncMap.putAll(m));
    }

    @Override
    public V get(Object key) {
        return complete(asyncMap.get((K) key));
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return complete(asyncMap.getOrDefault((K) key, defaultValue));
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return complete(asyncMap.computeIfAbsent(key, mappingFunction));
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return complete(asyncMap.computeIfPresent(key, remappingFunction));
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return complete(asyncMap.compute(key, remappingFunction));
    }

    @Override
    public V remove(Object key) {
        return complete(asyncMap.remove((K) key));
    }

    @Override
    public void clear() {
        complete(asyncMap.clear());
    }

    @Override
    public DistributedSet<K> keySet() {
        return new BlockingDistributedSet<K>(asyncMap.keySet(), operationTimeoutMillis);
    }

    @Override
    public DistributedCollection<V> values() {
        return new BlockingDistributedCollection<>(asyncMap.values(), operationTimeoutMillis);
    }

    @Override
    public DistributedSet<Map.Entry<K, V>> entrySet() {
        return new BlockingDistributedSet<>(asyncMap.entrySet(), operationTimeoutMillis);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return complete(asyncMap.remove((K) key, (V) value));
    }

    @Override
    public V replace(K key, V value) {
        return complete(asyncMap.replace(key, value));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return complete(asyncMap.replace(key, oldValue, newValue));
    }

    @Override
    public void addListener(MapEventListener<K, V> listener, Executor executor) {
        complete(asyncMap.addListener(listener, executor));
    }

    @Override
    public void removeListener(MapEventListener<K, V> listener) {
        complete(asyncMap.removeListener(listener));
    }

    @Override
    public void addStateChangeListener(Consumer<PrimitiveState> listener) {
        asyncMap.addStateChangeListener(listener);
    }

    @Override
    public void removeStateChangeListener(Consumer<PrimitiveState> listener) {
        asyncMap.removeStateChangeListener(listener);
    }

    @Override
    public AsyncDistributedMap<K, V> async() {
        return asyncMap;
    }

    protected <T> T complete(CompletableFuture<T> future) {
        try {
            return future.get(operationTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PrimitiveException.Interrupted();
        } catch (TimeoutException e) {
            throw new PrimitiveException.ConcurrentModification();
        } catch (ExecutionException e) {
            Throwable cause = Throwables.getRootCause(e);
            if (cause instanceof PrimitiveException) {
                throw (PrimitiveException) cause;
            } else {
                throw new PrimitiveException(cause);
            }
        }
    }
}

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

import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;
import io.atomix.api.primitive.Name;
import io.atomix.client.PrimitiveManagementService;
import io.atomix.client.map.AsyncAtomicMap;
import io.atomix.client.map.AtomicMap;
import io.atomix.client.map.AtomicMapBuilder;
import io.atomix.client.utils.serializer.Serializer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Default {@link AsyncAtomicMap} builder.
 *
 * @param <K> type for map key
 * @param <V> type for map value
 */
public class DefaultAtomicMapBuilder<K, V> extends AtomicMapBuilder<K, V> {
    public DefaultAtomicMapBuilder(Name name, PrimitiveManagementService managementService) {
        super(name, managementService);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<AtomicMap<K, V>> buildAsync() {
        Map<Integer, AsyncAtomicMap<String, byte[]>> partitions = managementService.getSessionService().getSessions().stream()
            .map(session -> Maps.immutableEntry(session.getPartition().id(), new DefaultAsyncAtomicMap(getName(), session, managementService.getThreadFactory().createContext())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new PartitionedAsyncAtomicMap(name, partitions, partitioner).connect()
            .thenApply(rawMap -> {
                Serializer serializer = serializer();
                return new TranscodingAsyncAtomicMap<K, V, String, byte[]>(
                    rawMap,
                    key -> BaseEncoding.base16().encode(serializer.encode(key)),
                    string -> serializer.decode(BaseEncoding.base16().decode(string)),
                    value -> serializer.encode(value),
                    bytes -> serializer.decode(bytes));
            })
            .<AsyncAtomicMap<K, V>>thenApply(map -> {
                if (cacheEnabled) {
                    return new CachingAsyncAtomicMap<>(map, cacheSize);
                }
                return map;
            })
            .thenApply(map -> {
                if (readOnly) {
                    return new UnmodifiableAsyncAtomicMap<>(map);
                }
                return map;
            })
            .thenApply(AsyncAtomicMap::sync);
    }
}

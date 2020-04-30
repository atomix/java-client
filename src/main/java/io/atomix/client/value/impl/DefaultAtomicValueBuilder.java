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
package io.atomix.client.value.impl;

import com.google.common.io.BaseEncoding;
import io.atomix.api.primitive.Name;
import io.atomix.client.PrimitiveManagementService;
import io.atomix.client.utils.serializer.Serializer;
import io.atomix.client.value.AsyncAtomicValue;
import io.atomix.client.value.AtomicValue;
import io.atomix.client.value.AtomicValueBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * Default implementation of AtomicValueBuilder.
 *
 * @param <V> value type
 */
public class DefaultAtomicValueBuilder<V> extends AtomicValueBuilder<V> {
    public DefaultAtomicValueBuilder(Name name, PrimitiveManagementService managementService) {
        super(name, managementService);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<AtomicValue<V>> buildAsync() {
        return new DefaultAsyncAtomicValue(
            getName(),
            managementService.getSessionService().getSession(partitioner.partition(getName().getName(), managementService.getPartitionService().getPartitionIds())),
            managementService.getThreadFactory().createContext())
            .connect()
            .thenApply(rawValue -> {
                Serializer serializer = serializer();
                return new TranscodingAsyncAtomicValue<V, String>(
                    rawValue,
                    value -> BaseEncoding.base16().encode(serializer.encode(value)),
                    string -> serializer.decode(BaseEncoding.base16().decode(string)));
            })
            .thenApply(AsyncAtomicValue::sync);
    }
}

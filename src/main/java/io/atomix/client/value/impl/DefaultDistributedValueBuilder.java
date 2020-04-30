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

import java.util.concurrent.CompletableFuture;

import com.google.common.io.BaseEncoding;
import io.atomix.api.primitive.Name;
import io.atomix.client.PrimitiveManagementService;
import io.atomix.client.utils.serializer.Serializer;
import io.atomix.client.value.AsyncDistributedValue;
import io.atomix.client.value.DistributedValue;
import io.atomix.client.value.DistributedValueBuilder;

/**
 * Default implementation of DistributedValueBuilder.
 *
 * @param <V> value type
 */
public class DefaultDistributedValueBuilder<V> extends DistributedValueBuilder<V> {
    public DefaultDistributedValueBuilder(Name name, PrimitiveManagementService managementService) {
        super(name, managementService);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<DistributedValue<V>> buildAsync() {
        /*return managementService.getPartitionService().getPartition(group)
            .thenCompose(group -> new DefaultAsyncAtomicValue(
                getName(),
                group.getPartition(partitioner.partition(getName().getName(), group.getPartitionIds())),
                managementService.getThreadFactory().createContext(),
                sessionTimeout)
                .connect()
                .thenApply(rawValue -> {
                    Serializer serializer = serializer();
                    return new TranscodingAsyncAtomicValue<V, String>(
                        rawValue,
                        value -> BaseEncoding.base16().encode(serializer.encode(value)),
                        string -> serializer.decode(BaseEncoding.base16().decode(string)));
                })
                .thenApply(DelegatingAsyncDistributedValue::new)
                .thenApply(AsyncDistributedValue::sync));*/
        return null;
    }

}

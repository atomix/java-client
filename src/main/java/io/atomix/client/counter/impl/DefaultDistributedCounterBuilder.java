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
package io.atomix.client.counter.impl;

import io.atomix.api.primitive.Name;
import io.atomix.client.PrimitiveManagementService;
import io.atomix.client.counter.AsyncDistributedCounter;
import io.atomix.client.counter.DistributedCounter;
import io.atomix.client.counter.DistributedCounterBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * Default distributed counter builder.
 */
public class DefaultDistributedCounterBuilder extends DistributedCounterBuilder {
    public DefaultDistributedCounterBuilder(Name name, PrimitiveManagementService managementService) {
        super(name, managementService);
    }

    @Override
    public CompletableFuture<DistributedCounter> buildAsync() {
        return new DefaultAsyncAtomicCounter(
            getName(),
            managementService.getSessionService().getSession(partitioner.partition(getName().getName(), managementService.getPartitionService().getPartitionIds())),
            managementService.getThreadFactory().createContext())
            .connect()
            .thenApply(DelegatingDistributedCounter::new)
            .thenApply(AsyncDistributedCounter::sync);
    }
}

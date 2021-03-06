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
package io.atomix.client;

import io.atomix.api.primitive.Name;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Atomix primitive cache.
 */
public interface PrimitiveCache {

    /**
     * Gets or creates a locally cached multiton primitive instance.
     *
     * @param name     the primitive name
     * @param supplier the primitive factory
     * @param <P>      the primitive type
     * @return the primitive instance
     */
    <P extends DistributedPrimitive> CompletableFuture<P> getPrimitive(Name name, Supplier<CompletableFuture<P>> supplier);

}

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

import io.atomix.client.partition.PartitionService;
import io.atomix.client.session.SessionService;
import io.atomix.client.utils.concurrent.ThreadContextFactory;

/**
 * Primitive management service.
 */
public interface PrimitiveManagementService {

    /**
     * Returns the session service.
     *
     * @return the session service
     */
    SessionService getSessionService();

    /**
     * Returns the partition service.
     *
     * @return the partition service
     */
    PartitionService getPartitionService();

    /**
     * Returns the local primitive cache.
     *
     * @return the local primitive cache
     */
    PrimitiveCache getPrimitiveCache();

    /**
     * Returns the thread context factory.
     *
     * @return the thread context factory
     */
    ThreadContextFactory getThreadFactory();

}

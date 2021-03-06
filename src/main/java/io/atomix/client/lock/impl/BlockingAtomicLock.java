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
package io.atomix.client.lock.impl;

import java.time.Duration;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Throwables;
import io.atomix.client.PrimitiveException;
import io.atomix.client.Synchronous;
import io.atomix.client.lock.AsyncAtomicLock;
import io.atomix.client.lock.AtomicLock;

;

/**
 * Default implementation for a {@code DistributedLock} backed by a {@link AsyncAtomicLock}.
 */
public class BlockingAtomicLock extends Synchronous<AsyncAtomicLock> implements AtomicLock {

    private final AsyncAtomicLock asyncLock;
    private final long operationTimeoutMillis;

    public BlockingAtomicLock(AsyncAtomicLock asyncLock, long operationTimeoutMillis) {
        super(asyncLock);
        this.asyncLock = asyncLock;
        this.operationTimeoutMillis = operationTimeoutMillis;
    }

    @Override
    public long lock() {
        return asyncLock.lock().join();
    }

    @Override
    public OptionalLong tryLock() {
        return asyncLock.tryLock().join();
    }

    @Override
    public OptionalLong tryLock(Duration timeout) {
        return asyncLock.tryLock(timeout).join();
    }

    @Override
    public boolean isLocked() {
        return complete(asyncLock.isLocked());
    }

    @Override
    public boolean isLocked(long version) {
        return complete(asyncLock.isLocked(version));
    }

    @Override
    public void unlock() {
        complete(asyncLock.unlock());
    }

    @Override
    public boolean unlock(long version) {
        return complete(asyncLock.unlock(version));
    }

    @Override
    public AsyncAtomicLock async() {
        return asyncLock;
    }

    private <T> T complete(CompletableFuture<T> future) {
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

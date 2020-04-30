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
package io.atomix.client.election.impl;

import io.atomix.api.election.*;
import io.atomix.api.primitive.Name;
import io.atomix.client.election.*;
import io.atomix.client.impl.AbstractAsyncPrimitive;
import io.atomix.client.session.Session;
import io.atomix.client.utils.concurrent.ThreadContext;
import io.grpc.stub.StreamObserver;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Distributed resource providing the {@link AsyncLeaderElection} primitive.
 */
public class DefaultAsyncLeaderElection
    extends AbstractAsyncPrimitive<LeaderElectionServiceGrpc.LeaderElectionServiceStub, AsyncLeaderElection<String>>
    implements AsyncLeaderElection<String> {
    private volatile CompletableFuture<Long> listenFuture;
    private final Set<LeadershipEventListener<String>> eventListeners = new CopyOnWriteArraySet<>();

    public DefaultAsyncLeaderElection(Name name, Session session, ThreadContext context) {
        super(name, LeaderElectionServiceGrpc.newStub(session.getPartition().getChannelFactory().getChannel()), session, context);
    }

    @Override
    public CompletableFuture<Leadership<String>> run(String identifier) {
        /*return command(
            (header, observer) -> getService().enter(EnterRequest.newBuilder()
                .setHeader(header)
                .setCandidateId(identifier)
                .build(), observer), EnterResponse::getHeader)
            .thenApply(response -> new Leadership<>(!Strings.isNullOrEmpty(response.getLeader())
                ? new Leader<>(response.getLeader(), response.getTerm(), response.getTimestamp())
                : null,
                response.getCandidatesList()));*/
        return null;
    }

    @Override
    public CompletableFuture<Void> withdraw(String identifier) {
        return command(
            (header, observer) -> getService().withdraw(WithdrawRequest.newBuilder()
                .setHeader(header)
                .setCandidateId(identifier)
                .build(), observer), WithdrawResponse::getHeader)
            .thenApply(response -> null);
    }

    @Override
    public CompletableFuture<Boolean> anoint(String identifier) {
        /*return command(
            (header, observer) -> getService().anoint(AnointRequest.newBuilder()
                .setHeader(header)
                .setCandidateId(identifier)
                .build(), observer), AnointResponse::getHeader)
            .thenApply(response -> response.getSucceeded());*/
        return null;
    }

    @Override
    public CompletableFuture<Void> evict(String identifier) {
        return command(
            (header, observer) -> getService().evict(EvictRequest.newBuilder()
                .setHeader(header)
                .setCandidateId(identifier)
                .build(), observer), EvictResponse::getHeader)
            .thenApply(response -> null);
    }

    @Override
    public CompletableFuture<Boolean> promote(String identifier) {
        /*return command(
            (header, observer) -> getService().promote(PromoteRequest.newBuilder()
                .setHeader(header)
                .setCandidateId(identifier)
                .build(), observer), PromoteResponse::getHeader)
            .thenApply(response -> response.getSucceeded());*/
        return null;
    }

    @Override
    public CompletableFuture<Leadership<String>> getLeadership() {
        /*return query(
            (header, observer) -> getService().getLeadership(GetLeadershipRequest.newBuilder()
                .setHeader(header)
                .build(), observer), GetLeadershipResponse::getHeader)
            .thenApply(response -> new Leadership<>(!Strings.isNullOrEmpty(response.getLeader())
                ? new Leader<>(response.getLeader(), response.getTerm(), response.getTimestamp())
                : null,
                response.getCandidatesList()));*/
        return null;
    }

    private synchronized CompletableFuture<Void> listen() {
        if (listenFuture == null && !eventListeners.isEmpty()) {
            listenFuture = command(
                (header, observer) -> getService().events(EventRequest.newBuilder()
                    .setHeader(header)
                    .build(), observer),
                EventResponse::getHeader,
                new StreamObserver<EventResponse>() {
                    @Override
                    public void onNext(EventResponse response) {
                        LeadershipEvent<String> event = null;
                        switch (response.getType()) {
                            case CHANGED:
                                /*event = new LeadershipEvent<>(
                                    LeadershipEvent.Type.CHANGED,
                                    new Leadership<>(!Strings.isNullOrEmpty(response.getLeader())
                                        ? new Leader<>(response.getLeader(), response.getTerm(), response.getTimestamp())
                                        : null,
                                        response.getCandidatesList()));
                                break;*/
                        }
                        onEvent(event);
                    }

                    private void onEvent(LeadershipEvent<String> event) {
                        eventListeners.forEach(l -> l.event(event));
                    }

                    @Override
                    public void onError(Throwable t) {
                        onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        synchronized (DefaultAsyncLeaderElection.this) {
                            listenFuture = null;
                        }
                        listen();
                    }
                });
        }
        return listenFuture.thenApply(v -> null);
    }

    @Override
    public synchronized CompletableFuture<Void> addListener(LeadershipEventListener<String> listener) {
        eventListeners.add(listener);
        return listen();
    }

    @Override
    public synchronized CompletableFuture<Void> removeListener(LeadershipEventListener<String> listener) {
        eventListeners.remove(listener);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected CompletableFuture<Void> create() {
        return this.<CreateResponse>session((header, observer) -> getService().create(CreateRequest.newBuilder()
            .setHeader(header)
            .build(), observer))
            .thenApply(v -> null);
    }

    @Override
    protected CompletableFuture<Void> close(boolean delete) {
        return this.<CloseResponse>session((header, observer) -> getService().close(CloseRequest.newBuilder()
            .setHeader(header)
            .setDelete(delete)
            .build(), observer))
            .thenApply(v -> null);
    }

    @Override
    public LeaderElection<String> sync(Duration operationTimeout) {
        return new BlockingLeaderElection<>(this, operationTimeout.toMillis());
    }
}
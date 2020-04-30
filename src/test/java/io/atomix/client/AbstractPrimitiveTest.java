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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.protobuf.Empty;
import io.atomix.api.controller.NodeConfig;
import io.atomix.api.controller.Partition;
import io.atomix.api.controller.PartitionConfig;
import io.atomix.api.controller.PartitionEndpoint;
import io.atomix.api.controller.PartitionGroup;
import io.atomix.api.controller.PartitionGroupId;
import io.atomix.api.controller.PartitionGroupSpec;
import io.atomix.api.controller.PartitionId;
import org.junit.After;
import org.junit.Before;

/**
 * Base Atomix test.
 */
public abstract class AbstractPrimitiveTest {
  /*private TestController controller;
  private List<AtomixServer> servers;
  private List<AtomixClient> clients;*/

  /**
   * Returns a new Atomix instance.
   *
   * @return a new Atomix instance.
   */
  /*protected AtomixClient client() throws Exception {
    AtomixClient client = AtomixClient.builder()
        .withServer("localhost", 6000)
        .build();
    client.start().get(10, TimeUnit.SECONDS);
    return client;
  }

  private AtomixServer createServer(int partitionId, String host, int port) {
    String memberId = String.format("test-%d", partitionId);
    PartitionConfig partitionConfig = PartitionConfig.newBuilder()
        .setPartition(PartitionId.newBuilder()
            .setGroup(PartitionGroupId.newBuilder()
                .setName("test")
                .setNamespace("default")
                .build())
            .setPartition(partitionId)
            .build())
        .setController(NodeConfig.newBuilder()
            .setId("controller")
            .setHost("localhost")
            .setPort(6000)
            .build())
        .addMembers(NodeConfig.newBuilder()
            .setId(memberId)
            .setHost(host)
            .setPort(port)
            .build())
        .build();
    return new AtomixServer(memberId, partitionConfig, TestProtocol.TYPE, Empty.newBuilder().build());
  }

  @Before
  public void setupTest() throws Exception {
    deleteData();
    TestProtocol.reset();
    clients = new CopyOnWriteArrayList<>();
    servers = new CopyOnWriteArrayList<>();
    PartitionGroup partitionGroup = PartitionGroup.newBuilder()
        .setId(PartitionGroupId.newBuilder()
            .setName("test")
            .setNamespace("default")
            .build())
        .setSpec(PartitionGroupSpec.newBuilder()
            .setPartitions(3)
            .setPartitionSize(1)
            .build())
        .addPartitions(Partition.newBuilder()
            .setPartitionId(1)
            .addEndpoints(PartitionEndpoint.newBuilder()
                .setHost("localhost")
                .setPort(5001)
                .build())
            .build())
        .addPartitions(Partition.newBuilder()
            .setPartitionId(2)
            .addEndpoints(PartitionEndpoint.newBuilder()
                .setHost("localhost")
                .setPort(5002)
                .build())
            .build())
        .addPartitions(Partition.newBuilder()
            .setPartitionId(3)
            .addEndpoints(PartitionEndpoint.newBuilder()
                .setHost("localhost")
                .setPort(5003)
                .build())
            .build())
        .build();
    controller = new TestController(6000, Collections.singletonList(partitionGroup));
    controller.start().get(30, TimeUnit.SECONDS);

    servers.add(createServer(1, "localhost", 5001));
    servers.add(createServer(2, "localhost", 5002));
    servers.add(createServer(3, "localhost", 5003));
    Futures.allOf(servers.stream().map(AtomixServer::start)).get(30, TimeUnit.SECONDS);
  }

  @After
  public void teardownTest() throws Exception {
    List<CompletableFuture<Void>> futures = clients.stream().map(AtomixClient::stop).collect(Collectors.toList());
    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get(30, TimeUnit.SECONDS);
    } catch (Exception e) {
      // Do nothing
    }
    Futures.allOf(servers.stream().map(AtomixServer::stop)).get(30, TimeUnit.SECONDS);
    deleteData();
  }

  public void deleteData() throws IOException {
    if (Files.exists(Paths.get("target/test-data/"))) {
      Files.walkFileTree(Paths.get("target/test-data/"), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }*/
}

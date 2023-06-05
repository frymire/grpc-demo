
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class HelloWorldClient {
  
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  public HelloWorldClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String name) {
    
    logger.info("Sending: " + name + " ...");
    
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();    
    HelloReply response1;
    HelloReply response2;
    try {
      response1 = blockingStub.sayHello(request);
      response2 = blockingStub.sayHelloAgain(request);
    } catch (StatusRuntimeException e) {
      logger.warning("RPC failed: " + e.getStatus());
      return;
    }
    
    logger.info("Received: " + response1.getMessage());
    logger.info("Received: " + response2.getMessage());
  }

  public static void main(String[] args) throws Exception {
    HelloWorldClient client = new HelloWorldClient("localhost", HelloWorldServer.port);
    try {
      client.greet("Mark");
    } finally {
      client.shutdown();
    }
  }
}

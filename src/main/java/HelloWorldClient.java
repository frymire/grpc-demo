

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class HelloWorldClient {
  
  private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

  private final ManagedChannel channel;
  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  public HelloWorldClient(String host, int port) {
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void greet(String name) {
    
    logger.info("Sending: " + name + " ...");
    
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();    
    HelloReply response;    
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    
    logger.info("Received (1): " + response.getMessage());    
  }

  public static void main(String[] args) throws Exception {
    
    /* Access a service running on the local machine on port 50051 */
    HelloWorldClient client = new HelloWorldClient("localhost", 50051);
    
    try {
      client.greet("Mark");
    } finally {
      client.shutdown();
    }
    
  }
  
}

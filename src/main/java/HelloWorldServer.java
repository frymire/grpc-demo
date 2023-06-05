
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class HelloWorldServer {
  
  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());
  public static final int port = 50051;
  private Server server;

  public static void main(String[] args) throws IOException, InterruptedException {
    final HelloWorldServer server = new HelloWorldServer();
    server.start();
    server.blockUntilShutdown();
  }

  private void start() throws IOException {

    server = ServerBuilder.forPort(port).addService(new GreeterImpl()).build().start();
    logger.info("Server started, listening on " + port);
    
    Thread thread = new Thread(() -> {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      HelloWorldServer.this.stop();
      System.err.println("*** server shut down");
    });
    
    Runtime.getRuntime().addShutdownHook(thread);
  }

  private void stop() {
    if (server != null) server.shutdown();
  }

  /** Await termination on the main thread since the grpc library uses daemon threads. */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) server.awaitTermination();
  }

  private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Suck it, " + request.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

    @Override public void sayHelloAgain(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
      HelloReply reply = HelloReply.newBuilder().setMessage("Suck it again, " + request.getName()).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
    }

  }
}

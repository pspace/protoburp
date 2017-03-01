package space.polylog.protoburp;

/**
 * Created by hsp on 3/1/17.
 */

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import space.polylog.burp.protobuf.BurpConnectorGrpc;

public class ProtoburpClient {

    private final ManagedChannel channel;
    private final BurpConnectorGrpc.BurpConnectorBlockingStub blockingStub;

    public ProtoburpClient(String hostname, int port, boolean usePlain) {
        this(ManagedChannelBuilder.forAddress(hostname, port)
                .usePlaintext(usePlain));
    }

    public ProtoburpClient(ManagedChannelBuilder<?> mcb){
        channel = mcb.build();
        blockingStub = BurpConnectorGrpc.newBlockingStub(channel);
    }

      public static void main(String[] args) throws Exception {
          ProtoburpClient client = new ProtoburpClient("localhost", 50051,false);
    try {
      /* Access a service running on the local machine on port 50051 */
      String user = "world";
      if (args.length > 0) {
        user = args[0]; /* Use the arg as the name to greet if provided */
      }
    } finally {
    }
  }

}

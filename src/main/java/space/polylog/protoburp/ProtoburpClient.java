package space.polylog.protoburp;

/**
 * Created by hsp on 3/1/17.
 */

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import space.polylog.burp.protobuf.BurpConnectorGrpc;
import space.polylog.burp.protobuf.BurpRequest;
import space.polylog.burp.protobuf.BurpResponse;

import java.util.concurrent.TimeUnit;

public class ProtoburpClient {

    private final ManagedChannel channel;
    private final BurpConnectorGrpc.BurpConnectorBlockingStub blockingStub;

    public ProtoburpClient(String hostname, int port, boolean usePlain) {
        this(ManagedChannelBuilder.forAddress(hostname, port)
                .usePlaintext(usePlain));
    }

    private ProtoburpClient(ManagedChannelBuilder<?> mcb){
        channel = mcb.build();
        blockingStub = BurpConnectorGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public BurpRequest sendBurpRequest(BurpRequest request){
        BurpRequest response = null;
        try{
            response = blockingStub.sendRequest(request);
        }
        catch (StatusRuntimeException sre){

            response = null;

        } finally {
            return response;
        }
    }

    public void sendBurpResponse(BurpResponse response){
        try{
            blockingStub.sendResponse(response);
        }
        catch (StatusRuntimeException sre){


        } finally {
            return;
        }
    }

}

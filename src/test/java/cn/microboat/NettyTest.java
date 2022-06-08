package cn.microboat;

import cn.microboat.dto.RpcRequest;
import cn.microboat.dto.RpcResponse;
import org.junit.Test;

public class NettyTest {

    @Test
    public void testServer() {
        NettyServer nettyServer = new NettyServer(8889);
        nettyServer.run();
    }

    @Test
    public void testClient() {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("hello")
                .build();

        NettyClient nettyClient = new NettyClient("127.0.0.1", 8889);

        for (int i = 0; i < 3; i++) {
            nettyClient.sendMessage(rpcRequest);
        }

        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
        System.out.println(rpcResponse.toString());
    }
}

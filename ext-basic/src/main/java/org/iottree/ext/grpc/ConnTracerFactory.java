package org.iottree.ext.grpc;

import io.grpc.Metadata;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;

public class ConnTracerFactory extends ServerStreamTracer.Factory {
    
    @Override
    public ServerStreamTracer newServerStreamTracer(String fullMethodName, Metadata headers)
    {
        return new ServerStreamTracer()
        {
            private final long startTime = System.currentTimeMillis();
            private String clientId;
            
            @Override
            public void serverCallStarted(ServerCallInfo<?, ?> callInfo)
            {
                this.clientId = extractClientId(headers);
                System.out.println("客户端 " + clientId + " 开始调用: " + fullMethodName);
                
                ConnMonitor.registerConnection(clientId, callInfo.getAttributes());
            }
            
            @Override
            public void streamClosed(Status status) {
                long duration = System.currentTimeMillis() - startTime;
                
                if (status.isOk()) {
                    System.out.println("客户端 " + clientId + " 调用完成，耗时: " + duration + "ms");
                } else {
                    System.out.println("客户端 " + clientId + " 调用异常: " + status + "，耗时: " + duration + "ms");
                }
                
                // 可以在这里记录连接结束
                ConnMonitor.unregisterConnection(clientId, status);
            }
            
            @Override
            public void outboundMessage(int seqNo) {
            }
            
            @Override
            public void inboundMessage(int seqNo) {
            }
            
            private String extractClientId(Metadata headers) {
                // 从headers中提取clientId
                String clientId = headers.get(Metadata.Key.of("client-id", Metadata.ASCII_STRING_MARSHALLER));
                return clientId != null ? clientId : "unknown";
            }
        };
    }
}

package org.iottree.ext.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class IOTTreeServerGrpc {

  private IOTTreeServerGrpc() {}

  public static final java.lang.String SERVICE_NAME = "IOTTreeServer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.PrjList> getListPrjsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listPrjs",
      requestType = org.iottree.ext.grpc.RtSyn.ReqClient.class,
      responseType = org.iottree.ext.grpc.RtSyn.PrjList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.PrjList> getListPrjsMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.PrjList> getListPrjsMethod;
    if ((getListPrjsMethod = IOTTreeServerGrpc.getListPrjsMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getListPrjsMethod = IOTTreeServerGrpc.getListPrjsMethod) == null) {
          IOTTreeServerGrpc.getListPrjsMethod = getListPrjsMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.PrjList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "listPrjs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqClient.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.PrjList.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("listPrjs"))
              .build();
        }
      }
    }
    return getListPrjsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqPrj,
      org.iottree.ext.grpc.RtSyn.TagList> getListTagsInPrjMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "listTagsInPrj",
      requestType = org.iottree.ext.grpc.RtSyn.ReqPrj.class,
      responseType = org.iottree.ext.grpc.RtSyn.TagList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqPrj,
      org.iottree.ext.grpc.RtSyn.TagList> getListTagsInPrjMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqPrj, org.iottree.ext.grpc.RtSyn.TagList> getListTagsInPrjMethod;
    if ((getListTagsInPrjMethod = IOTTreeServerGrpc.getListTagsInPrjMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getListTagsInPrjMethod = IOTTreeServerGrpc.getListTagsInPrjMethod) == null) {
          IOTTreeServerGrpc.getListTagsInPrjMethod = getListTagsInPrjMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqPrj, org.iottree.ext.grpc.RtSyn.TagList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "listTagsInPrj"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqPrj.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.TagList.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("listTagsInPrj"))
              .build();
        }
      }
    }
    return getListTagsInPrjMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagPaths,
      org.iottree.ext.grpc.RtSyn.TagList> getSetSynTagPathMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "setSynTagPath",
      requestType = org.iottree.ext.grpc.RtSyn.ReqTagPaths.class,
      responseType = org.iottree.ext.grpc.RtSyn.TagList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagPaths,
      org.iottree.ext.grpc.RtSyn.TagList> getSetSynTagPathMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagPaths, org.iottree.ext.grpc.RtSyn.TagList> getSetSynTagPathMethod;
    if ((getSetSynTagPathMethod = IOTTreeServerGrpc.getSetSynTagPathMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getSetSynTagPathMethod = IOTTreeServerGrpc.getSetSynTagPathMethod) == null) {
          IOTTreeServerGrpc.getSetSynTagPathMethod = getSetSynTagPathMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqTagPaths, org.iottree.ext.grpc.RtSyn.TagList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "setSynTagPath"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqTagPaths.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.TagList.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("setSynTagPath"))
              .build();
        }
      }
    }
    return getSetSynTagPathMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.TagList> getGetSynTagPathMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getSynTagPath",
      requestType = org.iottree.ext.grpc.RtSyn.ReqClient.class,
      responseType = org.iottree.ext.grpc.RtSyn.TagList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.TagList> getGetSynTagPathMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.TagList> getGetSynTagPathMethod;
    if ((getGetSynTagPathMethod = IOTTreeServerGrpc.getGetSynTagPathMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getGetSynTagPathMethod = IOTTreeServerGrpc.getGetSynTagPathMethod) == null) {
          IOTTreeServerGrpc.getGetSynTagPathMethod = getGetSynTagPathMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.TagList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getSynTagPath"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqClient.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.TagList.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("getSynTagPath"))
              .build();
        }
      }
    }
    return getGetSynTagPathMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.TagSynVals> getStartSynMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "startSyn",
      requestType = org.iottree.ext.grpc.RtSyn.ReqClient.class,
      responseType = org.iottree.ext.grpc.RtSyn.TagSynVals.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.TagSynVals> getStartSynMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.TagSynVals> getStartSynMethod;
    if ((getStartSynMethod = IOTTreeServerGrpc.getStartSynMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getStartSynMethod = IOTTreeServerGrpc.getStartSynMethod) == null) {
          IOTTreeServerGrpc.getStartSynMethod = getStartSynMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.TagSynVals>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "startSyn"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqClient.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.TagSynVals.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("startSyn"))
              .build();
        }
      }
    }
    return getStartSynMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.Result> getStopSynMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "stopSyn",
      requestType = org.iottree.ext.grpc.RtSyn.ReqClient.class,
      responseType = org.iottree.ext.grpc.RtSyn.Result.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient,
      org.iottree.ext.grpc.RtSyn.Result> getStopSynMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.Result> getStopSynMethod;
    if ((getStopSynMethod = IOTTreeServerGrpc.getStopSynMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getStopSynMethod = IOTTreeServerGrpc.getStopSynMethod) == null) {
          IOTTreeServerGrpc.getStopSynMethod = getStopSynMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqClient, org.iottree.ext.grpc.RtSyn.Result>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "stopSyn"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqClient.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.Result.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("stopSyn"))
              .build();
        }
      }
    }
    return getStopSynMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW,
      org.iottree.ext.grpc.RtSyn.Result> getWriteTagValMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "writeTagVal",
      requestType = org.iottree.ext.grpc.RtSyn.ReqTagW.class,
      responseType = org.iottree.ext.grpc.RtSyn.Result.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW,
      org.iottree.ext.grpc.RtSyn.Result> getWriteTagValMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW, org.iottree.ext.grpc.RtSyn.Result> getWriteTagValMethod;
    if ((getWriteTagValMethod = IOTTreeServerGrpc.getWriteTagValMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getWriteTagValMethod = IOTTreeServerGrpc.getWriteTagValMethod) == null) {
          IOTTreeServerGrpc.getWriteTagValMethod = getWriteTagValMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqTagW, org.iottree.ext.grpc.RtSyn.Result>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "writeTagVal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqTagW.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.Result.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("writeTagVal"))
              .build();
        }
      }
    }
    return getWriteTagValMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW,
      org.iottree.ext.grpc.RtSyn.Result> getSetTagValMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "setTagVal",
      requestType = org.iottree.ext.grpc.RtSyn.ReqTagW.class,
      responseType = org.iottree.ext.grpc.RtSyn.Result.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW,
      org.iottree.ext.grpc.RtSyn.Result> getSetTagValMethod() {
    io.grpc.MethodDescriptor<org.iottree.ext.grpc.RtSyn.ReqTagW, org.iottree.ext.grpc.RtSyn.Result> getSetTagValMethod;
    if ((getSetTagValMethod = IOTTreeServerGrpc.getSetTagValMethod) == null) {
      synchronized (IOTTreeServerGrpc.class) {
        if ((getSetTagValMethod = IOTTreeServerGrpc.getSetTagValMethod) == null) {
          IOTTreeServerGrpc.getSetTagValMethod = getSetTagValMethod =
              io.grpc.MethodDescriptor.<org.iottree.ext.grpc.RtSyn.ReqTagW, org.iottree.ext.grpc.RtSyn.Result>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "setTagVal"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.ReqTagW.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iottree.ext.grpc.RtSyn.Result.getDefaultInstance()))
              .setSchemaDescriptor(new IOTTreeServerMethodDescriptorSupplier("setTagVal"))
              .build();
        }
      }
    }
    return getSetTagValMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IOTTreeServerStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerStub>() {
        @java.lang.Override
        public IOTTreeServerStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IOTTreeServerStub(channel, callOptions);
        }
      };
    return IOTTreeServerStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static IOTTreeServerBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerBlockingV2Stub>() {
        @java.lang.Override
        public IOTTreeServerBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IOTTreeServerBlockingV2Stub(channel, callOptions);
        }
      };
    return IOTTreeServerBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IOTTreeServerBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerBlockingStub>() {
        @java.lang.Override
        public IOTTreeServerBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IOTTreeServerBlockingStub(channel, callOptions);
        }
      };
    return IOTTreeServerBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IOTTreeServerFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<IOTTreeServerFutureStub>() {
        @java.lang.Override
        public IOTTreeServerFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new IOTTreeServerFutureStub(channel, callOptions);
        }
      };
    return IOTTreeServerFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void listPrjs(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.PrjList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListPrjsMethod(), responseObserver);
    }

    /**
     */
    default void listTagsInPrj(org.iottree.ext.grpc.RtSyn.ReqPrj request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListTagsInPrjMethod(), responseObserver);
    }

    /**
     */
    default void setSynTagPath(org.iottree.ext.grpc.RtSyn.ReqTagPaths request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetSynTagPathMethod(), responseObserver);
    }

    /**
     */
    default void getSynTagPath(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetSynTagPathMethod(), responseObserver);
    }

    /**
     */
    default void startSyn(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagSynVals> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStartSynMethod(), responseObserver);
    }

    /**
     */
    default void stopSyn(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStopSynMethod(), responseObserver);
    }

    /**
     * <pre>
     * write tag
     * </pre>
     */
    default void writeTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteTagValMethod(), responseObserver);
    }

    /**
     * <pre>
     * in memory
     * </pre>
     */
    default void setTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSetTagValMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service IOTTreeServer.
   */
  public static abstract class IOTTreeServerImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return IOTTreeServerGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service IOTTreeServer.
   */
  public static final class IOTTreeServerStub
      extends io.grpc.stub.AbstractAsyncStub<IOTTreeServerStub> {
    private IOTTreeServerStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IOTTreeServerStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IOTTreeServerStub(channel, callOptions);
    }

    /**
     */
    public void listPrjs(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.PrjList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListPrjsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void listTagsInPrj(org.iottree.ext.grpc.RtSyn.ReqPrj request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListTagsInPrjMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setSynTagPath(org.iottree.ext.grpc.RtSyn.ReqTagPaths request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetSynTagPathMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getSynTagPath(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetSynTagPathMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void startSyn(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagSynVals> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStartSynMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void stopSyn(org.iottree.ext.grpc.RtSyn.ReqClient request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getStopSynMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * write tag
     * </pre>
     */
    public void writeTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteTagValMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * in memory
     * </pre>
     */
    public void setTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request,
        io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSetTagValMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service IOTTreeServer.
   */
  public static final class IOTTreeServerBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<IOTTreeServerBlockingV2Stub> {
    private IOTTreeServerBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IOTTreeServerBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IOTTreeServerBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.PrjList listPrjs(org.iottree.ext.grpc.RtSyn.ReqClient request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListPrjsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList listTagsInPrj(org.iottree.ext.grpc.RtSyn.ReqPrj request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getListTagsInPrjMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList setSynTagPath(org.iottree.ext.grpc.RtSyn.ReqTagPaths request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSetSynTagPathMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList getSynTagPath(org.iottree.ext.grpc.RtSyn.ReqClient request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetSynTagPathMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, org.iottree.ext.grpc.RtSyn.TagSynVals>
        startSyn(org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getStartSynMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.Result stopSyn(org.iottree.ext.grpc.RtSyn.ReqClient request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getStopSynMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * write tag
     * </pre>
     */
    public org.iottree.ext.grpc.RtSyn.Result writeTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getWriteTagValMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * in memory
     * </pre>
     */
    public org.iottree.ext.grpc.RtSyn.Result setTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSetTagValMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service IOTTreeServer.
   */
  public static final class IOTTreeServerBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<IOTTreeServerBlockingStub> {
    private IOTTreeServerBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IOTTreeServerBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IOTTreeServerBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.PrjList listPrjs(org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListPrjsMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList listTagsInPrj(org.iottree.ext.grpc.RtSyn.ReqPrj request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListTagsInPrjMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList setSynTagPath(org.iottree.ext.grpc.RtSyn.ReqTagPaths request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetSynTagPathMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.TagList getSynTagPath(org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetSynTagPathMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.iottree.ext.grpc.RtSyn.TagSynVals> startSyn(
        org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStartSynMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iottree.ext.grpc.RtSyn.Result stopSyn(org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getStopSynMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * write tag
     * </pre>
     */
    public org.iottree.ext.grpc.RtSyn.Result writeTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getWriteTagValMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * in memory
     * </pre>
     */
    public org.iottree.ext.grpc.RtSyn.Result setTagVal(org.iottree.ext.grpc.RtSyn.ReqTagW request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSetTagValMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service IOTTreeServer.
   */
  public static final class IOTTreeServerFutureStub
      extends io.grpc.stub.AbstractFutureStub<IOTTreeServerFutureStub> {
    private IOTTreeServerFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IOTTreeServerFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new IOTTreeServerFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.PrjList> listPrjs(
        org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListPrjsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.TagList> listTagsInPrj(
        org.iottree.ext.grpc.RtSyn.ReqPrj request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListTagsInPrjMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.TagList> setSynTagPath(
        org.iottree.ext.grpc.RtSyn.ReqTagPaths request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetSynTagPathMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.TagList> getSynTagPath(
        org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetSynTagPathMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.Result> stopSyn(
        org.iottree.ext.grpc.RtSyn.ReqClient request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getStopSynMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * write tag
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.Result> writeTagVal(
        org.iottree.ext.grpc.RtSyn.ReqTagW request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteTagValMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * in memory
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iottree.ext.grpc.RtSyn.Result> setTagVal(
        org.iottree.ext.grpc.RtSyn.ReqTagW request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSetTagValMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_LIST_PRJS = 0;
  private static final int METHODID_LIST_TAGS_IN_PRJ = 1;
  private static final int METHODID_SET_SYN_TAG_PATH = 2;
  private static final int METHODID_GET_SYN_TAG_PATH = 3;
  private static final int METHODID_START_SYN = 4;
  private static final int METHODID_STOP_SYN = 5;
  private static final int METHODID_WRITE_TAG_VAL = 6;
  private static final int METHODID_SET_TAG_VAL = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_LIST_PRJS:
          serviceImpl.listPrjs((org.iottree.ext.grpc.RtSyn.ReqClient) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.PrjList>) responseObserver);
          break;
        case METHODID_LIST_TAGS_IN_PRJ:
          serviceImpl.listTagsInPrj((org.iottree.ext.grpc.RtSyn.ReqPrj) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList>) responseObserver);
          break;
        case METHODID_SET_SYN_TAG_PATH:
          serviceImpl.setSynTagPath((org.iottree.ext.grpc.RtSyn.ReqTagPaths) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList>) responseObserver);
          break;
        case METHODID_GET_SYN_TAG_PATH:
          serviceImpl.getSynTagPath((org.iottree.ext.grpc.RtSyn.ReqClient) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagList>) responseObserver);
          break;
        case METHODID_START_SYN:
          serviceImpl.startSyn((org.iottree.ext.grpc.RtSyn.ReqClient) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.TagSynVals>) responseObserver);
          break;
        case METHODID_STOP_SYN:
          serviceImpl.stopSyn((org.iottree.ext.grpc.RtSyn.ReqClient) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result>) responseObserver);
          break;
        case METHODID_WRITE_TAG_VAL:
          serviceImpl.writeTagVal((org.iottree.ext.grpc.RtSyn.ReqTagW) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result>) responseObserver);
          break;
        case METHODID_SET_TAG_VAL:
          serviceImpl.setTagVal((org.iottree.ext.grpc.RtSyn.ReqTagW) request,
              (io.grpc.stub.StreamObserver<org.iottree.ext.grpc.RtSyn.Result>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getListPrjsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqClient,
              org.iottree.ext.grpc.RtSyn.PrjList>(
                service, METHODID_LIST_PRJS)))
        .addMethod(
          getListTagsInPrjMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqPrj,
              org.iottree.ext.grpc.RtSyn.TagList>(
                service, METHODID_LIST_TAGS_IN_PRJ)))
        .addMethod(
          getSetSynTagPathMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqTagPaths,
              org.iottree.ext.grpc.RtSyn.TagList>(
                service, METHODID_SET_SYN_TAG_PATH)))
        .addMethod(
          getGetSynTagPathMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqClient,
              org.iottree.ext.grpc.RtSyn.TagList>(
                service, METHODID_GET_SYN_TAG_PATH)))
        .addMethod(
          getStartSynMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqClient,
              org.iottree.ext.grpc.RtSyn.TagSynVals>(
                service, METHODID_START_SYN)))
        .addMethod(
          getStopSynMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqClient,
              org.iottree.ext.grpc.RtSyn.Result>(
                service, METHODID_STOP_SYN)))
        .addMethod(
          getWriteTagValMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqTagW,
              org.iottree.ext.grpc.RtSyn.Result>(
                service, METHODID_WRITE_TAG_VAL)))
        .addMethod(
          getSetTagValMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.iottree.ext.grpc.RtSyn.ReqTagW,
              org.iottree.ext.grpc.RtSyn.Result>(
                service, METHODID_SET_TAG_VAL)))
        .build();
  }

  private static abstract class IOTTreeServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IOTTreeServerBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.iottree.ext.grpc.RtSyn.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("IOTTreeServer");
    }
  }

  private static final class IOTTreeServerFileDescriptorSupplier
      extends IOTTreeServerBaseDescriptorSupplier {
    IOTTreeServerFileDescriptorSupplier() {}
  }

  private static final class IOTTreeServerMethodDescriptorSupplier
      extends IOTTreeServerBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    IOTTreeServerMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IOTTreeServerGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IOTTreeServerFileDescriptorSupplier())
              .addMethod(getListPrjsMethod())
              .addMethod(getListTagsInPrjMethod())
              .addMethod(getSetSynTagPathMethod())
              .addMethod(getGetSynTagPathMethod())
              .addMethod(getStartSynMethod())
              .addMethod(getStopSynMethod())
              .addMethod(getWriteTagValMethod())
              .addMethod(getSetTagValMethod())
              .build();
        }
      }
    }
    return result;
  }
}

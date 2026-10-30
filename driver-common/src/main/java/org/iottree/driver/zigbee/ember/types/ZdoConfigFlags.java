package org.iottree.driver.zigbee.ember.types;

public interface ZdoConfigFlags
{
	// Flags for controlling which incoming ZDO requests are passed to the
    // application. To see if the application is required to send a ZDO response
    // to an incoming message, the application must check the APS options
    // bitfield within the incomingMessageHandler callback to see if the
    // APS_OPTION_ZDO_RESPONSE_REQUIRED flag is set.

    // Set this flag in order to receive supported ZDO request messages via the
    // incomingMessageHandler callback. A supported ZDO request is one that is
    // handled by the EmberZNet stack. The stack will continue to handle the
    // request and send the appropriate ZDO response even if this configuration
    // option is enabled.
    public static int APP_RECEIVES_SUPPORTED_ZDO_REQUESTS = 0x01;
    // Set this flag in order to receive unsupported ZDO request messages via
    // the incomingMessageHandler callback. An unsupported ZDO request is one
    // that is not handled by the EmberZNet stack, other than to send a 'not
    // supported' ZDO response. If this configuration option is enabled, the
    // stack will no longer send any ZDO response, and it is the application's
    // responsibility to do so.
    public static int  APP_HANDLES_UNSUPPORTED_ZDO_REQUESTS = 0x02;
    // Set this flag in order to receive the following ZDO request messages via
    // the incomingMessageHandler callback: SIMPLE_DESCRIPTOR_REQUEST,
    // MATCH_DESCRIPTORS_REQUEST, and ACTIVE_ENDPOINTS_REQUEST. If this
    // configuration option is enabled, the stack will no longer send any ZDO
    // response for these requests, and it is the application's responsibility
    // to do so.
    public static int  APP_HANDLES_ZDO_ENDPOINT_REQUESTS = 0x04;
    // Set this flag in order to receive the following ZDO request messages via
    // the incomingMessageHandler callback: BINDING_TABLE_REQUEST, BIND_REQUEST,
    // and UNBIND_REQUEST. If this configuration option is enabled, the stack
    // will no longer send any ZDO response for these requests, and it is the
    // application's responsibility to do so.
    public static int  APP_HANDLES_ZDO_BINDING_REQUESTS = 0x08;
}

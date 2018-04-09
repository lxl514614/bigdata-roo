package com.bigdata.server;

import jdk.management.resource.ResourceId;

public @interface RpcService {
    ResourceId value();
}

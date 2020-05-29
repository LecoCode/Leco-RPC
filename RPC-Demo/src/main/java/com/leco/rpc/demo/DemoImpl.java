package com.leco.rpc.demo;

import com.leco.rpc.core.annotations.RpcService;

/**
 * @创建人 leco
 * @创建时间 2018/7/31
 */

@RpcService
public class DemoImpl implements IDemo {
    public String getName() {
        return "hello Leco RPC!1";
    }
}

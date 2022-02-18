package com.sainnt.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public abstract class Request {
    public abstract ChannelFuture perform(Channel channel);
}

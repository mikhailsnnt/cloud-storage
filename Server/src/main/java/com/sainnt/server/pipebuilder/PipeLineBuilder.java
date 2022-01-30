package com.sainnt.server.pipebuilder;

import com.sainnt.server.entity.User;
import io.netty.channel.ChannelPipeline;

public interface PipeLineBuilder {
    void buildUserPipeLine(ChannelPipeline pipeline, User user);
}

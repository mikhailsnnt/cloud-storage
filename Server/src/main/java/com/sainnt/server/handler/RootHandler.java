package com.sainnt.server.handler;

import com.sainnt.server.util.HibernateUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.hibernate.Session;

public class RootHandler extends ChannelInboundHandlerAdapter {
    private Session session;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        session = HibernateUtil.getCurrentSession();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        session.close();
    }


}

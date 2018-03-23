package org.xiaoheshan.hallo.boxing.pi.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xiaoheshan.hallo.boxing.pi.config.ServerProperty;
import org.xiaoheshan.hallo.boxing.pi.looper.ServerMessageLooper;
import org.xiaoheshan.hallo.boxing.pi.util.ConsoleUtil;
import org.xiaoheshan.hallo.boxing.pi.util.ThreadSleepUtil;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

import static org.xiaoheshan.hallo.boxing.pi.constant.SystemConstant.SERVER_COMMAND_PREFIX;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
@Component
public class ServerConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnector.class);

    private final ServerProperty property;

    private Channel serverChannel;

    @Autowired
    public ServerConnector(ServerProperty property) {
        this.property = property;
    }

    @PostConstruct
    private void init() {
        new Thread(this::connect).start();
    }

    public synchronized boolean send(String message) {
        if (message == null || message.isEmpty()) {
            return true;
        }
        ConsoleUtil.get().println("向服务端发送消息：" + message);
        for (int i = 0; i < 2; i++) {
            if (serverChannel.isWritable()) {
                serverChannel.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                ConsoleUtil.get().println("服务端发送成功");
                return true;
            }
            ThreadSleepUtil.sleep(1000);
        }
        LOGGER.warn("发送消息超时：" + message);
        return false;
    }

    private void connect() {
        ConsoleUtil.get().box("连接远程服务器：" + property.getName(),
                "服务器地址：" + property.getIp(),
                "服务器端口：" + property.getPort());
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture future = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(property.getIp(), property.getPort()))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 90))
                                    .addLast(new StringDecoder())
                                    .addLast(new ClientHandler());
                        }
                    })
                    .connect()
                    .sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException ignored) {
            }
        }

    }

    private class ClientHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            serverChannel = ctx.channel();
            ConsoleUtil.get().println("连接远程服务器成功");
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            if (msg.startsWith(SERVER_COMMAND_PREFIX)) {
                ConsoleUtil.get().println("收到服务端消息：" + msg);
                ServerMessageLooper.offerMessage(msg.substring(SERVER_COMMAND_PREFIX.length()));
                return;
            }
            LOGGER.warn("接收到未知消息：" + msg);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.ALL_IDLE) {
                    ctx.writeAndFlush("HB+HEART_BEAT");
                }
            }
        }
    }

}

package com.lagou.service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private Serializer serializer;

    private static final int HEAD_LENGTH = 4;

    public RpcDecoder(Class<?> clazz, Serializer serializer) {

        this.clazz = clazz;

        this.serializer = serializer;

    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (clazz == null || (byteBuf.readableBytes() < HEAD_LENGTH)) return;

        // 标记当前读的index
        byteBuf.markReaderIndex();

        // 通信的消息长度 --- readInt() 方法会使readIndex增加4
        int dataSize = byteBuf.readInt();

        // 读取到的消息体长度为0， 关闭连接
        if (dataSize < 0) {
            channelHandlerContext.close();
        }

        // 读取的消息长度小于传过来的消息长度，重置index
        if (byteBuf.readableBytes() < dataSize) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] body = new byte[dataSize];
        byteBuf.readBytes(body);
        list.add(new String(body));
    }
}



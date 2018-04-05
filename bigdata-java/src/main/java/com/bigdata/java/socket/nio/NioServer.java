package com.bigdata.java.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class NioServer {

    private ByteBuffer readBuffer ;

    private Selector selector;

    public static void main(String[] args) {

        NioServer server = new NioServer();
        // 初始化服服务
        server.init();

        System.out.println("start server port:8383 ....");

        server.listen();
    }

    private void listen() {

        while (true) {

            try {

                System.out.println("listening ....");

                // 去询问一次选择器
                selector.select();

                Iterator<SelectionKey> item = selector.selectedKeys().iterator();

                while (item.hasNext()) {
                    // 遍历一个事件
                    SelectionKey key = item.next();
                    // 确保事件不重复处理, 先删除掉
                    item.remove();

                    // 处理事件
                    handleKey(key);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException, ClosedChannelException {
        SocketChannel channel = null;
        try {
            // 判断事件是否是接收事件
            if (key.isAcceptable()) {

                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                    // 接收事件
                    channel = serverSocketChannel.accept();

                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
            }
            // 如果为读事件
            else if (key.isReadable()) {

                channel = (SocketChannel) key.channel();

                // 确保读入buffer中数据准确, 先清空buffer
                readBuffer.clear();

                int count = channel.read(readBuffer);

                // 此处进行校验,如果客户端连接断开的话,count 会返回-1
                if (count > 0 ) {
                    // 一定要调用flip函数, 否则会读取错误数据
                    readBuffer.flip();

                    /*使用CharBuffer配合取出正确的数据
                    String question = new String(readBuffer.array());
                    可能会出错，因为前面readBuffer.clear();并未真正清理数据
                    只是重置缓冲区的position, limit, mark，
                    而readBuffer.array()会返回整个缓冲区的内容。
                    decode方法只取readBuffer的position到limit数据。
                    例如，上一次读取到缓冲区的是"where", clear后position为0，limit为 1024，
                    再次读取“bye"到缓冲区后，position为3，limit不变，
                    flip后position为0，limit为3，前三个字符被覆盖了，但"re"还存在缓冲区中，
                    所以 new String(readBuffer.array()) 返回 "byere",
                    而decode(readBuffer)返回"bye"。
                    */
                    //CharBuffer charBuffer = CharsetHelper.decode(readBuffer);
                    CharBuffer charBuffer = Charset.forName("UTF-8").newDecoder().decode(readBuffer);

                    String question = charBuffer.toString();

                    String answer = getAnswer(question);

                    channel.write(Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(answer)));
                }
                else {
                    channel.close();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();

            if ( null != channel ) {
                channel.close();
            }
        }
    }

    private String getAnswer(String question) {
        String answer = null;

        switch(question){
            case "who":
                answer = "我是Lee\n";
                break;
            case "what":
                answer = "我是来帮你解闷的\n";
                break;
            case "where":
                answer = "我来自外太空\n";
                break;
            case "hi":
                answer = "hello\n";
                break;
            case "bye":
                answer = "88\n";
                break;
            default:
                answer = "请输入 who， 或者what， 或者where\n";
        }

        return answer;
    }

    private void init() {

        readBuffer = ByteBuffer.allocate(1024);

        ServerSocketChannel channel;

        try {
            // 创建一个socket通道
            channel = ServerSocketChannel.open();
            // 设置为非阻塞方式
            channel.configureBlocking(false);
            // 将通道绑定在服务器ip地址和某个端口上
            channel.socket().bind(new InetSocketAddress(8383));

            // 打开一个多路复用器 (管家)
            selector = Selector.open();

            // 将建立好的通道注册到多路复用器上, 并注册一个监听的事件
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockingServer {

    public static void main(String[] args) {
        int port = 12345;

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Non-Blocking Server is running and waiting for clients...");

            while (true) {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        handleAcceptable(key, selector);
                    } else if (key.isReadable()) {
                        handleReadable(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAcceptable(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Client connected: " + clientChannel.getRemoteAddress());
    }

    private static void handleReadable(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            return;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        System.out.println("Received from client: " + new String(data));
    }
}

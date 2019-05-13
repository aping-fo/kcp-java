import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class Server {
    static int i = 0;
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(9090);


        KCP kcp = new KCP(1000) {
            @Override
            protected void output(byte[] buffer, int size) {
                try {
                    DatagramPacket packetBody = new DatagramPacket(buffer, size, clientip);
                    socket.send(packetBody);
                } catch (Exception e) {

                }
            }
        };
        kcp.WndSize(128, 128);
        while (true) {
            kcp.Update(System.currentTimeMillis());
            //头
//            byte[] buf = new byte[4];
//            DatagramPacket datagramPacketHead = new DatagramPacket(buf, buf.length);
//            socket.receive(datagramPacketHead);
//            int len = BytesUtils.bytesToInt(buf);

            byte[] bufBody = new byte[1400]; //最大
            DatagramPacket datagramPacket = new DatagramPacket(bufBody, bufBody.length);
            socket.receive(datagramPacket);
            kcp.Input(bufBody);
            kcp.flush();
            kcp.clientip = datagramPacket.getSocketAddress();

            int offset = 0;
            long conv_ = KCP.ikcp_decode32u(bufBody, offset);
            offset += 4;

            int cmd = KCP.ikcp_decode8u(bufBody, offset);
            offset += 1;
            //frg = ikcp_decode8u(data, offset);
            offset += 1;
            //wnd = ikcp_decode16u(data, offset);
            offset += 2;
            //ts = ikcp_decode32u(data, offset);
            offset += 4;
            long sn = KCP.ikcp_decode32u(bufBody, offset);
            offset += 4;
            //una = ikcp_decode32u(data, offset);
            offset += 4;
            long length = KCP.ikcp_decode32u(bufBody, offset);
            offset += 4;

            int s = kcp.PeekSize();
            if (s > 0) {
                byte[] bufBody1 = new byte[s];


                int size = kcp.Recv(bufBody1);
                System.out.println("接收端接收到的数据size：" + size);
                System.out.println("接收端接收到的数据：" + new String(bufBody1));
                System.out.println("receive阻塞了我，哈哈");
                i ++;
                System.out.println(i);
            }
        }
    }
}

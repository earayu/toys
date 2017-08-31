package cn.eovie.socks5.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by earayu on 2017/8/31.
 */
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "socks5")
public class LocalServerConfig {

    private LocalServer localServer = new LocalServer();
    private RemoteServer remoteServer = new RemoteServer();


    public LocalServer getLocalServer() {
        return localServer;
    }

    public RemoteServer getRemoteServer() {
        return remoteServer;
    }

    public static class RemoteServer{
        private String addr;
        private int port;
        private int timeout;
        private boolean keepalive;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public boolean isKeepalive() {
            return keepalive;
        }

        public void setKeepalive(boolean keepalive) {
            this.keepalive = keepalive;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class LocalServer{
        private String addr;
        private int port;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }



}

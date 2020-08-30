import server.api.LoadBalancer;
import server.implementation.LoadBalancerImpl;

public class ServerMain {
    public static void main(String[] args) {
        System.out.println(Thread.currentThread());
        LoadBalancer loadBalancer = new LoadBalancerImpl();
        loadBalancer.initialize();
    }
}

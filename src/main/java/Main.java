public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        System.out.println("waiting connections....");
        server.listenToThePort(9999);
    }
}


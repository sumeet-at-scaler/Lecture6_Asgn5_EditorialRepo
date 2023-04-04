import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Node root = new Node(10);
        root.left = new Node(20);
        root.right = new Node(30);
        root.left.left = new Node(40);
        root.left.right = new Node(50);
        root.right.left = new Node(60);
        root.right.right = new Node(70);

        ExecutorService exs = Executors.newFixedThreadPool(10);
        TreeSizeCalculator treeSizeCalculator = new TreeSizeCalculator(root, exs);
        Future<Integer> size = exs.submit(treeSizeCalculator);
        System.out.println(size.get());

        exs.shutdown();
    }
}
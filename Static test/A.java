import java.util.*;

public class A {
    public static int num;

    public static void main(String[] args){
        List<Integer> queue = new ArrayList<>();
        queue.add(10);
        queue.add(20);
        queue.add(30);
        queue.add(40);
        System.out.println(queue.toString());
        System.out.println(queue.get(0));
        System.out.println(queue.toString());
        queue.set(0, queue.get(0) - 6);
        System.out.println(queue.toString());
        queue.remove(0);
        System.out.println(queue.toString());
    }
}
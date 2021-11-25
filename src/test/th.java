package test;

public class th extends Thread {
    public int i;

    public th(int num) {
      i = num;
    }

    public void run() {
//        for(i=0;i<100;i++) {
            System.out.println(i);
//        }
    }
}

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        /*
        this is useful with swing ui and this makes GUI updates more thread safe
         */
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppGui().setVisible(true);
//                System.out.println(AppApi.getLocationData("Berlin"));
//                System.out.println(AppApi.getcurrentTime());
            }
        });
    }
}
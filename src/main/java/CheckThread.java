import command.CheckCommand;
import view.PaintInformation;
import world.CheckMap;
import world.Request;
import world.UserData;

import java.util.Random;

public class CheckThread extends Thread {

    private UserData user;
    private CheckMap checkMap;
    private double r;
    private double x, y;
    private int t;

    private boolean run;

    private Controller controller;


    public CheckThread(UserData user, CheckMap checkMap, Controller controller, double x, double y, double r, int t) {
        this.user = user;
        this.checkMap = checkMap;
        this.controller = controller;
        this.x = x;
        this.y = y;
        this.r = r;
        this.t = t;

        run = true;

        setDaemon(true);
    }

    @Override
    public void run() {
        while (run) {
            try {

                Request request = new Request(x, y, r, user);
                System.out.println("Thread running" + user.getName() + " " + user.getTags());
                controller.getInvoker().doCommand(new CheckCommand(request));
                controller.paintInformationMap.put(getName(), new PaintInformation(x, y, r));
                sleep(t);

                Random random = new Random();
                int dx = random.nextInt(30) - 15;
                int dy = random.nextInt(30) - 15;
                int dr = random.nextInt(30) - 15;
                if (x + dx < 0 || x + dx > 500) {
                    x -= dx;
                } else {
                    x += dx;
                }

                if (y + dy < 0 || y + dy > 500) {
                    y -= dy;
                } else {
                    y += dy;
                }

                if (r + dr < 90 || r + dr > 250) {
                    r -= dr;
                } else {
                    r += dr;
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopCheckIn() {
        run = false;
    }

    public double getR() {
        return r;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

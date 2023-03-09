package monalisabot;

public class StopFromKeyboard implements Runnable {
    Drawer drawer;
    StopFromKeyboard(Drawer drawer)
    {
        this.drawer = drawer;
    }

    public native boolean isKeyPressed();

    public void run()
    {
        isKeyPressed();
        try {
            while (drawer.running)
                if (isKeyPressed()) {
                    drawer.running = false;

                }
            Thread.sleep(100);
        }
        catch (Exception e)
        {

        }
    }
}

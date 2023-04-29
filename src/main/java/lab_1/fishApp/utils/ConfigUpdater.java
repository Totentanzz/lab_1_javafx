package lab_1.fishApp.utils;

public class ConfigUpdater {

    private Runnable updateTask;

    public ConfigUpdater(Runnable updateTask) {
        this.updateTask = updateTask;
    }

    public void update() {
        updateTask.run();
    }

}

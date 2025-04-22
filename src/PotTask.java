import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.TimerTask;

public class PotTask extends TimerTask {
    final private SSD1306 display;
    final private Pin Pot;
    final private Pin Pump;


    //Constructor
    public PotTask(SSD1306 display, Pin Pot, Pin Pump) {
        this.display = display;
        this.Pot = Pot;
        this.Pump = Pump;
    }

    @Override
    public void run() {
        try{
            display.getCanvas().clear();
            long MoistureValue = Pot.getValue();
            MainProject.MoistureValue = MoistureValue;
            String string = String.valueOf(MoistureValue);

            if (MoistureValue <= MainProject.DryValue && MoistureValue> MainProject.DampValue) {
                display.getCanvas().drawString(0,0, "Soil is dry. Let's Water it...");
                display.display();
                Pump.setValue(1);
            } else if (MoistureValue <= MainProject.DampValue && MoistureValue > MainProject.WetValue) {
                display.getCanvas().drawString(0,0, "Soil is a little wet. keep watering...");
                display.display();
                Pump.setValue(1);
            } else if (MoistureValue <= MainProject.WetValue) {
                display.getCanvas().drawString(0,0,"Soil is wet enough. Stopped watering.");
                display.display();
                Pump.setValue(0);
            } else {
                display.getCanvas().drawString(0,0,"Error");
                display.display();
            }
            System.out.println(string);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}

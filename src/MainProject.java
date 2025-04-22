import org.firmata4j.firmata.*;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import edu.princeton.cs.introcs.StdDraw;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;

public class MainProject {

    static final int WetValue = 620;
    static final int DryValue = 700;
    static final int DampValue = 640;

    private static SSD1306 display;
    static long MoistureValue;
    static HashMap<Double, Double> MoistureValues = new HashMap<>();


    public static void main(String[] args) throws IOException, InterruptedException{


        int cntr = 1;
        var MyArduinoObject = new FirmataDevice("/dev/cu.usbserial-0001");

        MyArduinoObject.start();
        MyArduinoObject.ensureInitializationIsDone();

        I2CDevice I2cObject = MyArduinoObject.getI2CDevice((byte) 0x3C);

        display = new SSD1306(I2cObject, SSD1306.Size.SSD1306_128_64);
        display.init();

        Pin Pot = MyArduinoObject.getPin(15);
        Pot.setMode(Pin.Mode.ANALOG);

        Pin Pump = MyArduinoObject.getPin(2);
        Pump.setMode(Pin.Mode.OUTPUT);

        var Task = new PotTask(display, Pot, Pump);
        new Timer().schedule(Task,0,100);

        long Start = System.currentTimeMillis();
        PlotGraph(MoistureValues, Start);

        while(true) {
            double MoistureValue = (double) ((MyArduinoObject.getPin(15).getValue() * 5)/1023);
            double TimePassed = (System.currentTimeMillis() - Start)/1000.0;
            MoistureValues.put(TimePassed, MoistureValue);
            StdDraw.text(cntr, (double) Pot.getValue(), ".");
            Thread.sleep(500);
            if(cntr <100) {
                cntr++;
            } else {
                cntr = 1;
            }
        }

    }

    private static void PlotGraph(HashMap<Double, Double> soil, long Start) {
        StdDraw.setCanvasSize(1100, 600);
        StdDraw.setXscale(-10,100);
        StdDraw.setYscale(-450, 1100);
        StdDraw.line(0,0,0,1000);
        StdDraw.line(0,0,100,0);
        StdDraw.text(50,-50,"Time Passed (s)");
        StdDraw.text(-6,500, "Soil Moisture Level");
        StdDraw.text(50,1100,"Time Passed VS Soil Moisture Level");

        StdDraw.text(-3,0,"0");
        StdDraw.text(-3,200,"1");
        StdDraw.text(-3,400,"2");
        StdDraw.text(-3,600,"3");
        StdDraw.text(-3,800,"4");
        StdDraw.text(-3,1000,"5");

        for (double Time:soil.keySet()) {
            double Moisture = soil.get(Time);
            StdDraw.text(Time, Moisture, ".");
        }

    }
}



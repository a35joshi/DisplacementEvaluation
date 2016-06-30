package lab3_205_02.uwaterloo.ca.lab3_205_02;

import android.location.Location;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Guard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import ca.uwaterloo.sensortoy.LineGraphView;
import ca.uwaterloo.sensortoy.InterceptPoint;
import ca.uwaterloo.sensortoy.LabeledPoint;
import ca.uwaterloo.sensortoy.LineSegment;
import ca.uwaterloo.sensortoy.MapLoader;
import ca.uwaterloo.sensortoy.MapView;
import ca.uwaterloo.sensortoy.NavigationalMap;
import ca.uwaterloo.sensortoy.PositionListener;
import ca.uwaterloo.sensortoy.VectorUtils;

public class Lab3_205_02 extends AppCompatActivity implements SensorEventListener {
    Location myCurrentLocation;
    LineGraphView graph;
    MapView mv;
    Sensor LinearAcceleration, Accelerometer, gyroscope, Rotation, Orientation;
    SensorManager sensorManager;
    Button ResetButton;
    TextView StepCountValue, NorthValue, EastValue, AzimuthValue, Direction, NetDisp,GetOrientationAzimuth;
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    int stepcounting = 0;
    int tempsteps=0;
    double azimuth;
    double m_Azimuth;
    double NorthSteps = 0;
    double EastSteps = 0;
    double eastvalue = 0;
    double northvalue = 0;
    double oldm_Azimuth = 0;
    boolean clicked = false;
    boolean success;

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // getorientation();
            return;
        }
       if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
            getorientation();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        Gyroscope(event);
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (event.values[0] > 4 || event.values[1] > 3 || event.values[2] > 3) {
                return;
            }
            float[] pointstoplot = {event.values[0], event.values[1], event.values[2]};
            double total = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
            double tofind = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1]);
            //can also use tofind>1.8, stepcounting/40.
            if (total > 1) {
                int steps=stepcounting/60;
                StepCountValue.setText(Integer.toString(steps));
                stepcounting++;
                double m_AzimuthinRadians = Math.toRadians(m_Azimuth);
                    /*if (oldm_Azimuth == 0) {
                        northvalue = Math.cos(m_AzimuthinRadians);
                        oldm_Azimuth = m_AzimuthinRadians;
                    }*/
                if(tempsteps!=steps)
                {
                    northvalue+= Math.cos(m_AzimuthinRadians);
                    eastvalue += Math.sin(m_AzimuthinRadians);
                    oldm_Azimuth = m_AzimuthinRadians;
                  //  if(northvalue<=steps)
                    NorthValue.setText(Double.toString(northvalue));
                   // if(eastvalue<=steps)
                    EastValue.setText(Double.toString(eastvalue));
                    NetDisp.setText(Double.toString(Math.sqrt(northvalue * northvalue + eastvalue * eastvalue)));
                    tempsteps=steps;
                }

            }

            /*if (total>1) {
                StepCountValue.setText(Integer.toString(stepcounting/60));
                stepcounting++;
            }*/
            graph.addPoint(pointstoplot);
            //getorientation();
        }
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                m_Azimuth = (event.values[0] + 360) % 360;
                AzimuthValue.setText(Double.toString(Math.abs((m_Azimuth))));
                if(m_Azimuth>=0 && m_Azimuth<90)
                {
                    Direction.setText("North");
                }
                if(m_Azimuth>=90 && m_Azimuth<180)
                {
                    Direction.setText("East");
                }
                if(m_Azimuth>=180 && m_Azimuth<270)
                {
                    Direction.setText("South");
                }
                if(m_Azimuth>=270 && m_Azimuth<=360)
                {
                    Direction.setText("West");
                }
               //getorientation(event);
            }
        }
    void Gyroscope(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values.clone();
            getorientation();
           /* for(int i=0; i<3; i++) {
                mGeomagnetic[i] = event.values[i];
            }*/
        }
        else
            return;
    }
    void getorientation()
    {
        if (mGravity != null && mGeomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            success = sensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                 orientation=sensorManager.getOrientation(R, orientation);
                //returns azimuth, pitch and roll(stored in orientation)
                //azimuth is 0 to 2pie or 0 to 360 deg.
                azimuth = orientation[0];
                if(azimuth<0)
                    azimuth=Math.PI+azimuth;
                // myCurrentLocation.getBearing();
                //azimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( R, orientation )[0] ) + 360 ) % 360;
               // GetOrientationAzimuth.setText(Double.toString((Math.toDegrees(azimuth))));
            }
        }
        else
            return;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3_205_02);
        initializeViews();
        registerForContextMenu(mv);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor[] sensor = new Sensor[]{sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),// Sensor- Accelerometer
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),// Sensor- Vector
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)};
        if(sensor!=null) {
            Accelerometer=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,Accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            LinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, LinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
            Rotation=sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this,Rotation,SensorManager.SENSOR_DELAY_NORMAL);
            Orientation=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            sensorManager.registerListener(this,Orientation,SensorManager.SENSOR_DELAY_FASTEST);
        }
        else
            return;
        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click detected!
                clicked = true;
                StepCountValue.setText("0");
                stepcounting=0;
                northvalue=0;
                eastvalue=0;
                NorthValue.setText("0.00");
                EastValue.setText("0.00");
                NetDisp.setText("0.00");
                Direction.setText(" ");
                graph.clearAnimation();
            }
        });

    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, Rotation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, LinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,Orientation,SensorManager.SENSOR_DELAY_FASTEST);
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    public void initializeViews() {
        try {
            LinearLayout layout = ((LinearLayout) findViewById(R.id.layout));
            mv = new MapView(getApplicationContext(),1100,900, 60, 60);
            String output= "<Phone>/Android/data/lab3_205_02.uwaterloo.ca.lab3_205_02/files";
            NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null), "Lab-room-peninsula.svg");
            mv.setMap(map);
            layout.addView(mv);
            mv.setVisibility(View.VISIBLE);
            // create a File object for the parent directory
            //File dir = getDir(Environment.DIRECTORY_DCIM, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
            //dir.getUsableSpace();
            graph = new LineGraphView(getApplicationContext(), 200, Arrays.asList("X", "Y", "Z"));
            layout.addView(graph);
            graph.setVisibility(View.VISIBLE);
            ResetButton = (Button) findViewById(R.id.ResetButton);
            StepCountValue = (TextView) findViewById(R.id.Value);
            NorthValue = (TextView) findViewById(R.id.NorthValue);
            EastValue = (TextView) findViewById(R.id.EastValue);
            AzimuthValue=(TextView)findViewById(R.id.AzimuthValue);
            Direction=(TextView)findViewById(R.id.Direction);
            NetDisp=(TextView)findViewById(R.id.NetDisp);
            GetOrientationAzimuth=(TextView)findViewById(R.id.GetOrientationAzimuth);
            DisplayCleanValues();
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }
    void DisplayCleanValues() {
        StepCountValue.setText("0");
        NorthValue.setText("0.0");
        EastValue.setText("0.0");
        AzimuthValue.setText("0.0");
        NetDisp.setText("0.0");
    }
    @Override
    public  void onCreateContextMenu(ContextMenu menu , View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu , v, menuInfo);
        mv.onCreateContextMenu(menu , v, menuInfo);
    }
    @Override
    public  boolean  onContextItemSelected(MenuItem item)
    {
        return  super.onContextItemSelected(item) ||  mv.onContextItemSelected(item);
    }
}

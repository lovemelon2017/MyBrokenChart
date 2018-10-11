package xiaofu.com.mychart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    MyChartView chartView;
    int [] scores=new int[4];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chartView=findViewById(R.id.chart);

        scores[1]=4;
        scores[1]=1;
        scores[1]=3;
        scores[1]=2;
        chartView.setScore(scores);

    }
}

package xiaofu.com.mychart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    MyChartView chartView;
    int[] scores = new int[4];
    String xT[] = new String[]{"10/10", "11/11", "12/12", "现在"};

    //y轴数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chartView = findViewById(R.id.chart);
        scores[0] = 4;
        scores[1] = 1;
        scores[2] = 3;
        scores[3] = 2;
        chartView.setScore(scores);
        chartView.setMonthText(xT);

    }
}

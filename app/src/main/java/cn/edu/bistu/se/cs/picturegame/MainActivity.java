package cn.edu.bistu.se.cs.picturegame;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
;


public class MainActivity extends AppCompatActivity {
    private ImageLayout imageLayout;
    private TextView levelTextView;
    private TextView stepTextView;
    private TextView timeTextView;
    private Button pauseResumeButton;
    private boolean pause = true;
    private Button restartButton;
    private Button resetButton;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageLayout = (ImageLayout)findViewById(R.id.jigsawLayout);
        levelTextView = (TextView)findViewById(R.id.level);
        stepTextView = (TextView)findViewById(R.id.step);
        timeTextView = (TextView)findViewById(R.id.time);
        imageLayout.setImageListener(new ImageLayoutListener() {
            @Override
            public void nextLevel() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("游戏信息")
                        .setMessage("下一关")
                        .setCancelable(false)
                        .setPositiveButton(R.string.go, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                imageLayout.nextLevel();
                                levelTextView.setText("" + imageLayout.getLevel());
                            }
                        }).show();
            }

            @Override
            public void stepChange(int currentStep) {
                stepTextView.setText("" + currentStep);
            }

            @Override
            public void timeChange(int currentTime) {
                timeTextView.setText("" + currentTime);
            }

            @Override
            public void gameOver() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.game_info)
                        .setMessage("游戏结束")
                        .setCancelable(false)
                        .setPositiveButton("从新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                imageLayout.restart();
                            }
                        }).setNeutralButton("从头再来", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageLayout.reset();
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
            }
        });

        pauseResumeButton = (Button)findViewById(R.id.pauseResumeButton);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pause) {
                    pause = false;
                    imageLayout.pause();
                    pauseResumeButton.setText("继续");
                } else {
                    pause = true;
                    imageLayout.resume();
                    pauseResumeButton.setText("暂停");
                }
            }
        });
        restartButton = (Button)findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLayout.restart();
            }
        });
        resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLayout.reset();
            }
        });
        quitButton = (Button)findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageLayout.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        imageLayout.resume();
    }

}


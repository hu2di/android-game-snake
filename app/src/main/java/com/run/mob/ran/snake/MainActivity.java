package com.run.mob.ran.snake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.run.mob.ran.snake.engine.GameEngine;
import com.run.mob.ran.snake.enums.Direction;
import com.run.mob.ran.snake.enums.GameState;
import com.run.mob.ran.snake.views.SnakeView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private GameEngine gameEngine;
    private SnakeView snakeView;
    private TextView tvScore;

    private final Handler handler = new Handler();
    private final long updateDelay = 125;

    private float prevX, prevY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snakeView = (SnakeView) findViewById(R.id.snakeView);
        snakeView.setOnTouchListener(this);
        tvScore = (TextView) findViewById(R.id.tvScore);

        play();
    }

    private void play() {
        gameEngine = new GameEngine();
        gameEngine.initGame();
        startUpdateHandler();
    }

    private void startUpdateHandler() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gameEngine.Update();

                if (gameEngine.getCurrentGameState() == GameState.Running) {
                    handler.postDelayed(this, updateDelay);
                }
                if (gameEngine.getCurrentGameState() == GameState.Lost) {
                    OnGameLost();
                }

                tvScore.setText("Score: " + gameEngine.getScore());
                snakeView.setSnakeViewMap(gameEngine.getMap());
                snakeView.invalidate();
            }
        }, updateDelay);
    }

    private void OnGameLost() {
        Toast.makeText(MainActivity.this, "You lost.", Toast.LENGTH_SHORT).show();

        try {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle("DEFEAT")
                    .setMessage("Your score is: " + gameEngine.getScore())
                    .setPositiveButton("Replay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                            play();
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // exit
                            MainActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = event.getX();
                prevY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                float newX = event.getX();
                float newY = event.getY();

                //Calculate where vs swiped
                if (Math.abs(newX - prevX) > Math.abs(newY - prevY)) {
                    //LEFT - RIGHT direction
                    if (newX > prevX) {
                        //RIGHT
                        gameEngine.UpdateDirection(Direction.East);
                    } else {
                        //LEFT
                        gameEngine.UpdateDirection(Direction.West);
                    }
                } else {
                    //UP - DOWN direction
                    if (newY > prevY) {
                        //UP
                        gameEngine.UpdateDirection(Direction.South);
                    } else {
                        //DOWN
                        gameEngine.UpdateDirection(Direction.North);
                    }
                }

                break;
        }

        return true;
    }
}

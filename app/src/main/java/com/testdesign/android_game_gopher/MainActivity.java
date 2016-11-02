package com.testdesign.android_game_gopher;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ImageView[] img_holeList ;
    private TextView tv_score;
    private int[] gopher ;
    private boolean play;
    private Handler handler;
    private GopherSprite[] glist;
    private SoundPool soundPool;
    private int touchId;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        img_holeList = new ImageView[]{
                (ImageView)findViewById(R.id.image_hole_01),
                (ImageView) findViewById(R.id.image_hole_02),
                (ImageView) findViewById(R.id.imageView2),
                (ImageView) findViewById(R.id.imageView3),
                (ImageView) findViewById(R.id.imageView4),
                (ImageView) findViewById(R.id.imageView5),
                (ImageView) findViewById(R.id.imageView6),
                (ImageView) findViewById(R.id.imageView7),
                (ImageView) findViewById(R.id.imageView8)
        };

        tv_score = (TextView) findViewById(R.id.tv_score);
        gopher = new int[] {R.drawable.hole,R.drawable.mole1,R.drawable.mole2,R.drawable.mole3,
                R.drawable.mole2,R.drawable.mole1,R.drawable.hole};
        handler = new Handler();

        buildSoundPool();

        glist = new GopherSprite[9];
        for (int i=0;i<glist.length;i++){
            glist[i] = new GopherSprite(img_holeList[i]);
            img_holeList[i].setOnTouchListener(new GopherOnTouchListener(glist[i]));
        }
    }

    private class GopherSprite implements Runnable{
        ImageView img_hole;
        int index;
        boolean hit;
        public GopherSprite(ImageView img_hole) {
            this.img_hole = img_hole;
        }

        @Override
        public void run() {
            draw();
        }

        private void draw() {
            if(!play){
                return;
            }

            if(hit){
                img_hole.setImageResource(R.drawable.mole4);
                hit = false;
                index= 0;
                handler.postDelayed(this,1000);
            }else{
                index = index % gopher.length;
                img_hole.setImageResource(gopher[index]);
                int n = (int)(Math.random() * 3000) % 3 +1;
                handler.postDelayed(this,(n*100));
                index = ++index % gopher.length;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_play){
            play = true;
            score =0;
            tv_score.setText("0");
            item.setEnabled(false);
            new CountDownTimer(30000, 1000){

                @Override
                public void onTick(long l) {
                    setTitle("剩餘時間 ： "+ l/1000) ;
                }

                @Override
                public void onFinish() {
                    play = false;
                    item.setEnabled(true);
                    setTitle("剩餘時間： 0 ");
                }
            }.start();

            for(GopherSprite g : glist){
                handler.post(g);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GopherOnTouchListener implements View.OnTouchListener{
        GopherSprite g;
        GopherOnTouchListener(GopherSprite g){
            this.g = g;
        }
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(play && event.getAction() == MotionEvent.ACTION_DOWN){
                if(gopher[g.index] == R.drawable.mole2 ||
                        gopher[g.index] == R.drawable.mole3){
                    g.hit = true;
                    soundPool.play(touchId, 1.0F, 1.0F, 0, 0, 1.0F);
                    tv_score.setText("得分："+String.valueOf(++score));
                }else {
                    tv_score.setText("得分："+String.valueOf(--score));
                }
            }
            return false;
        }
    }

    private void buildSoundPool(){
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attr)
                    .build();
        }else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }

        touchId = soundPool.load(this, R.raw.touch, 1);
    }


}

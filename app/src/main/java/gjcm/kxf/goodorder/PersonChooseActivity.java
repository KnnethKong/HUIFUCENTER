package gjcm.kxf.goodorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import gjcm.kxf.huifucenter.R;

/**
 * Created by kxf on 2017/3/9.
 * 开台人数选择
 */
public class PersonChooseActivity extends AppCompatActivity {

    private String deskId;
    private int personNum;
    private EditText editNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.person_choose_layout);
        deskId = getIntent().getStringExtra("deskid");
        initbar();

    }

    private void initbar() {
        editNum = (EditText) findViewById(R.id.person_choosenum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tb);
        TextView ttile = (TextView) findViewById(R.id.toolbar_txt);
        ttile.setText("开台");
        setSupportActionBar(toolbar);
        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonChooseActivity.this.finish();
            }
        });
        findViewById(R.id.toolbar_remind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonChooseActivity.this, RemindActivity.class));
            }
        });
    }

    public void personChooseOk(View view) {
        String snum = editNum.getText().toString();
        if (snum == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
        personNum = Integer.parseInt(snum);
        Intent intent = new Intent(this, GoodsCatActivity.class);
        intent.putExtra("deskid", deskId);
        intent.putExtra("personNum", personNum);
        startActivity(intent);
        finish();
    }
}

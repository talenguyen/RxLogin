package apidez.com.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by nongdenchet on 3/6/16.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpView();
    }

    private void setUpView() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    final Intent data = new Intent();
                    data.putExtra("email", "hello@world.com");
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkLogin() {
        String username = ((TextInputEditText) findViewById(R.id.username)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.password)).getText().toString();
        return username.equals("username") && password.equals("password");
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}

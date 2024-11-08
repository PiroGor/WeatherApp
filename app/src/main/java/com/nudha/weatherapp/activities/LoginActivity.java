package com.nudha.weatherapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.nudha.weatherapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private TextView registerTextView;
    private Button loginButton;
    private boolean isRegistering = false;
    private ArrayList<HashMap<String, String>> userDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Инициализация полей и кнопок
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerTextView = findViewById(R.id.registerTextView);
        loginButton = findViewById(R.id.loginButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.start_color));
        }

        // Инициализация ArrayList для хранения данных пользователей
        userDataList = new ArrayList<>();

        // Обработка нажатия на "Еще не зарегистрированы?"
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRegistering) {
                    emailEditText.setVisibility(View.VISIBLE);
                    registerTextView.setVisibility(View.GONE);
                    isRegistering = true;
                }
            }
        });

        // Обработка нажатия на кнопку "Войти"
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                // Проверка на соответствие требованиям
                if (username.isEmpty() || password.isEmpty() || (isRegistering && email.isEmpty())) {
                    Toast.makeText(LoginActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                } else if (!isUsernameValid(username)) {
                    Toast.makeText(LoginActivity.this, "Неправильный формат логина: от 4 до 16 символов", Toast.LENGTH_SHORT).show();
                } else if (!isPasswordValid(password)) {
                    Toast.makeText(LoginActivity.this, "Неправильный формат пароля: минимум 8 символов, 1 цифра и 1 спец. символ", Toast.LENGTH_SHORT).show();
                } else if (isRegistering && !isEmailValid(email)) {
                    Toast.makeText(LoginActivity.this, "Неправильный формат email", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("password", password);

                    if (isRegistering) {
                        userData.put("email", email);
                    }

                    // Сохранение данных в ArrayList
                    userDataList.add(userData);

                    // Переход на MainActivity после успешного логина или регистрации
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Завершение LoginActivity, чтобы пользователь не мог вернуться обратно
                    finish();
                }
            }
        });
    }

    // Проверка валидности логина
    private boolean isUsernameValid(String username) {
        return username.length() >= 4 && username.length() <= 16;
    }

    // Проверка валидности пароля
    private boolean isPasswordValid(String password) {
        // Регулярное выражение для пароля: минимум 8 символов, хотя бы 1 цифра и 1 специальный символ
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[!@#$%^&*()_+=<>?])[a-zA-Z0-9!@#$%^&*()_+=<>?]{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // Проверка валидности email
    private boolean isEmailValid(String email) {
        // Регулярное выражение для email, чтобы проверять наличие "@" и доменного окончания
        Pattern pattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

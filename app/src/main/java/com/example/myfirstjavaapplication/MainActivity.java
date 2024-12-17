package com.example.myfirstjavaapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    TextView result;
    private boolean dotUsed = false;
    private boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        result = findViewById(R.id.textViewResult);
    }

    public void numFunc(View view) {
        Button button = (Button) view;
        String buttonText = button.getText().toString();

        boolean isOperator = buttonText.equals("+") || buttonText.equals("-") || buttonText.equals("x") || buttonText.equals("÷");
        if (isResultDisplayed) {
            if (buttonText.equals(".")) {
                return;
            } else if (isOperator) {
                result.append(buttonText);
                isResultDisplayed = false;
                return;
            } else {
                result.setText(buttonText);
                isResultDisplayed = false;
                return;
            }
        }

        if (buttonText.equals(".")) {
            if (dotUsed) {
                return;
            }
            dotUsed = true;
        } else if (isOperator) {
            dotUsed = false;
        }

        result.append(buttonText);
    }

    public void numCalc(View view) {
        try {
            String expression = result.getText().toString();
            double calcExpression = evaluateExpression(expression);
            double roundedResult = Math.round(calcExpression * 100.0) / 100.0;

            if (calcExpression == (int) calcExpression) {
                result.setText(String.valueOf((int) calcExpression));
            }
            else {
                result.setText(String.valueOf(roundedResult));
            }

            isResultDisplayed = true;
            dotUsed = false;
        } catch (ArithmeticException e) {
            result.setText(R.string.divided_by_zero_text);
        } catch (Exception e) {
            result.setText(R.string.error_text);
        }
    }

    public static double evaluateExpression(String exp) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                double num = 0;
                boolean isFraction = false;
                double fractionDivider = 10;

                while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
                    if (exp.charAt(i) == '.') {
                        isFraction = true;
                    }
                    else {
                        if (!isFraction) {
                            num = num * 10 + Character.getNumericValue(exp.charAt(i));
                        } else {
                            num += Character.getNumericValue(exp.charAt(i)) / fractionDivider;
                            fractionDivider *= 10;
                        }
                    }
                    i++;
                }
                i--;
                operands.push(num);
            }

            else if (c == '+' || c == '-' || c == 'x' || c == '÷') {
                while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                    operands.push(applyOperation(operators.pop(), operands.pop(), operands.pop()));
                }
                operators.push(c);
            }

            else {
                throw new IllegalArgumentException("Invalid character in expression: " + c);
            }
        }

        while (!operators.empty()) {
            operands.push(applyOperation(operators.pop(), operands.pop(), operands.pop()));
        }

        return operands.pop();
    }

    public static boolean hasPrecedence(char op1, char op2) {
        return (op1 != 'x' && op1 != '÷') || (op2 != '+' && op2 != '-');
    }

    public static double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case 'x':
                return a * b;
            case '÷':
                if (b == 0) {
                    throw new ArithmeticException("Divided by zero");
                }
                return a / b;
        }
        return 0;
    }

    public void clearAll(View view) {
        result.setText("");
        dotUsed = false;
        isResultDisplayed = false;
    }
}
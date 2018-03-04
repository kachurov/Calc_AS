package e.kachurov.calculator;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    int Pointer; // указатель на регистр с числом или действием
    TextView tvScreen, tvF1, tvF2;     // View экрана
    private Screen oScreen;
    static StringBuilder vScreenChar = new StringBuilder();  // Вводимый текст на экране (16 символов max)
    static StringBuilder StrF2 = new StringBuilder();  // История операций на верхней строке
    public double dReg[] = new double[4];      // регистры хранения чисел
    public char cReg[] = {'0', '0', '0', '0'}; // регистры хранения операторов;
    private int intClearAction = 0;
    private boolean PrevKey = false;
    private boolean CurrentKey = true;

    // Следом три перегружаемых метода обновления зкрана
    void ScreenDraw(char c, boolean ScrClear) { // дописывание символа с очисткой экрана или без оной
        if (ScrClear) vScreenChar.delete(0, vScreenChar.length());  // очищаем временный буфер;
        vScreenChar.append(c);                // добавляем введенный символ
        tvScreen.setText(vScreenChar.toString());         // Обновляем экран
    }

    void ScreenDraw(double d) { // вывод рассчитанного числа на зкран с очисткой
        vScreenChar.delete(0, vScreenChar.length());  // очищаем временный буфер;
        vScreenChar.append(d);                // добавляем введенный символ
        tvScreen.setText(vScreenChar.toString());          // Обновляем экран
    }

    void ScreenDraw() { // просто очистка экрана
        vScreenChar.delete(0, vScreenChar.length());  // очищаем временный буфер;
        tvScreen.setText(vScreenChar.toString());   // Обновляем экран
    }

    protected void Put(char cH) {
        cReg[Pointer] = cH;
        Pointer = Pointer > 2 ? 3 : ++Pointer;
    }

    protected void Put(double D) {
        dReg[Pointer] = D;
        Pointer = Pointer > 2 ? 3 : ++Pointer;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получим VIEW наших объектов - дисплея и функциональных полей
        tvScreen = findViewById(R.id.textDisplay);
        tvF1 = findViewById(R.id.F1); //отображение операций - +, -, /, * и т.д.
        tvF2 = findViewById(R.id.F2);
        oScreen = new Screen(); // создали экземпляр класса Screen
        vScreenChar.append("0");
    }

    private void SaveD(char Action) {
        // Запись чисел в регистры
        switch (oScreen.ActionHistory(PrevKey)) {
            case 'C': // Была нажата клавиша Сброс. Меняем константу в dReg[Pointer] на новую, не меняя действия!
                if (oScreen.ActionHistory(CurrentKey) != 'C') {
                    Pointer = 2;
                    Put(Double.parseDouble(vScreenChar.toString())); // приведем тип к double и запишем в регистр
                } else {
                    break;
                }
                break;
            default:
                if (((oScreen.KeyHistory(PrevKey)) == '=') && (Action != '=')) { // после равно нажали клавишу действия
                    Pointer = 1;
                    Put(Action);
                    ScreenDraw();
                } else {
                    Put(Double.parseDouble(vScreenChar.toString())); // приведем тип к double и запишем в регистр
                    Put(Action);
                    ScreenDraw();
                }
        }
        if (Pointer == 3) PrepareReg(Action);
    }

    private void PrepareReg(char Action) {
        // Постобработка регистров и вычисления
        if (Action == '=') {
            dReg[0] = oScreen.Calculate(dReg[0], cReg[1], dReg[2]); // сохранили результат в регистре 0
            ScreenDraw(dReg[0]);
        } else { // клавиша не равно
            if (cReg[3] != '=')
                dReg[0] = oScreen.Calculate(dReg[0], cReg[1], dReg[2]); // сохранили результат
            cReg[1] = Action; // сохранили действие
            Pointer = 2; // поставили указатель на следующий регистр
            ScreenDraw(dReg[0]);
        }
    }

    private void Update(char cH) {
        // Вводимый текст на экране (16 символов max)
        // Метод Update добавляет символ в конце строки и выводит его на экран
        intClearAction = 0;
        if (vScreenChar.length() == 16) return; // Достигнута максимальная длина строки
        if (vScreenChar.toString().compareTo("0") == 0) {
            switch (cH) {
                case '0':
                    break;                                       // защита от лидирующих нулей
                case '.':
                    ScreenDraw(cH, false); // обновляем экран без очистки
                    break;
                default:
                    ScreenDraw(cH, true);  // обновляем экран с очисткой
                    break;
            }
        } else {
            switch (oScreen.KeyHistory(false)) {
                case '-':
                case '+':
                case '/':
                case '*':
                    ScreenDraw(cH, true);
                    break;
                default:
                    ScreenDraw(cH, false);
            }
        }
    }

    void Backspace () {
        if (vScreenChar.length() == 1) {
            ScreenDraw('0', true);
        } else {
            String res = vScreenChar.substring(0, vScreenChar.length() - 1);
            vScreenChar.delete(0, vScreenChar.length());  // очищаем временный буфер;
            vScreenChar.append(res);                // добавляем введенный символ
            tvScreen.setText(vScreenChar.toString());          // Обновляем экран
        }
    }
    @Override
    public void onClick(View v) {
        /*
        btnShift = R.id.Row2_button1;
        btnMode = R.id.Row2_button2;
        btnMC = R.id.Row2_button3;
        btnMR = R.id.Row2_button4;
        btnOFF = R.id.Row2_button5;
        btnMMin = R.id.Row4_button5;
        */
        // по id определяем кнопку, вызвавшую этот обработчик
        oScreen.KeyHistory(v.getId());
        switch (v.getId()) {
            case R.id.Row10_button1:  // кнопка 0
                if ((vScreenChar.length() == 1) & ((vScreenChar.toString()).compareTo("0") == 0))
                    break;
                Update('0');
                break;
            case R.id.Row8_button1:  // кнопка 1
                Update('1');
                break;
            case R.id.Row8_button2:  // кнопка 2
                Update('2');
                break;
            case R.id.Row8_button3:  // кнопка 3
                Update('3');
                break;
            case R.id.Row6_button1:  // кнопка 4
                Update('4');
                break;
            case R.id.Row6_button2:  // кнопка 5
                Update('5');
                break;
            case R.id.Row6_button3:  // кнопка 6
                Update('6');
                break;
            case R.id.Row4_button1:  // кнопка 7
                Update('7');
                break;
            case R.id.Row4_button2:  // кнопка 8
                Update('8');
                break;
            case R.id.Row4_button3:  // кнопка 9
                Update('9');
                break;
            case R.id.Row10_button2:  // кнопка Точка (или запятая?)
                // Проверим, вводилась ли точка ранее, чтобы не допустить второй точки
                if (vScreenChar.indexOf(".") == -1) Update('.');
                break;
            case R.id.Row2_button5:  // кнопка Backspace
                // затирка символа
                Backspace();
                break;
            // кнопки арифметических действий
            case R.id.Row10_button3:  // кнопка плюс
                oScreen.ActionHistory(v.getId());
                SaveD('+');
                break;
            case R.id.Row8_button4:  // кнопка минус
                oScreen.ActionHistory(v.getId());
                SaveD('-');
                break;
            case R.id.Row6_button4:  // кнопка умножить
                oScreen.ActionHistory(v.getId());
                SaveD('*');
                break;
            case R.id.Row4_button4:  // кнопка разделить
                oScreen.ActionHistory(v.getId());
                SaveD('/');
                break;
            // ************ кнопки арифметических действий
            case R.id.Row10_button4:  // кнопка равно
                oScreen.ActionHistory(v.getId());
                SaveD('=');
                break;
            case R.id.Row8_button5:
                // кнопка Clear, первое нажатие: сброс экрана и временного буфера
                // второе нажатие подряд + очистка регистров и сброс действия
                oScreen.ActionHistory(v.getId());
                ++intClearAction;
                switch (intClearAction) {
                    case 1: //первое нажатие
                        tvScreen.setText(R.string.HellowStr);
                        vScreenChar.delete(0, vScreenChar.length()); //сброс строки
                        break;
                    case 2: //второе нажатие
                        tvScreen.setText(R.string.HellowStr);
                        vScreenChar.delete(0, vScreenChar.length()); //сброс строки
                        for (Pointer = 3; Pointer >= 0; Pointer--) {
                            dReg[Pointer] = 0; //сброс регистра
                            cReg[Pointer] = '0';
                        }
                        Pointer = 0;
                        tvF1.setText(" ");// Сбросим функциональный экран
                        intClearAction = 0;
                        break;
                }
        }
    }
}
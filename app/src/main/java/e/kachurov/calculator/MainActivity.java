package e.kachurov.calculator;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    static StringBuilder vScreenChar = new StringBuilder();  // Вводимый текст на экране (16 символов max)
    static StringBuilder StrF2 = new StringBuilder();  // История операций на верхней строке
    public double dReg[] = new double[4];      // регистры хранения чисел
    public double Mem = 0; // содержимое памяти
    public char cReg[] = {'0', '0', '0', '0'}; // регистры хранения операторов;
    int Pointer; // указатель на регистр с числом или действием
    TextView tvScreen, tvF1, tvF2;     // View экрана
    TextView fv2_1;
    private Screen oScreen;
    private int intClearAction = 0;
    private boolean PrevKey = false;
    private boolean CurrentKey = true;
    boolean Shift = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oScreen = new Screen(); // создали экземпляр класса Screen
        vScreenChar.append("0");
        // Получим VIEW наших объектов - дисплея и функциональных полей
        tvScreen = findViewById(R.id.textDisplay);
        tvF1 = findViewById(R.id.textView11); //отображение операций - +, -, /, * и т.д.
        tvF2 = findViewById(R.id.textView4);
        ////
        fv2_1 = findViewById(R.id.Row3_textView1);
    }

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

    private void SaveD(char Action) {
        CharSequence cs = String.valueOf(Action);
        // Запись чисел в регистры
        tvF1.setBackgroundColor(getResources().getColor(R.color.Orange));
        //tvF2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        tvF1.setText(cs);
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

    private void MemoryAction(char Action) {
        switch (Action) {
            case 'a':
                Mem += Double.parseDouble(vScreenChar.toString());
                tvF2.setVisibility(View.VISIBLE);
                break;
            case 'b':
                Mem -= Double.parseDouble(vScreenChar.toString());
                tvF2.setVisibility(View.VISIBLE);
                break;
            case 'c':
                ScreenDraw(Mem);
                break;
            case 'd':
                Mem = 0;
                tvF2.setVisibility(View.INVISIBLE);
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
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                    ScreenDraw(cH, true);
                    break;
                default:
                    ScreenDraw(cH, false);
            }
        }
    }

    private void Shift(boolean Action) { // Action = true - включение режима Shift, false - выклбчение
        Shift = Action;
        if (Action) {
            fv2_1.setTextColor(getResources().getColor(R.color.OrangeDark));
        } else {
            fv2_1.setTextColor(getResources().getColor(R.color.DarkerGray));
        }
    }
    private void Update_Shift (char cH) {
        switch (cH) {
            case '7':  // 1/x
                ScreenDraw(1/Double.parseDouble(vScreenChar.toString()));
        }
    }

    void Backspace() {
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
                if (Shift) {Update_Shift('7');} else {Update('7');}
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
            // кнопки управления памятью
            case R.id.Row6_button5: // M+
                MemoryAction('a');
                break;
            case R.id.Row4_button5: // M-
                MemoryAction('b');
                break;
            case R.id.Row2_button4: // MR
                MemoryAction('c');
                break;
            case R.id.Row2_button3: // MC
                MemoryAction('d');
                break;
            // *********** кнопки управления пямятью
            case R.id.Row2_button1:
                Shift(true);
                break;
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
                        Shift (false);
                        break;
                    case 2: //второе нажатие
                        tvScreen.setText(R.string.HellowStr);
                        vScreenChar.delete(0, vScreenChar.length()); //сброс строки
                        for (Pointer = 3; Pointer >= 0; Pointer--) {
                            dReg[Pointer] = 0; //сброс регистра
                            cReg[Pointer] = ' ';
                        }
                        Pointer = 0;
                        tvF1.setText(" ");// Сбросим функциональный экран
                        tvF1.setBackgroundColor(getResources().getColor(R.color.Background));
                        intClearAction = 0;
                        oScreen.RsetHistory();
                        break;
                }
        }
    }
}
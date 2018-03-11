package e.kachurov.calculator;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    int Pointer; // указатель на регистр с числом или действием
    TextView tvScreen, tvF1, tvF2;     // View экрана
    TextView fv2_1;
    private Screen oScreen;
    static StringBuilder vScreenChar = new StringBuilder();  // Вводимый текст на экране (16 символов max)
    static StringBuilder StrF2 = new StringBuilder();  // История операций на верхней строке
    public double dReg[] = new double[4];      // регистры хранения чисел
    public double Mem = 0; // содержимое памяти
    public char cReg[] = {'0', '0', '0', '0'}; // регистры хранения операторов;
    private int intClearAction = 0;
    boolean Shift = false;

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
        tvF1 = findViewById(R.id.textView11); //отображение операций - +, -, /, * и т.д.
        tvF2 = findViewById(R.id.textView4);  // отображение состояния памяти
        oScreen = new Screen(); // создали экземпляр класса Screen
        vScreenChar.append("0");

        ///// функциональные кнопки
        fv2_1 = findViewById(R.id.Row3_textView1);
    }

    private void SaveD (char Action) throws IllegalArgumentException {
        CharSequence cs = String.valueOf(Action);
        // Запись чисел в регистры
        tvF1.setBackgroundColor(getResources().getColor(R.color.Orange));
        tvF1.setText(cs);
        boolean prevKey = false;
        switch (oScreen.ActionHistory(prevKey)) {
            case 'C': // Была нажата клавиша Сброс. Меняем константу в dReg[Pointer] на новую, не меняя действия!
                boolean currentKey = true;
                if (oScreen.ActionHistory(currentKey) != 'C') {
                    Pointer = 2;
                    Put(Double.parseDouble(vScreenChar.toString())); // приведем тип к double и запишем в регистр
                } else {
                    break;
                }
                break;
            default:
                if (((oScreen.KeyHistory(prevKey)) == '=') && (Action != '=')) { // после равно нажали клавишу действия
                    Pointer = 1;
                    Put(Action);
                    ScreenDraw();
                } else {
                    try {
                        Put(Double.parseDouble(vScreenChar.toString())); // приведем тип к double и запишем в регистр
                    } catch (NumberFormatException e) {
                        Reset(2);
                    }
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
    private void MemoryAction (char Action) {
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

    private void Update(char cH) { // обновление экрана при нажатии обычных кнопок
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
                    ScreenDraw(cH, true); // обновляем экран с очисткой
                    break;
                default:
                    ScreenDraw(cH, false); // обновляем экран без очистки
            }
        }
    }

    private void Update_Shift (char cH) { // обновление экрана с нажатой кнопкой Shift
        switch (cH) {
            case '7':  // 1/x
                double res = 1/Double.parseDouble(vScreenChar.toString());
                ScreenDraw(res);
                KeyShift(false); // сбросим кнопку Shift
                break;                                       // защита от лидирующих нулей
        }
    }
    void KeyShift (boolean Action) {
        Shift = Action;
        if (Action) {  // включаем функциональные кнопки
            fv2_1.setTextColor(getResources().getColor(R.color.OrangeDark));
        } else {       // выключаем функциональные кнопки
            fv2_1.setTextColor(getResources().getColor(R.color.Gray));
        }
    }
    void Reset (int Action) {
        switch (Action) {
            case 1: //первое нажатие
                tvScreen.setText(R.string.HellowStr);
                vScreenChar.delete(0, vScreenChar.length()); //сброс строки
                KeyShift(false);
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
                oScreen.ResetHistory();
                break;
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
            case R.id.Row2_button1:  // кнопка Shift
                KeyShift(true);
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
                Reset(intClearAction);
        }
    }
}
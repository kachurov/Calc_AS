package e.kachurov.calculator;


/**
 * Created by Kachurov_EV on 25.01.2018.
 * Методы для работы с экраном
 */

class Screen {

    private char PrevKey, CurrentKey, PrevAction, CurrentAction;

    public void RsetHistory () {
        PrevKey = CurrentKey = PrevAction = CurrentAction = ' ';
    }

    double Calculate(double s1, char Action, double s2) {
        switch (Action) {
            case '+':
                return s1 + s2;
            case '-':
                return s1 - s2;
            case '*':
                return s1 * s2;
            case '/':
                return s1 / s2;
            case 'e':
                return Math.pow(s1, s2);
            default:
                return 0;
        }
    }

    void KeyHistory(int vId) {
        switch (vId) {
            case R.id.Row10_button1: // кнопка 0
                PrevKey = CurrentKey;
                CurrentKey = '0';
                break;
            case R.id.Row8_button1:  // кнопка 1
                PrevKey = CurrentKey;
                CurrentKey = '1';
                break;
            case R.id.Row8_button2:  // кнопка 2
                PrevKey = CurrentKey;
                CurrentKey = '2';
                break;
            case R.id.Row8_button3:  // кнопка 3
                PrevKey = CurrentKey;
                CurrentKey = '3';
                break;
            case R.id.Row6_button1:  // кнопка 4
                PrevKey = CurrentKey;
                CurrentKey = '4';
                break;
            case R.id.Row6_button2: // кнопка 5
                PrevKey = CurrentKey;
                CurrentKey = '5';
                break;
            case R.id.Row6_button3:  // кнопка 6
                PrevKey = CurrentKey;
                CurrentKey = '6';
                break;
            case R.id.Row4_button1:  // кнопка 7
                PrevKey = CurrentKey;
                CurrentKey = '7';
                break;
            case R.id.Row4_button2:  // кнопка 8
                PrevKey = CurrentKey;
                CurrentKey = '8';
                break;
            case R.id.Row4_button3:  // кнопка 9
                PrevKey = CurrentKey;
                CurrentKey = '9';
                break;
            case R.id.Row10_button2:  // кнопка Точка (или запятая?)
                PrevKey = CurrentKey;
                CurrentKey = '.';
                break;
            // кнопки арифметических действий
            case R.id.Row10_button3:  // кнопка плюс
                PrevKey = CurrentKey;
                CurrentKey = '+';
                break;
            case R.id.Row8_button4:  // кнопка минус
                PrevKey = CurrentKey;
                CurrentKey = '-';
                break;
            case R.id.Row6_button4:  // кнопка умножить
                PrevKey = CurrentKey;
                CurrentKey = '*';
                break;
            case R.id.Row4_button4:  // кнопка разделить
                PrevKey = CurrentKey;
                CurrentKey = '/';
                break;
            // ************ кнопки арифметических действий
            case R.id.Row10_button4:  // кнопка равно
                PrevKey = CurrentKey;
                CurrentKey = '=';
                break;
            case R.id.Row8_button5:
                PrevKey = CurrentKey;
                CurrentKey = 'C';
                break;
                //  ---------- Кнопки управления памятью
            case R.id.Row6_button5:  // M+
                PrevKey = CurrentKey;
                CurrentKey = 'a';
                break;
            case R.id.Row4_button5:  // M-
                PrevKey = CurrentKey;
                CurrentKey = 'b';
                break;
            case R.id.Row2_button4:  // MR
                PrevKey = CurrentKey;
                CurrentKey = 'c';
                break;
            case R.id.Row2_button3:  // MC
                PrevKey = CurrentKey;
                CurrentKey = 'd';
                break;
        }
    }

    void ActionHistory(int vID) {
        switch (vID) {
            // кнопки арифметических действий
            case R.id.Row10_button3:  // кнопка плюс
                PrevAction = CurrentAction;
                CurrentAction = '+';
                break;
            case R.id.Row8_button4:  // кнопка минус
                PrevAction = CurrentAction;
                CurrentAction = '-';
                break;
            case R.id.Row6_button4:  // кнопка умножить
                PrevAction = CurrentAction;
                CurrentAction = '*';
                break;
            case R.id.Row4_button4:  // кнопка разделить
                PrevAction = CurrentAction;
                CurrentAction = '/';
                break;
            // ************ кнопки арифметических действий
            case R.id.Row10_button4:  // кнопка равно
                PrevAction = CurrentAction;
                CurrentAction = '=';
                break;
            case R.id.Row8_button5:
                PrevAction = CurrentAction;
                CurrentAction = 'C';
                break;

        }
    }

    char ActionHistory(boolean PrevNextValue) {
        if (PrevNextValue) return CurrentAction;
        else return PrevAction;
    }

    char KeyHistory(boolean PrevNextValue) {
        if (PrevNextValue) return CurrentKey;
        else return PrevKey;
    }

}
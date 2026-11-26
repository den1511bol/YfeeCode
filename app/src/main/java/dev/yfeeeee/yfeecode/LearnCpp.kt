// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun cppCourse() = Course("cpp", "C++", "⚙️", listOf(
    Lesson("Первая программа", "Программа на C++ начинается с функции main. Для вывода подключают iostream.\n>#include <iostream>\n>int main() {\n>    std::cout << \"Hello\" << std::endl;\n>    return 0;\n>}", listOf(
        build("Подключи iostream", "#|include|<|iostream|>", "import|using"),
        build("Объяви функцию main", "int|main|(|)|{", "void|def"),
        type("Выведи Hello", "std::cout << \"Hello\" << std::endl;"),
        build("Заверши программу", "return|0|;", "exit|end"),
        type("Подключи библиотеку string", "#include <string>"),
        type("Закрой функцию", "}")
    )),
    Lesson("Переменные и типы", "У каждой переменной есть тип: int — целое, double — дробное, string — текст, bool — логическое.\n>int age = 18;\n>double pi = 3.14;\n>std::string name = \"Аня\";", listOf(
        build("Целое age = 18", "int|age|=|18|;", "double|string"),
        type("Дробное pi = 3.14", "double pi = 3.14;"),
        build("Строка name", "std|::|string|name|=|\"Anna\"|;", "int|char"),
        type("Логическая ok = true", "bool ok = true;"),
        build("Увеличь x на 1", "x|++|;", "--|=="),
        type("Объяви int x = 5", "int x = 5;")
    )),
    Lesson("Условия", "if проверяет условие в скобках, тело блока пишут в фигурных.\n>if (x > 5) {\n>    std::cout << \"много\";\n>} else {\n>    std::cout << \"мало\";\n>}", listOf(
        build("Если x больше 5", "if|(|x|>|5|)|{", "elif|then"),
        type("Если age >= 18", "if (age >= 18) {"),
        build("Сравни a и b на равенство", "if|(|a|==|b|)|{", "=|:"),
        type("Напиши else", "} else {"),
        build("Логическое И", "if|(|a|&&|b|)|{", "and|&"),
        type("Логическое ИЛИ", "if (a || b) {")
    )),
    Lesson("Циклы", "for повторяет код по счётчику, while — пока условие верно.\n>for (int i = 0; i < 3; i++) {\n>}\n>while (n < 10) { n++; }", listOf(
        build("Цикл от 0 до 2", "for|(|int|i|=|0|;|i|<|3|;|i|++|)|{", "in|range"),
        type("Пока n меньше 10", "while (n < 10) {"),
        build("Прерви цикл", "break|;", "stop|exit"),
        type("Следующая итерация", "continue;"),
        build("Бесконечный цикл", "while|(|true|)|{", "for|if"),
        type("Увеличь i на 2", "i += 2;")
    )),
    Lesson("Функции", "Функция объявляется с типом результата. void — ничего не возвращает.\n>int add(int a, int b) {\n>    return a + b;\n>}\n>void hello() { }", listOf(
        build("Функция add двух чисел", "int|add|(|int|a|,|int|b|)|{", "def|void"),
        type("Объяви void hello()", "void hello() {"),
        build("Верни сумму", "return|a|+|b|;", "print|=="),
        type("Вызови add(2, 3)", "add(2, 3);"),
        build("Выведи результат add", "std|::|cout|<<|add|(|2|,|3|)|;", "cin|>>"),
        type("Прочитай число в x", "std::cin >> x;")
    )),
    Lesson("Массивы и vector", "Массив имеет фиксированный размер, vector растёт. Для vector подключают библиотеку.\n>#include <vector>\n>std::vector<int> v = {1, 2, 3};\n>v.push_back(4);", listOf(
        build("Подключи vector", "#|include|<|vector|>", "import|array"),
        type("Добавь 4 в vector v", "v.push_back(4);"),
        build("Размер vector v", "v|.|size|(|)", "len|length"),
        type("Объяви массив int a[3]", "int a[3];"),
        build("Первый элемент массива a", "a|[|0|]", "(|1"),
        type("Объяви vector чисел v", "std::vector<int> v;")
    )),
    Lesson("Указатели и классы", "Указатель хранит адрес переменной: & берёт адрес, * разыменовывает. Класс описывает объект.\n>int x = 5;\n>int* p = &x;\n>*p = 7;\n>class Dog {\n>public:\n>    std::string name;\n>};", listOf(
        build("Возьми адрес x", "int|*|p|=|&|x|;", "int|x|=="),
        type("Запиши 7 по указателю p", "*p = 7;"),
        build("Объяви класс Dog", "class|Dog|{", "def|struct"),
        type("Открытая секция класса", "public:"),
        build("Поле name в классе", "std|::|string|name|;", "int|=="),
        type("Создай объект Dog с именем d", "Dog d;")
    )),
    Lesson("Наследование", "Класс-наследник получает поля и методы родителя. Двоеточие и public указывают базовый класс. virtual позволяет переопределять метод.\n>class Animal {\n>public:\n>    virtual void speak() { }\n>};\n>class Dog : public Animal {\n>};", listOf(
        build("Наследуй Dog от Animal", "class|Dog|:|public|Animal|{", "extends|struct"),
        type("Виртуальный метод speak", "virtual void speak() { }"),
        build("Переопредели speak в наследнике", "void|speak|(|)|override|{", "virtual|class"),
        type("Конструктор класса Dog", "Dog() { }"),
        build("Вызови метод у объекта d", "d|.|speak|(|)|;", "Dog|class"),
        type("Создай указатель на Animal", "Animal* a = new Dog();")
    )),
    Lesson("Строки и ввод", "std::string хранит текст, cin читает ввод, getline читает строку целиком вместе с пробелами.\n>std::string s;\n>std::cin >> s;\n>std::cout << s.size();", listOf(
        type("Объяви строку name", "std::string name;"),
        type("Прочитай слово в name", "std::cin >> name;"),
        type("Прочитай строку целиком", "std::getline(std::cin, name);"),
        type("Длина строки", "name.size()"),
        type("Склей две строки", "std::string c = a + b;"),
        type("Подключи заголовок строк", "#include <string>")
    )),
    Lesson("Ссылки и const", "Ссылка (&) даёт другое имя той же переменной, поэтому функция может её менять без копирования. const запрещает менять значение.\n>void inc(int& x) { x++; }\n>const int N = 10;", listOf(
        type("Параметр-ссылка на int", "void inc(int& x)"),
        type("Константа N равна 10", "const int N = 10;"),
        build("Передай строку по константной ссылке", "const|std::string|&|s", "int|void"),
        type("Создай ссылку r на переменную a", "int& r = a;"),
        type("Увеличь x на 1", "x++;"),
        type("Подключи заголовок vector", "#include <vector>")
    )),
    Lesson("Ввод: cin и getline", "cin читает слово или число, getline читает всю строку с пробелами. После cin перед getline нужно убрать перевод строки: cin.ignore().\n>int a;\n>cin >> a;\n>string s;\n>getline(cin, s);", listOf(
        type("Прочитай число в переменную a", "cin >> a;"),
        type("Прочитай две переменные a и b", "cin >> a >> b;"),
        type("Прочитай строку целиком в s", "getline(cin, s);"),
        type("Убери перевод строки из ввода", "cin.ignore();"),
        type("Выведи a и перенос строки", "cout << a << endl;"),
        type("Подключи ввод-вывод", "#include <iostream>")
    )),
    Lesson("Switch и тернарный оператор", "switch выбирает ветку по значению, break выходит из switch, default срабатывает, если ничего не подошло. Тернарный оператор: условие ? да : нет.\n>switch (x) {\n>  case 1: cout << \"один\"; break;\n>  default: cout << \"другое\";\n>}\n>int m = a > b ? a : b;", listOf(
        type("Начни switch по x", "switch (x) {"),
        type("Ветка для значения 1", "case 1:"),
        type("Выйди из switch", "break;"),
        type("Ветка по умолчанию", "default:"),
        type("Максимум из a и b тернарным оператором", "int m = a > b ? a : b;"),
        type("Условие И", "a > 0 && b > 0")
    )),
    Lesson("Строки std::string", "string хранит текст. Методы: size, substr, find, append, +. Строки сравнивают через ==. Нужен #include <string>.\n>string s = \"Привет\";\n>s.size();\n>s.substr(0, 3);\n>s.find(\"ив\");\n>s += \"!\";", listOf(
        type("Создай строку s со значением abc", "string s = \"abc\";"),
        type("Длина строки s", "s.size()"),
        type("Первые 3 символа s", "s.substr(0, 3)"),
        type("Найди ab в строке s", "s.find(\"ab\")"),
        type("Добавь ! к строке s", "s += \"!\";"),
        type("Подключи string", "#include <string>")
    )),
    Lesson("Структуры и enum", "struct объединяет поля в один тип. enum задаёт набор именованных значений. Поля читают через точку.\n>struct Point { int x; int y; };\n>Point p = {1, 2};\n>p.x = 5;\n>enum Color { RED, GREEN, BLUE };", listOf(
        type("Структура Point с полями x и y", "struct Point { int x; int y; };"),
        type("Создай p со значениями 1 и 2", "Point p = {1, 2};"),
        type("Задай полю x значение 5", "p.x = 5;"),
        type("Перечисление Color из RED GREEN BLUE", "enum Color { RED, GREEN, BLUE };"),
        type("Переменная c равна GREEN", "Color c = GREEN;"),
        type("Прочитай поле y", "p.y")
    )),
    Lesson("map и set", "map хранит пары ключ-значение и держит ключи отсортированными. set хранит уникальные значения. Нужны #include <map> и <set>.\n>map<string, int> m;\n>m[\"a\"] = 1;\n>set<int> s;\n>s.insert(5);\n>m.count(\"a\");", listOf(
        type("Создай map string-int", "map<string, int> m;"),
        type("Положи a со значением 1", "m[\"a\"] = 1;"),
        type("Создай множество int", "set<int> s;"),
        type("Добавь 5 в множество", "s.insert(5);"),
        type("Проверь, есть ли ключ a", "m.count(\"a\")"),
        type("Перебери пары map", "for (auto& kv : m) {")
    )),
    Lesson("Алгоритмы", "Заголовок <algorithm> даёт sort, reverse, find, count, max_element. Они работают с диапазоном begin() ... end().\n>sort(v.begin(), v.end());\n>reverse(v.begin(), v.end());\n>auto it = find(v.begin(), v.end(), 3);", listOf(
        type("Подключи algorithm", "#include <algorithm>"),
        type("Отсортируй вектор v", "sort(v.begin(), v.end());"),
        type("Разверни вектор v", "reverse(v.begin(), v.end());"),
        type("Найди число 3 в v", "auto it = find(v.begin(), v.end(), 3);"),
        type("Посчитай, сколько раз 3 в v", "count(v.begin(), v.end(), 3)"),
        type("Размер вектора v", "v.size()")
    )),
    Lesson("Лямбды", "Лямбда это безымянная функция: [захват](параметры){тело}. Её часто передают в алгоритмы.\n>auto sq = [](int x) { return x * x; };\n>sort(v.begin(), v.end(), [](int a, int b) { return a > b; });", listOf(
        type("Лямбда квадрата x в переменной sq", "auto sq = [](int x) { return x * x; };"),
        type("Вызови sq с 5", "sq(5)"),
        type("Сортировка по убыванию лямбдой", "sort(v.begin(), v.end(), [](int a, int b) { return a > b; });"),
        type("Лямбда, захватывающая всё по ссылке", "[&]() {}"),
        type("Лямбда без параметров и тела", "[]() {}"),
        type("Подключи functional", "#include <functional>")
    )),
    Lesson("Шаблоны", "Шаблон делает функцию или класс для любого типа. template<typename T> объявляет тип-параметр.\n>template<typename T>\n>T maxOf(T a, T b) { return a > b ? a : b; }\n>maxOf<int>(1, 2);", listOf(
        type("Объяви шаблон с типом T", "template<typename T>"),
        type("Функция maxOf для T", "T maxOf(T a, T b) { return a > b ? a : b; }"),
        type("Вызови maxOf для int", "maxOf<int>(1, 2)"),
        type("Вектор строк", "vector<string> v;"),
        type("Пара из int и string", "pair<int, string> p;"),
        type("Создай пару 1 и a", "make_pair(1, \"a\")")
    )),
    Lesson("Динамическая память и умные указатели", "new выделяет память, delete освобождает. Умные указатели делают это сами: unique_ptr владеет один, shared_ptr разделяют. Нужен #include <memory>.\n>int* p = new int(5);\n>delete p;\n>auto u = make_unique<int>(5);", listOf(
        type("Выдели память под int со значением 5", "int* p = new int(5);"),
        type("Освободи память p", "delete p;"),
        type("Создай unique_ptr на 5", "auto u = make_unique<int>(5);"),
        type("Создай shared_ptr на 5", "auto s = make_shared<int>(5);"),
        type("Подключи memory", "#include <memory>"),
        type("Прочитай значение через указатель p", "*p")
    )),
    Lesson("Исключения", "try ловит ошибку, catch обрабатывает, throw бросает. Для стандартных ошибок используют std::exception и what().\n>try {\n>  throw runtime_error(\"плохо\");\n>} catch (const exception& e) {\n>  cout << e.what();\n>}", listOf(
        type("Начни блок try", "try {"),
        type("Брось runtime_error", "throw runtime_error(\"плохо\");"),
        type("Поймай любое стандартное исключение", "} catch (const exception& e) {"),
        type("Выведи текст ошибки", "cout << e.what();"),
        type("Поймай вообще всё", "} catch (...) {"),
        type("Подключи stdexcept", "#include <stdexcept>")
    )),
    Lesson("Файлы: fstream", "ifstream читает файл, ofstream пишет. Нужен #include <fstream>. Файл закрывается сам, когда объект исчезает.\n>ofstream out(\"a.txt\");\n>out << \"привет\";\n>ifstream in(\"a.txt\");\n>getline(in, line);", listOf(
        type("Подключи fstream", "#include <fstream>"),
        type("Открой a.txt для записи", "ofstream out(\"a.txt\");"),
        type("Запиши привет в файл", "out << \"привет\";"),
        type("Открой a.txt для чтения", "ifstream in(\"a.txt\");"),
        type("Прочитай строку из файла", "getline(in, line);"),
        type("Проверь, что файл открылся", "in.is_open()")
    )),
    Lesson("Перегрузка операторов и namespace", "Оператор можно определить для своего класса. namespace группирует имена и предотвращает конфликты. using namespace std; избавляет от префикса std::.\n>Point operator+(Point a, Point b) {\n>  return {a.x + b.x, a.y + b.y};\n>}\n>namespace my { int x = 1; }\n>my::x", listOf(
        type("Оператор плюс для Point", "Point operator+(Point a, Point b) {"),
        type("Объяви пространство имён my", "namespace my {"),
        type("Обратись к x из my", "my::x"),
        type("Используй пространство std", "using namespace std;"),
        type("Вызови cout с префиксом", "std::cout"),
        type("Постоянная pi равная 3.14 на этапе компиляции", "constexpr double pi = 3.14;")
    )),
    Lesson("Потоки: std::thread", "Поток выполняет функцию параллельно с главной программой. join ждёт его завершения. Если два потока меняют одну переменную, нужен mutex: lock_guard закрывает доступ на время блока. При сборке добавь флаг -pthread.\n>#include <thread>\n>#include <mutex>\n>std::thread t(work);\n>t.join();\n>std::mutex m;\n>std::lock_guard<std::mutex> lock(m);", listOf(
        type("Подключи thread", "#include <thread>"),
        type("Подключи mutex", "#include <mutex>"),
        type("Запусти функцию work в потоке t", "std::thread t(work);"),
        type("Дождись завершения потока t", "t.join();"),
        type("Создай мьютекс m", "std::mutex m;"),
        type("Закрой доступ на время блока", "std::lock_guard<std::mutex> lock(m);")
    )),
    Lesson("Время и случайные числа", "chrono измеряет время, random даёт качественные случайные числа. mt19937 это генератор, uniform_int_distribution задаёт диапазон. this_thread::sleep_for делает паузу.\n>#include <chrono>\n>#include <random>\n>std::mt19937 gen(std::random_device{}());\n>std::uniform_int_distribution<int> d(1, 6);\n>int x = d(gen);\n>std::this_thread::sleep_for(std::chrono::seconds(1));", listOf(
        type("Подключи chrono", "#include <chrono>"),
        type("Подключи random", "#include <random>"),
        type("Создай генератор gen", "std::mt19937 gen(std::random_device{}());"),
        type("Задай диапазон от 1 до 6", "std::uniform_int_distribution<int> d(1, 6);"),
        type("Получи число x", "int x = d(gen);"),
        type("Пауза на 1 секунду", "std::this_thread::sleep_for(std::chrono::seconds(1));")
    ))
))

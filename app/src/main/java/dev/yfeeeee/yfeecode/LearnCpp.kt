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
    ))
))

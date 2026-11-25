// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun csCourse() = Course("cs", "C#", "🔷", listOf(
    Lesson("Первая программа", "Метод Main — точка входа. Console.WriteLine печатает строку.\n>using System;\n>class Program {\n>    static void Main() {\n>        Console.WriteLine(\"Hello\");\n>    }\n>}", listOf(
        build("Подключи System", "using|System|;", "import|include"),
        build("Объяви класс Program", "class|Program|{", "def|func"),
        type("Выведи Hello", "Console.WriteLine(\"Hello\");"),
        build("Точка входа", "static|void|Main|(|)|{", "int|main"),
        type("Объяви класс App", "class App {"),
        type("Закрой блок", "}")
    )),
    Lesson("Переменные и типы", "int — целое, double — дробное, string — текст, bool — логическое. var подбирает тип сам.\n>int age = 18;\n>string name = \"Аня\";\n>var x = 5;", listOf(
        build("Целое age = 18", "int|age|=|18|;", "string|double"),
        type("Строка name = \"Bob\"", "string name = \"Bob\";"),
        build("Дробное pi", "double|pi|=|3.14|;", "int|bool"),
        type("Логическая ok = true", "bool ok = true;"),
        build("Тип выводится сам", "var|x|=|5|;", "int|let"),
        type("Увеличь x на 1", "x++;")
    )),
    Lesson("Условия", "if, else if и else управляют ходом программы.\n>if (x > 5) {\n>    Console.WriteLine(\"много\");\n>} else if (x == 0) {\n>}", listOf(
        build("Если x больше 5", "if|(|x|>|5|)|{", "elif|then"),
        type("Если age >= 18", "if (age >= 18) {"),
        build("Иначе если x равен 0", "}|else|if|(|x|==|0|)|{", "elif|:"),
        type("Напиши else", "} else {"),
        build("Логическое И", "if|(|a|&&|b|)|{", "and|&"),
        type("Логическое ИЛИ", "if (a || b) {")
    )),
    Lesson("Циклы", "for — по счётчику, foreach — по элементам коллекции, while — пока условие верно.\n>for (int i = 0; i < 3; i++) { }\n>foreach (var x in items) { }", listOf(
        build("Цикл от 0 до 2", "for|(|int|i|=|0|;|i|<|3|;|i|++|)|{", "in|range"),
        type("Пока n меньше 10", "while (n < 10) {"),
        build("Перебери items", "foreach|(|var|x|in|items|)|{", "for|of"),
        type("Прерви цикл", "break;"),
        build("Следующая итерация", "continue|;", "next|skip"),
        type("Бесконечный цикл", "while (true) {")
    )),
    Lesson("Методы", "Метод объявляется с типом результата и параметрами. void — ничего не возвращает.\n>static int Add(int a, int b) {\n>    return a + b;\n>}", listOf(
        build("Метод Add двух чисел", "static|int|Add|(|int|a|,|int|b|)|{", "def|void"),
        type("Объяви static void Hello()", "static void Hello() {"),
        build("Верни сумму", "return|a|+|b|;", "print|=="),
        type("Вызови Add(2, 3)", "Add(2, 3);"),
        build("Выведи результат Add", "Console|.|WriteLine|(|Add|(|2|,|3|)|)|;", "ReadLine|print"),
        type("Прочитай строку в s", "string s = Console.ReadLine();")
    )),
    Lesson("Списки", "List<T> — растущий список. Для него нужен using System.Collections.Generic.\n>var nums = new List<int>();\n>nums.Add(4);\n>Console.WriteLine(nums.Count);", listOf(
        build("Создай список чисел", "var|nums|=|new|List|<|int|>|(|)|;", "array|[]"),
        type("Добавь 4 в список nums", "nums.Add(4);"),
        build("Размер списка nums", "nums|.|Count", "Length|size"),
        type("Выведи размер nums", "Console.WriteLine(nums.Count);"),
        build("Первый элемент nums", "nums|[|0|]", "(|1"),
        type("Подключи коллекции", "using System.Collections.Generic;")
    )),
    Lesson("Классы и свойства", "Класс описывает объект, свойство даёт доступ к данным через get и set.\n>public class Dog {\n>    public string Name { get; set; }\n>}\n>var d = new Dog();\n>d.Name = \"Рекс\";", listOf(
        build("Объяви класс Dog", "public|class|Dog|{", "def|func"),
        type("Свойство Name типа string", "public string Name { get; set; }"),
        build("Создай объект Dog", "var|d|=|new|Dog|(|)|;", "Dog|=="),
        type("Задай имя Рекс", "d.Name = \"Рекс\";"),
        build("Выведи имя", "Console|.|WriteLine|(|d|.|Name|)|;", "ReadLine|print"),
        type("Метод Bark, ничего не возвращает", "public void Bark() {")
    )),
    Lesson("LINQ", "LINQ — запросы к спискам: Where фильтрует, Select преобразует, Sum и Count считают. Нужен using System.Linq.\n>using System.Linq;\n>var even = nums.Where(n => n % 2 == 0);\n>var sq = nums.Select(n => n * n);\n>int total = nums.Sum();", listOf(
        build("Подключи LINQ", "using|System|.|Linq|;", "import|include"),
        type("Оставь чётные числа", "var even = nums.Where(n => n % 2 == 0);"),
        build("Возведи в квадрат каждый элемент", "var|sq|=|nums|.|Select|(|n|=>|n|*|n|)|;", "Where|map"),
        type("Посчитай сумму nums", "int total = nums.Sum();"),
        build("Преврати результат в список", "nums|.|ToList|(|)", "ToArray|Count"),
        type("Найди первый элемент больше 5", "var x = nums.First(n => n > 5);")
    )),
    Lesson("Словари и строки", "Dictionary хранит пары ключ-значение. Строки в C# удобно склеивать через интерполяцию $\"...\".\n>var d = new Dictionary<string, int>();\n>d[\"a\"] = 1;", listOf(
        type("Создай словарь string-int", "var d = new Dictionary<string, int>();"),
        type("Добавь ключ a со значением 1", "d[\"a\"] = 1;"),
        type("Проверь наличие ключа a", "d.ContainsKey(\"a\")"),
        type("Перебери пары словаря", "foreach (var kv in d)"),
        type("Строка с подстановкой name", "$\"Привет, {name}\""),
        type("Длина строки s", "s.Length")
    )),
    Lesson("Исключения", "try ловит ошибку, catch обрабатывает, finally выполняется всегда. throw создаёт своё исключение.\n>try { int x = int.Parse(s); }\n>catch (FormatException e) { Console.WriteLine(e.Message); }", listOf(
        build("Начни блок try", "try|{", "catch|finally"),
        type("Поймай FormatException", "catch (FormatException e)"),
        type("Выведи сообщение ошибки", "Console.WriteLine(e.Message);"),
        build("Блок, который выполняется всегда", "finally|{", "try|catch"),
        type("Брось своё исключение", "throw new Exception(\"Ошибка\");"),
        type("Преобразуй строку в число", "int x = int.Parse(s);")
    ))
))

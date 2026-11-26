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
    )),
    Lesson("Switch и тернарный оператор", "switch выбирает ветку по значению, break завершает ветку. В C# есть switch-выражение. Тернарный оператор: условие ? да : нет.\n>switch (x) {\n>  case 1: Console.WriteLine(\"один\"); break;\n>  default: Console.WriteLine(\"другое\"); break;\n>}\n>int m = a > b ? a : b;", listOf(
        type("Начни switch по x", "switch (x) {"),
        type("Ветка для значения 1", "case 1:"),
        type("Выйди из ветки", "break;"),
        type("Ветка по умолчанию", "default:"),
        type("Максимум из a и b тернарным оператором", "int m = a > b ? a : b;"),
        type("Switch-выражение: 1 это один, иначе другое", "var r = x switch { 1 => \"один\", _ => \"другое\" };")
    )),
    Lesson("Массивы", "Массив имеет фиксированный размер. Индексы с нуля. Length даёт размер, Array.Sort сортирует, foreach перебирает.\n>int[] a = new int[3];\n>int[] b = { 1, 2, 3 };\n>a[0] = 5;\n>Array.Sort(b);", listOf(
        type("Создай массив из 3 int", "int[] a = new int[3];"),
        type("Массив из 1, 2, 3", "int[] b = { 1, 2, 3 };"),
        type("Задай первому элементу 5", "a[0] = 5;"),
        type("Размер массива a", "a.Length"),
        type("Отсортируй массив b", "Array.Sort(b);"),
        type("Переверни массив b", "Array.Reverse(b);")
    )),
    Lesson("Строки: методы и StringBuilder", "Строки неизменяемые: каждое изменение создаёт новую. Методы: ToUpper, Trim, Split, Contains, Replace, Substring. Для многих склеек есть StringBuilder.\n>s.ToUpper();\n>s.Split(',');\n>s.Contains(\"ab\");\n>var sb = new StringBuilder();\n>sb.Append(\"a\");", listOf(
        type("Верхний регистр s", "s.ToUpper()"),
        type("Убери пробелы по краям", "s.Trim()"),
        type("Раздели строку по запятой", "s.Split(',')"),
        type("Проверь, содержит ли s ab", "s.Contains(\"ab\")"),
        type("Создай StringBuilder", "var sb = new StringBuilder();"),
        type("Добавь a в sb", "sb.Append(\"a\");")
    )),
    Lesson("Интерфейсы и абстрактные классы", "Интерфейс описывает набор методов без реализации. Абстрактный класс нельзя создать, но от него наследуют. Метод переопределяют через override.\n>interface IAnimal { void Speak(); }\n>abstract class Base { public abstract void Run(); }\n>class Dog : Base { public override void Run() {} }", listOf(
        type("Интерфейс IAnimal с методом Speak", "interface IAnimal { void Speak(); }"),
        type("Абстрактный класс Base", "abstract class Base"),
        type("Абстрактный метод Run", "public abstract void Run();"),
        type("Класс Dog наследует Base", "class Dog : Base"),
        type("Переопредели Run", "public override void Run() {}"),
        type("Класс реализует интерфейс IAnimal", "class Cat : IAnimal")
    )),
    Lesson("async и await", "async-метод может ждать долгие операции через await и не блокирует программу. Он возвращает Task. Task.Delay делает паузу.\n>async Task Main() {\n>  await Task.Delay(1000);\n>  Console.WriteLine(\"готово\");\n>}", listOf(
        type("Асинхронный метод Main, возвращающий Task", "async Task Main() {"),
        type("Подожди секунду", "await Task.Delay(1000);"),
        type("Подключи System.Threading.Tasks", "using System.Threading.Tasks;"),
        type("Запусти задачу в фоне", "Task.Run(() => Work());"),
        type("Дождись всех задач", "await Task.WhenAll(t1, t2);"),
        type("Метод вернёт число асинхронно", "async Task<int> GetAsync() {")
    )),
    Lesson("Лямбды и делегаты", "Делегат это тип для ссылки на метод. Func принимает параметры и возвращает значение, Action ничего не возвращает. Лямбда: x => выражение.\n>Func<int, int> sq = x => x * x;\n>Action hi = () => Console.WriteLine(\"hi\");\n>sq(5);", listOf(
        type("Func квадрата x", "Func<int, int> sq = x => x * x;"),
        type("Action, пишущий hi", "Action hi = () => Console.WriteLine(\"hi\");"),
        type("Вызови sq с 5", "sq(5)"),
        type("Func сложения двух чисел", "Func<int, int, int> add = (a, b) => a + b;"),
        type("Событие Click", "public event Action Click;"),
        type("Вызови событие, если есть подписчики", "Click?.Invoke();")
    )),
    Lesson("Файлы: File и Path", "Класс File читает и пишет файлы: ReadAllText, WriteAllText, Exists. Path работает с путями. Нужен using System.IO.\n>using System.IO;\n>File.WriteAllText(\"a.txt\", \"привет\");\n>var t = File.ReadAllText(\"a.txt\");\n>File.Exists(\"a.txt\");", listOf(
        type("Подключи System.IO", "using System.IO;"),
        type("Запиши привет в a.txt", "File.WriteAllText(\"a.txt\", \"привет\");"),
        type("Прочитай весь a.txt", "File.ReadAllText(\"a.txt\")"),
        type("Проверь, существует ли a.txt", "File.Exists(\"a.txt\")"),
        type("Прочитай строки файла", "File.ReadAllLines(\"a.txt\")"),
        type("Склей путь папки и файла", "Path.Combine(\"dir\", \"a.txt\")")
    )),
    Lesson("null и безопасность", "Ссылка может быть null. ?. безопасно вызывает метод, ?? подставляет значение по умолчанию, ??= присваивает, если null. is null проверяет.\n>string s = null;\n>int n = s?.Length ?? 0;\n>s ??= \"x\";\n>if (s is null) {}", listOf(
        type("Длина s или 0, если s null", "int n = s?.Length ?? 0;"),
        type("Присвой x, если s равна null", "s ??= \"x\";"),
        type("Проверь, что s null", "s is null"),
        type("Проверь, что s не null", "s is not null"),
        type("Строка, которая может быть null", "string? s = null;"),
        type("Значение по умолчанию для int", "default(int)")
    )),
    Lesson("record, struct и enum", "record это короткий неизменяемый класс данных, struct хранится по значению, enum задаёт набор имён. Обычный класс подходит для сложной логики.\n>record Point(int X, int Y);\n>struct Vec { public int X; }\n>enum Color { Red, Green, Blue }", listOf(
        type("Record Point с X и Y", "record Point(int X, int Y);"),
        type("Создай Point 1 и 2", "var p = new Point(1, 2);"),
        type("Структура Vec с полем X", "struct Vec { public int X; }"),
        type("Перечисление Color", "enum Color { Red, Green, Blue }"),
        type("Переменная c равна Green", "Color c = Color.Green;"),
        type("Скопируй record с изменением X", "var q = p with { X = 5 };")
    )),
    Lesson("Обобщения (generics)", "Обобщённый класс или метод работает с любым типом T. List<T>, Dictionary<K,V> уже сделаны так. where ограничивает тип.\n>class Box<T> { public T Value; }\n>var b = new Box<int>();\n>T Max<T>(T a, T b) where T : IComparable<T>", listOf(
        type("Класс Box с типом T", "class Box<T>"),
        type("Поле Value типа T", "public T Value;"),
        type("Создай Box для int", "var b = new Box<int>();"),
        type("Метод Max для типа T", "T Max<T>(T a, T b)"),
        type("Ограничение: T сравнимый", "where T : IComparable<T>"),
        type("Список строк", "var list = new List<string>();")
    )),
    Lesson("using и IDisposable", "Объекты с ресурсами (файлы, соединения) реализуют IDisposable. using гарантирует вызов Dispose, даже если случилась ошибка.\n>using (var r = new StreamReader(\"a.txt\")) {\n>  Console.WriteLine(r.ReadToEnd());\n>}\n>using var w = new StreamWriter(\"b.txt\");", listOf(
        type("Открой StreamReader в блоке using", "using (var r = new StreamReader(\"a.txt\")) {"),
        type("Прочитай всё из r", "r.ReadToEnd()"),
        type("Короткий using для StreamWriter", "using var w = new StreamWriter(\"b.txt\");"),
        type("Запиши строку в w", "w.WriteLine(\"привет\");"),
        type("Освободи ресурс вручную", "r.Dispose();"),
        type("Интерфейс очистки ресурсов", "IDisposable")
    )),
    Lesson("Свойства, индексаторы и static", "Свойство с get и set даёт контроль доступа. static принадлежит классу, а не объекту. readonly нельзя менять после создания.\n>public int Age { get; private set; }\n>public static int Count;\n>public readonly int Id;", listOf(
        type("Свойство Age, set только внутри класса", "public int Age { get; private set; }"),
        type("Статическое поле Count", "public static int Count;"),
        type("Поле Id только для чтения", "public readonly int Id;"),
        type("Статический метод Hello, ничего не возвращает", "public static void Hello() {}"),
        type("Константа Pi равная 3.14", "public const double Pi = 3.14;"),
        type("Вызови статический метод Hello класса Util", "Util.Hello();")
    )),
    Lesson("JSON: System.Text.Json", "Встроенный класс JsonSerializer превращает объект в строку JSON и обратно. Serialize пишет, Deserialize читает. Свойства класса становятся полями JSON.\n>using System.Text.Json;\n>string s = JsonSerializer.Serialize(user);\n>var u = JsonSerializer.Deserialize<User>(s);", listOf(
        type("Подключи пространство имён JSON", "using System.Text.Json;"),
        type("Преврати user в строку JSON", "string s = JsonSerializer.Serialize(user);"),
        type("Прочитай User из строки s", "var u = JsonSerializer.Deserialize<User>(s);"),
        type("Прочитай весь файл data.json", "File.ReadAllText(\"data.json\")"),
        type("Запиши строку s в файл out.json", "File.WriteAllText(\"out.json\", s);"),
        type("Свойство Name типа string", "public string Name { get; set; }")
    )),
    Lesson("ASP.NET: минимальный API", "Минимальный веб-API создаётся в нескольких строках. WebApplication.CreateBuilder готовит приложение, MapGet описывает адрес, Run запускает сервер. Создать проект: dotnet new web.\n>var builder = WebApplication.CreateBuilder(args);\n>var app = builder.Build();\n>app.MapGet(\"/\", () => \"Привет\");\n>app.Run();", listOf(
        type("Создай проект веб-приложения", "dotnet new web"),
        type("Подготовь builder", "var builder = WebApplication.CreateBuilder(args);"),
        type("Собери приложение", "var app = builder.Build();"),
        type("Опиши GET / с ответом Привет", "app.MapGet(\"/\", () => \"Привет\");"),
        type("Опиши POST /add", "app.MapPost(\"/add\", (Item i) => i);"),
        type("Запусти сервер", "app.Run();")
    )),
    Lesson("Коллекции: HashSet, Queue, Stack", "HashSet хранит только уникальные значения, Add возвращает false для повтора. Queue работает по очереди: Enqueue кладёт, Dequeue берёт первый. Stack берёт последний: Push и Pop.\n>var set = new HashSet<int>();\n>set.Add(5);\n>var q = new Queue<string>();\n>q.Enqueue(\"a\");\n>var s = new Stack<int>();\n>s.Push(1);", listOf(
        type("Создай множество целых", "var set = new HashSet<int>();"),
        type("Добавь 5 в множество", "set.Add(5);"),
        type("Создай очередь строк", "var q = new Queue<string>();"),
        type("Положи a в очередь", "q.Enqueue(\"a\");"),
        type("Возьми первый из очереди", "var x = q.Dequeue();"),
        type("Положи 1 в стек", "s.Push(1);")
    )),
    Lesson("LINQ: группировка и агрегаты", "LINQ умеет считать и группировать. Sum, Average, Max, Min считают по числам, Count считает элементы, GroupBy собирает группы по ключу, Any проверяет наличие, First берёт первый.\n>using System.Linq;\n>int total = nums.Sum();\n>double avg = nums.Average();\n>var groups = words.GroupBy(w => w.Length);\n>bool has = nums.Any(n => n > 5);", listOf(
        type("Подключи LINQ", "using System.Linq;"),
        type("Посчитай сумму nums", "int total = nums.Sum();"),
        type("Найди среднее nums", "double avg = nums.Average();"),
        type("Сгруппируй слова по длине", "var groups = words.GroupBy(w => w.Length);"),
        type("Проверь, есть ли число больше 5", "bool has = nums.Any(n => n > 5);"),
        type("Возьми первый элемент", "var first = nums.First();")
    ))
))

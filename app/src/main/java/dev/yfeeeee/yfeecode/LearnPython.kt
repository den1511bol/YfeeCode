// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun pythonCourse() = Course("py", "Python", "🐍", listOf(
    Lesson("Вывод: print", "Команда print показывает текст или число на экране.\nТекст всегда пишут в кавычках.\n>print(\"Привет\")\n>print(2 + 3)", listOf(
        build("Выведи слово Привет", "print|(|\"Привет\"|)", "input|len", "print выводит значение в скобках."),
        build("Выведи число 5", "print|(|5|)", "\"5\"|echo"),
        type("Напиши команду, которая выводит Hello", "print(\"Hello\")"),
        build("Выведи сумму 2 и 3", "print|(|2|+|3|)", "-|*"),
        type("Выведи результат 4 * 5", "print(4 * 5)"),
        type("Выведи слово Python", "print(\"Python\")")
    )),
    Lesson("Переменные", "Переменная хранит значение. Знак = кладёт значение в имя.\n>name = \"Аня\"\n>age = 18\n>print(name)", listOf(
        build("Создай переменную x со значением 10", "x|=|10", "==|:"),
        type("Создай переменную age со значением 18", "age = 18"),
        build("Выведи переменную name", "print|(|name|)", "\"name\"|input"),
        type("Запиши в переменную name текст Bob", "name = \"Bob\""),
        build("Увеличь score на 1", "score|=|score|+|1", "-|=="),
        type("Выведи переменную score", "print(score)")
    )),
    Lesson("Условия: if", "if проверяет условие. После условия ставится двоеточие, а тело блока пишут с отступом.\n>if age >= 18:\n>    print(\"Взрослый\")\n>else:\n>    print(\"Ребёнок\")", listOf(
        build("Если x больше 5", "if|x|>|5|:", "=|elif|for", "Не забудь двоеточие в конце."),
        type("Если age больше или равно 18", "if age >= 18:"),
        build("Иначе, если x равен 0", "elif|x|==|0|:", "=|else|if"),
        type("Напиши ветку else", "else:"),
        build("Проверка на равенство a и b", "if|a|==|b|:", "=|!=|then"),
        type("Если name равно \"Аня\"", "if name == \"Аня\":")
    )),
    Lesson("Циклы", "for повторяет действие нужное число раз, while повторяет, пока условие верно.\n>for i in range(3):\n>    print(i)\n>while n < 10:\n>    n = n + 1", listOf(
        build("Повтори 5 раз", "for|i|in|range|(|5|)|:", "while|len|=="),
        type("Цикл i от 0 до 2", "for i in range(3):"),
        build("Пока n меньше 10", "while|n|<|10|:", "for|in|>"),
        type("Бесконечный цикл", "while True:", explain = "True — значение «истина», условие всегда верно."),
        build("Перебери элементы списка items", "for|x|in|items|:", "range|while"),
        type("Прерви цикл", "break")
    )),
    Lesson("Функции", "Функция — именованный блок кода. def создаёт её, return возвращает результат.\n>def add(a, b):\n>    return a + b\n>print(add(2, 3))", listOf(
        build("Объяви функцию hello без параметров", "def|hello|(|)|:", "func|function"),
        type("Объяви функцию add с параметрами a и b", "def add(a, b):"),
        build("Верни сумму a и b", "return|a|+|b", "print|=="),
        type("Верни x умноженное на 2", "return x * 2"),
        build("Вызови функцию hello", "hello|(|)", "def|:"),
        type("Выведи результат add(2, 3)", "print(add(2, 3))")
    )),
    Lesson("Списки", "Список хранит несколько значений в квадратных скобках.\n>nums = [1, 2, 3]\n>nums.append(4)\n>print(len(nums))", listOf(
        build("Создай список nums из 1, 2, 3", "nums|=|[|1|,|2|,|3|]", "(|)"),
        type("Добавь 4 в конец списка nums", "nums.append(4)"),
        build("Узнай длину списка nums", "len|(|nums|)", "size|count"),
        type("Выведи длину списка nums", "print(len(nums))"),
        build("Возьми первый элемент списка nums", "nums|[|0|]", "(|1"),
        type("Выведи последний элемент nums", "print(nums[-1])")
    )),
    Lesson("Telegram-боты и модули", "Для бота на pyTelegramBotAPI нужен токен, объект бота и обработчики сообщений.\n>import telebot\n>bot = telebot.TeleBot(TOKEN)\n>@bot.message_handler(commands=[\"start\"])\n>bot.infinity_polling()\nМодуль Heroku — класс от loader.Module с командами.\n>@loader.command()", listOf(
        build("Подключи библиотеку telebot", "import|telebot", "from|bot"),
        type("Создай бота из TOKEN", "bot = telebot.TeleBot(TOKEN)"),
        build("Обработчик команды /start", "@|bot|.|message_handler|(|commands|=|[|\"start\"|]|)", "def|:"),
        type("Запусти опрос сообщений", "bot.infinity_polling()"),
        build("Команда модуля Heroku", "@|loader|.|command|(|)", "def|class"),
        type("Объяви класс модуля MyMod", "class MyMod(loader.Module):")
    )),
    Lesson("Словари и файлы", "Словарь хранит пары «ключ: значение». Файлы читают и пишут через open и with.\n>user = {\"name\": \"Аня\", \"age\": 18}\n>print(user[\"name\"])\n>with open(\"a.txt\", \"w\") as f:\n>    f.write(\"Привет\")", listOf(
        build("Создай словарь с ключом name", "user|=|{|\"name\"|:|\"Аня\"|}", "[|(|=="),
        type("Выведи значение по ключу name", "print(user[\"name\"])"),
        build("Открой файл на запись", "with|open|(|\"a.txt\"|,|\"w\"|)|as|f|:", "def|import"),
        type("Запиши строку в файл f", "f.write(\"Привет\")"),
        build("Прочитай весь файл", "text|=|f|.|read|(|)", "write|open"),
        type("Пройди по парам словаря user", "for k, v in user.items():")
    )),
    Lesson("Классы и объекты", "Класс описывает объект. Метод __init__ вызывается при создании, self — сам объект.\n>class Dog:\n>    def __init__(self, name):\n>        self.name = name\n>    def bark(self):\n>        print(self.name)\n>d = Dog(\"Рекс\")", listOf(
        build("Объяви класс Dog", "class|Dog|:", "def|object"),
        type("Метод __init__ с параметром name", "def __init__(self, name):"),
        build("Сохрани имя в объекте", "self|.|name|=|name", "def|=="),
        type("Создай объект Dog с именем Рекс", "d = Dog(\"Рекс\")"),
        build("Вызови метод bark у d", "d|.|bark|(|)", "Dog|self"),
        type("Метод bark с параметром self", "def bark(self):")
    )),
    Lesson("Асинхронность и aiogram", "async def создаёт корутину, await ждёт её результат. На этом построены aiogram и pyrogram.\n>import asyncio\n>async def main():\n>    await asyncio.sleep(1)\n>asyncio.run(main())\n>from aiogram import Bot, Dispatcher, executor", listOf(
        build("Объяви асинхронную функцию main", "async|def|main|(|)|:", "await|class"),
        type("Подожди 1 секунду", "await asyncio.sleep(1)"),
        build("Запусти main", "asyncio|.|run|(|main|(|)|)", "await|def"),
        type("Подключи aiogram", "from aiogram import Bot, Dispatcher, executor"),
        build("Обработчик команды start в aiogram", "@|dp|.|message_handler|(|commands|=|[|\"start\"|]|)", "bot|def"),
        type("Ответь пользователю в aiogram", "await message.answer(\"Привет\")")
    )),
    Lesson("Ошибки: try/except", "try пробует выполнить код, а except ловит ошибку, чтобы программа не упала. finally выполняется всегда.\n>try:\n>    x = int(\"abc\")\n>except ValueError:\n>    print(\"Не число\")\n>finally:\n>    print(\"Готово\")", listOf(
        build("Начни блок try", "try|:", "except|def"),
        type("Лови ошибку ValueError", "except ValueError:"),
        build("Лови любую ошибку и сохрани в e", "except|Exception|as|e|:", "try|finally"),
        type("Блок, который выполняется всегда", "finally:"),
        build("Вызови своё исключение", "raise|ValueError|(|\"плохо\"|)", "try|catch"),
        type("Выведи текст ошибки e", "print(e)")
    )),
    Lesson("Модули и импорт", "Модуль — это файл с кодом. import подключает его, from … import берёт отдельные имена.\n>import math\n>print(math.sqrt(16))\n>from random import randint\n>import requests", listOf(
        build("Подключи модуль math", "import|math", "from|def"),
        type("Выведи корень из 16", "print(math.sqrt(16))"),
        build("Возьми randint из random", "from|random|import|randint", "import|as"),
        type("Подключи requests под именем r", "import requests as r"),
        build("Случайное число от 1 до 6", "randint|(|1|,|6|)", "range|len"),
        type("Подключи json", "import json")
    )),
    Lesson("Сортировка и поиск", "sorted() возвращает отсортированный список, .sort() сортирует на месте. min и max находят крайние значения, in проверяет наличие.\n>nums = [3, 1, 2]\n>print(sorted(nums))\n>print(max(nums))", listOf(
        build("Отсортируй список nums", "sorted|(|nums|)", "sort|len"),
        type("Отсортируй по убыванию", "sorted(nums, reverse=True)"),
        build("Найди наибольшее число", "max|(|nums|)", "min|sum"),
        type("Проверь, есть ли 5 в списке", "5 in nums"),
        build("Отсортируй список на месте", "nums|.|sort|(|)", "sorted|reverse"),
        type("Найди позицию числа 2 в списке", "nums.index(2)")
    )),
    Lesson("Включения и файлы", "Списковое включение создаёт список в одну строку. Файлы читают через with open(...), он сам закрывает файл.\n>sq = [x * x for x in range(5)]\n>with open(\"a.txt\") as f:\n>    data = f.read()", listOf(
        build("Квадраты чисел от 0 до 4", "[|x|*|x|for|x|in|range|(|5|)|]", "while|def"),
        type("Только чётные из nums", "[x for x in nums if x % 2 == 0]"),
        build("Открой файл a.txt на чтение", "with|open|(|\"a.txt\"|)|as|f|:", "def|import"),
        type("Прочитай весь файл", "data = f.read()"),
        build("Запиши текст в файл", "f|.|write|(|\"Привет\"|)", "read|close"),
        type("Открой файл на запись", "with open(\"a.txt\", \"w\") as f:")
    )),
    Lesson("Наследование и декораторы", "Класс-наследник берёт всё у родителя и может переопределять методы. Декоратор оборачивает функцию: над ней пишут @имя.\n>class Dog(Animal):\n>    def speak(self):\n>        return super().speak()", listOf(
        build("Класс Dog наследует Animal", "class|Dog|(|Animal|)|:", "def|import"),
        type("Вызови метод родителя", "super().speak()"),
        build("Конструктор с вызовом родителя", "super|(|)|.|__init__|(|)", "self|class"),
        type("Объяви декоратор-функцию deco", "def deco(func):"),
        build("Примени декоратор deco к функции", "@|deco", "def|class"),
        type("Проверь, что d — это Dog", "isinstance(d, Dog)")
    )),
    Lesson("Строки и f-строки", "Строки можно склеивать, умножать и форматировать. f-строка подставляет переменные прямо в текст. Методы: upper, lower, strip, split, replace.\n>name = \"Аня\"\n>print(f\"Привет, {name}!\")\n>s.upper()  # ПРИВЕТ\n>\"a,b\".split(\",\")  # ['a', 'b']", listOf(
        build("Выведи Привет с именем из переменной name", "print|(|f\"Привет, {name}\"|)", "format|str"),
        type("Переведи s в верхний регистр", "s.upper()"),
        type("Убери пробелы по краям s", "s.strip()"),
        type("Раздели строку s по запятой", "s.split(\",\")"),
        type("Замени a на b в строке s", "s.replace(\"a\", \"b\")"),
        type("Длина строки s", "len(s)")
    )),
    Lesson("Числа и математика", "Python умеет считать: + - * /, деление нацело //, остаток %, степень **. Функции round, abs, min, max, sum. Модуль math даёт sqrt, pi и другое.\n>7 // 2   # 3\n>7 % 2    # 1\n>2 ** 10  # 1024\n>import math\n>math.sqrt(16)  # 4.0", listOf(
        type("Остаток от деления 7 на 2", "7 % 2"),
        type("Целочисленное деление 7 на 2", "7 // 2"),
        type("2 в степени 10", "2 ** 10"),
        type("Округли x до двух знаков", "round(x, 2)"),
        type("Подключи модуль math", "import math"),
        type("Корень из 16 через math", "math.sqrt(16)")
    )),
    Lesson("Кортежи и множества", "Кортеж (tuple) нельзя менять. Множество (set) хранит только уникальные значения. Оба удобны, когда список не нужен.\n>t = (1, 2, 3)\n>s = {1, 2, 2, 3}  # {1, 2, 3}\n>s.add(4)\n>2 in s  # True", listOf(
        type("Создай кортеж из 1, 2, 3", "t = (1, 2, 3)"),
        type("Создай множество из 1, 2, 3", "s = {1, 2, 3}"),
        type("Добавь 4 в множество s", "s.add(4)"),
        type("Проверь, что 2 есть в s", "2 in s"),
        type("Преврати список lst в множество", "set(lst)"),
        type("Распакуй кортеж в a и b", "a, b = t")
    )),
    Lesson("Срезы и перебор", "Срез берёт часть списка или строки: s[1:3]. Отрицательный индекс считает с конца. range создаёт числа, enumerate даёт индекс, zip склеивает списки.\n>s[1:3]\n>s[::-1]   # развернуть\n>for i, x in enumerate(lst):\n>for a, b in zip(l1, l2):", listOf(
        type("Элементы с 1 по 2 включительно", "s[1:3]"),
        type("Последний элемент списка s", "s[-1]"),
        type("Разверни список s срезом", "s[::-1]"),
        type("Перебор с индексом i и значением x", "for i, x in enumerate(lst):"),
        type("Перебери два списка вместе", "for a, b in zip(l1, l2):"),
        type("Числа от 0 до 4", "range(5)")
    )),
    Lesson("Функции: *args, lambda", "*args собирает любое число аргументов, **kwargs собирает именованные. lambda создаёт короткую функцию. У параметров бывают значения по умолчанию.\n>def f(a, b=2):\n>def g(*args, **kwargs):\n>sq = lambda x: x * x", listOf(
        type("Функция f с параметром b=2 по умолчанию", "def f(a, b=2):"),
        type("Функция, принимающая любое число аргументов", "def g(*args):"),
        type("Функция с именованными аргументами", "def h(**kwargs):"),
        type("Лямбда: квадрат числа x", "sq = lambda x: x * x"),
        type("Вызови f с b равным 5", "f(1, b=5)"),
        type("Отсортируй список по длине строк", "sorted(words, key=len)")
    )),
    Lesson("Генераторы и итераторы", "Генератор отдаёт значения по одному через yield и не хранит всё в памяти. Генераторное выражение похоже на включение, но в круглых скобках. next берёт следующее значение.\n>def count():\n>    yield 1\n>    yield 2\n>g = (x * x for x in range(5))\n>next(g)", listOf(
        type("Отдай значение 1 из генератора", "yield 1"),
        type("Генераторное выражение квадратов чисел 0-4", "g = (x * x for x in range(5))"),
        type("Возьми следующее значение генератора g", "next(g)"),
        type("Преврати генератор g в список", "list(g)"),
        type("Сумма квадратов без списка", "sum(x * x for x in range(5))"),
        type("Получи итератор из списка lst", "iter(lst)")
    )),
    Lesson("Файлы и JSON", "with открывает файл и сам закрывает его. json превращает словарь в текст и обратно: dumps, loads, dump, load. Для русского текста указывай encoding и ensure_ascii=False.\n>with open(\"a.txt\", \"w\", encoding=\"utf-8\") as f:\n>    f.write(\"Привет\")\n>import json\n>data = json.loads(text)", listOf(
        type("Открой a.txt для записи как f", "with open(\"a.txt\", \"w\") as f:"),
        type("Прочитай весь файл f", "f.read()"),
        type("Подключи json", "import json"),
        type("Преврати строку text в словарь", "data = json.loads(text)"),
        type("Преврати словарь data в строку", "json.dumps(data)"),
        type("Дописывай в конец файла (режим)", "open(\"a.txt\", \"a\")")
    )),
    Lesson("Стандартная библиотека", "Полезные модули: random (случайные числа), datetime (даты), os (файлы и пути), time (пауза), sys (аргументы).\n>import random\n>random.randint(1, 6)\n>from datetime import datetime\n>datetime.now()\n>import os\n>os.path.exists(\"a.txt\")", listOf(
        type("Случайное число от 1 до 6", "random.randint(1, 6)"),
        type("Случайный элемент списка lst", "random.choice(lst)"),
        type("Подключи datetime из datetime", "from datetime import datetime"),
        type("Текущее время", "datetime.now()"),
        type("Проверь, существует ли файл a.txt", "os.path.exists(\"a.txt\")"),
        type("Подожди 2 секунды", "time.sleep(2)")
    )),
    Lesson("Регулярные выражения", "Модуль re ищет шаблоны в тексте. \\d цифра, \\w буква, + один или больше, * ноль или больше. search ищет первое совпадение, findall все, sub заменяет.\n>import re\n>re.findall(r\"\\d+\", text)\n>re.sub(r\"\\s+\", \" \", text)", listOf(
        type("Подключи модуль re", "import re"),
        type("Найди все числа в text", "re.findall(r\"\\d+\", text)"),
        type("Найди первое совпадение слова", "re.search(r\"\\w+\", text)"),
        type("Замени пробелы на один пробел", "re.sub(r\"\\s+\", \" \", text)"),
        type("Проверь начало строки на число", "re.match(r\"\\d\", text)"),
        type("Цифра в шаблоне (одна)", "\\d")
    )),
    Lesson("Классы: dataclass и свойства", "dataclass сам создаёт __init__ и вывод. @property делает метод похожим на поле. Подсказки типов не обязательны, но помогают читать код.\n>from dataclasses import dataclass\n>@dataclass\n>class User:\n>    name: str\n>    age: int = 0", listOf(
        type("Подключи dataclass", "from dataclasses import dataclass"),
        type("Декоратор dataclass над классом", "@dataclass"),
        type("Поле name типа str", "name: str"),
        type("Поле age типа int по умолчанию 0", "age: int = 0"),
        type("Метод, доступный как свойство", "@property"),
        type("Создай User с именем Аня", "u = User(\"Аня\")")
    )),
    Lesson("Контекстные менеджеры и ошибки", "with гарантирует закрытие ресурса. raise бросает свою ошибку, finally выполняется всегда. Свой тип ошибки — класс от Exception. assert проверяет условие при отладке.\n>try:\n>    x = int(s)\n>except ValueError as e:\n>    print(e)\n>finally:\n>    print(\"конец\")", listOf(
        type("Поймай ValueError как e", "except ValueError as e:"),
        type("Блок, который выполнится всегда", "finally:"),
        type("Брось ошибку с текстом", "raise ValueError(\"плохо\")"),
        type("Свой тип ошибки", "class MyError(Exception):"),
        type("Проверь, что x больше 0", "assert x > 0"),
        type("Пропусти действие в блоке", "pass")
    )),
    Lesson("Виртуальные среды и pip", "pip ставит библиотеки. venv создаёт отдельную среду для проекта, чтобы версии не конфликтовали. requirements.txt хранит список зависимостей.\n>pip install requests\n>python3 -m venv venv\n>source venv/bin/activate\n>pip freeze > requirements.txt", listOf(
        type("Установи requests через pip", "pip install requests"),
        type("Создай среду venv", "python3 -m venv venv"),
        type("Включи среду", "source venv/bin/activate"),
        type("Сохрани список пакетов", "pip freeze > requirements.txt"),
        type("Установи пакеты из файла", "pip install -r requirements.txt"),
        type("Покажи установленные пакеты", "pip list")
    ))
))

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
    ))
))

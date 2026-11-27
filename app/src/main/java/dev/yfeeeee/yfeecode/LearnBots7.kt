// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons7(): List<Lesson> = listOf(
    Lesson("Что такое модуль Heroku/Hikka", "Модуль — это файл Python, который добавляет команды юзерботу Heroku (Hikka). Команды запускают из любого чата, например .hello.\nМодуль — класс, который наследует loader.Module, а команды — его методы с декоратором @loader.command().\nПроверить модуль без Telegram можно командой mod файл.py .команда.", listOf(
        choice("Что такое модуль Heroku/Hikka?", "Файл Python с командами для юзербота", "Отдельный бот в BotFather", "Приложение на Heroku", explain = "Команды запускают из любого чата, например .hello."),
        choice("От какого класса наследует модуль?", "loader.Module", "utils.Module", "telebot.Bot"),
        gap("Дополни: модуль это класс", "class HelloMod(loader.___):", "Module", "Bot|Command"),
        gap("Дополни: импорт", "from .. import ___, utils", "loader", "telebot|aiogram", explain = "loader и utils нужны почти каждому модулю."),
        fix("Исправь: класс объявляют ключевым словом class", "klass HelloMod(loader.Module):", "class HelloMod(loader.Module):"),
        build("Импортируй loader и utils", "from|..|import|loader|,|utils", "import|telebot"),
        type("Объяви модуль MyMod на основе loader.Module", "class MyMod(loader.Module):"),
        build("Декоратор класса модуля", "@loader|.|tds", "@bot|.|command"),
        build("Проверь модуль в терминале", "mod|MyMod.py|.hello", "run|MyMod.py"),
        type("Покажи команды модуля", "mod MyMod.py")
    )),
    Lesson("Первая команда", "Команда — метод, имя которого заканчивается на cmd.\n>@loader.command()\n>async def hellocmd(self, message):\n>    \"\"\"Поздороваться\"\"\"\n>    await utils.answer(message, \"Привет!\")\nИмя hellocmd даёт команду .hello. Текст в тройных кавычках — описание команды в справке.", listOf(
        choice("Как называется метод команды .hello?", "hellocmd", "hello_command", "cmd_hello", explain = "В Heroku имя метода команды заканчивается на cmd."),
        choice("Что делает utils.answer?", "Отвечает или редактирует сообщение", "Читает аргументы", "Удаляет модуль"),
        gap("Дополни: декоратор команды", "@loader.___()", "command", "tds|cmd2"),
        gap("Дополни: ответ на сообщение", "await utils.___(message, \"Привет!\")", "answer", "reply|send"),
        fix("Исправь: метод команды должен быть async", "def hellocmd(self, message):", "async def hellocmd(self, message):"),
        build("Декоратор команды", "@loader|.|command|(|)", "@loader|.|tds"),
        build("Объяви команду hello", "async|def|hellocmd|(|self|,|message|)|:", "def|hello"),
        type("Ответь в чат словом Привет", "await utils.answer(message, \"Привет\")"),
        build("Какая команда получится из hellocmd", ".hello", ".hellocmd|hello"),
        type("Запусти команду hello в песочнице", "mod MyMod.py .hello")
    )),
    Lesson("Аргументы команды", "Всё, что человек написал после команды, достаём через utils.get_args_raw.\n>args = utils.get_args_raw(message)\n>if not args:\n>    await utils.answer(message, \"Напиши аргумент\")\n>    return\nget_args возвращает список слов, а get_args_raw — строку целиком.", listOf(
        choice("Что возвращает get_args_raw?", "Строку целиком после команды", "Список слов", "Номер чата", explain = "get_args возвращает список слов."),
        choice("Что делать, если аргумента нет?", "Показать подсказку и сделать return", "Упасть с ошибкой", "Ничего"),
        gap("Дополни: прочитай аргументы", "args = utils.___(message)", "get_args_raw", "get_args2|args"),
        gap("Дополни: выйди из функции", "if not args:\n    await utils.answer(message, \"Напиши аргумент\")\n    ___", "return", "break|pass"),
        fix("Исправь: если аргументов нет, нужен not", "if args:\n    return", "if not args:", explain = "not args значит «аргументов нет»."),
        build("Получи аргументы строкой", "args|=|utils|.|get_args_raw|(|message|)", "message|.|args"),
        build("Получи аргументы списком слов", "utils|.|get_args|(|message|)", "get_args_raw|split"),
        build("Если аргументов нет", "if|not|args|:", "if|args|=="),
        type("Выйди из функции", "return"),
        type("Ответь аргументами обратно", "await utils.answer(message, args)")
    )),
    Lesson("Строки и перевод", "Тексты ответов хранят в словаре strings, чтобы их было легко менять.\n>strings = {\"name\": \"MyMod\", \"hello\": \"Привет!\"}\n>await utils.answer(message, self.strings(\"hello\"))\nКлюч name — обязательное имя модуля.", listOf(
        choice("Зачем нужен словарь strings?", "Тексты ответов легко менять и переводить", "Он хранит токен", "Он запускает команды", explain = "Ключ name обязателен: это имя модуля."),
        choice("Как достать текст из strings?", "self.strings(\"hello\")", "strings.hello()", "get(strings)"),
        gap("Дополни: обязательный ключ", "strings = {\"___\": \"MyMod\"}", "name", "title|id"),
        gap("Дополни: достань строку", "await utils.answer(message, self.___(\"hello\"))", "strings", "string2|text"),
        fix("Исправь: strings это словарь с фигурными скобками", "strings = [\"name\": \"MyMod\"]", "strings = {\"name\": \"MyMod\"}"),
        build("Словарь строк модуля", "strings|=|{|\"name\"|:|\"MyMod\"|}", "string|=|["),
        build("Возьми строку hello", "self|.|strings|(|\"hello\"|)", "strings|[|]"),
        type("Ответь строкой hello", "await utils.answer(message, self.strings(\"hello\"))"),
        build("Обязательный ключ словаря", "\"name\"", "\"hello\"|\"text\"", "Без ключа name модуль не загрузится."),
        type("Добавь ключ err со значением Ошибка", "\"err\": \"Ошибка\"")
    )),
    Lesson("Ошибки и отладка модуля", "Ошибки в модуле пишутся в лог Heroku. В песочнице YfeeCode ошибка видна сразу в терминале.\n>try:\n>    ...\n>except Exception as e:\n>    await utils.answer(message, f\"Ошибка: {e}\")\nЕсли команда не находится, проверь, что имя метода заканчивается на cmd и стоит декоратор.", listOf(
        choice("Где в Heroku видна ошибка модуля?", "В логе Heroku", "В чате с BotFather", "В файле token.txt"),
        choice("Что делает try/except в команде?", "Показывает ошибку пользователю, а не падает", "Ускоряет команду", "Удаляет модуль", explain = "Так ты сразу видишь причину сбоя."),
        gap("Дополни: поймай ошибку", "___ Exception as e:\n    await utils.answer(message, f\"Ошибка: {e}\")", "except", "catch|finally"),
        gap("Дополни: f-строка с ошибкой", "f\"Ошибка: {___}\"", "e", "error|message"),
        fix("Исправь: команда не найдётся, если имя не заканчивается на cmd", "async def hello(self, message):", "async def hellocmd(self, message):"),
        build("Поймай ошибку", "try|:", "if|else"),
        build("Вторая часть", "except|Exception|as|e|:", "catch|e"),
        type("Ответь ошибкой e", "await utils.answer(message, f\"Ошибка: {e}\")"),
        build("Имя метода команды заканчивается на", "cmd", "_cmd|command"),
        type("Проверь список команд модуля", "mod MyMod.py")
    ))
)

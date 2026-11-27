// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons7(): List<Lesson> = listOf(
    Lesson("Что такое модуль Heroku/Hikka", "Модуль — это файл Python, который добавляет команды юзерботу Heroku (Hikka). Команды запускают из любого чата, например .hello.\nМодуль — класс, который наследует loader.Module, а команды — его методы с декоратором @loader.command().\nПроверить модуль без Telegram можно командой mod файл.py .команда.", listOf(
        build("Импортируй loader и utils", "from|..|import|loader|,|utils", "import|telebot"),
        type("Объяви модуль MyMod на основе loader.Module", "class MyMod(loader.Module):"),
        build("Декоратор класса модуля", "@loader|.|tds", "@bot|.|command"),
        build("Проверь модуль в терминале", "mod|MyMod.py|.hello", "run|MyMod.py"),
        type("Покажи команды модуля", "mod MyMod.py")
    )),
    Lesson("Первая команда", "Команда — метод, имя которого заканчивается на cmd.\n>@loader.command()\n>async def hellocmd(self, message):\n>    \"\"\"Поздороваться\"\"\"\n>    await utils.answer(message, \"Привет!\")\nИмя hellocmd даёт команду .hello. Текст в тройных кавычках — описание команды в справке.", listOf(
        build("Декоратор команды", "@loader|.|command|(|)", "@loader|.|tds"),
        build("Объяви команду hello", "async|def|hellocmd|(|self|,|message|)|:", "def|hello"),
        type("Ответь в чат словом Привет", "await utils.answer(message, \"Привет\")"),
        build("Какая команда получится из hellocmd", ".hello", ".hellocmd|hello"),
        type("Запусти команду hello в песочнице", "mod MyMod.py .hello")
    )),
    Lesson("Аргументы команды", "Всё, что человек написал после команды, достаём через utils.get_args_raw.\n>args = utils.get_args_raw(message)\n>if not args:\n>    await utils.answer(message, \"Напиши аргумент\")\n>    return\nget_args возвращает список слов, а get_args_raw — строку целиком.", listOf(
        build("Получи аргументы строкой", "args|=|utils|.|get_args_raw|(|message|)", "message|.|args"),
        build("Получи аргументы списком слов", "utils|.|get_args|(|message|)", "get_args_raw|split"),
        build("Если аргументов нет", "if|not|args|:", "if|args|=="),
        type("Выйди из функции", "return"),
        type("Ответь аргументами обратно", "await utils.answer(message, args)")
    )),
    Lesson("Строки и перевод", "Тексты ответов хранят в словаре strings, чтобы их было легко менять.\n>strings = {\"name\": \"MyMod\", \"hello\": \"Привет!\"}\n>await utils.answer(message, self.strings(\"hello\"))\nКлюч name — обязательное имя модуля.", listOf(
        build("Словарь строк модуля", "strings|=|{|\"name\"|:|\"MyMod\"|}", "string|=|["),
        build("Возьми строку hello", "self|.|strings|(|\"hello\"|)", "strings|[|]"),
        type("Ответь строкой hello", "await utils.answer(message, self.strings(\"hello\"))"),
        build("Обязательный ключ словаря", "\"name\"", "\"hello\"|\"text\"", "Без ключа name модуль не загрузится."),
        type("Добавь ключ err со значением Ошибка", "\"err\": \"Ошибка\"")
    )),
    Lesson("Ошибки и отладка модуля", "Ошибки в модуле пишутся в лог Heroku. В песочнице YfeeCode ошибка видна сразу в терминале.\n>try:\n>    ...\n>except Exception as e:\n>    await utils.answer(message, f\"Ошибка: {e}\")\nЕсли команда не находится, проверь, что имя метода заканчивается на cmd и стоит декоратор.", listOf(
        build("Поймай ошибку", "try|:", "if|else"),
        build("Вторая часть", "except|Exception|as|e|:", "catch|e"),
        type("Ответь ошибкой e", "await utils.answer(message, f\"Ошибка: {e}\")"),
        build("Имя метода команды заканчивается на", "cmd", "_cmd|command"),
        type("Проверь список команд модуля", "mod MyMod.py")
    ))
)

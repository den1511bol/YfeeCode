// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons8(): List<Lesson> = listOf(
    Lesson("Настройки модуля: config", "Конфиг даёт пользователю менять параметры модуля, не трогая код.\n>def __init__(self):\n>    self.config = loader.ModuleConfig(\n>        loader.ConfigValue(\"word\", \"привет\", \"Слово для поиска\"),\n>    )\nЗначение читают так: self.config[\"word\"].", listOf(
        choice("Зачем нужен config в модуле?", "Пользователь меняет параметры без правки кода", "Он запускает бота", "Он хранит токен", explain = "Настройки видны в меню конфигурации юзербота."),
        choice("Как прочитать значение word из конфига?", "self.config[\"word\"]", "config.word()", "self.word"),
        gap("Дополни: класс конфига", "self.config = loader.___(", "ModuleConfig", "Config|Settings"),
        gap("Дополни: одна настройка", "loader.___(\"word\", \"привет\", \"Слово для поиска\")", "ConfigValue", "Value|Setting"),
        fix("Исправь: ключ читают в квадратных скобках", "word = self.config(\"word\")", "word = self.config[\"word\"]"),
        build("Создай конфиг модуля", "self|.|config|=|loader|.|ModuleConfig|(", "self|.|db|=|dict"),
        build("Одно значение конфига", "loader|.|ConfigValue|(|\"word\"|,|\"привет\"|,|\"Слово\"|)", "loader|.|command|(|)"),
        type("Прочитай значение word из конфига", "word = self.config[\"word\"]"),
        build("Где объявляют конфиг", "def|__init__|(|self|)|:", "def|hellocmd"),
        type("Объяви метод __init__", "def __init__(self):")
    )),
    Lesson("Наблюдатель watcher", "Watcher реагирует на сообщения без команды.\n>@loader.watcher(only_messages=True)\n>async def watcher(self, message):\n>    if \"привет\" in (message.raw_text or \"\"):\n>        await message.respond(\"Привет!\")\nWatcher вызывается на каждое сообщение, поэтому внутри сначала проверяй условие и выходи.", listOf(
        choice("Когда вызывается watcher?", "На каждое сообщение, а не по команде", "Только по команде", "Раз в час", explain = "Поэтому в начале нужна быстрая проверка условия."),
        choice("Что делает only_messages=True?", "Пропускает служебные события", "Отключает ответы", "Удаляет сообщения"),
        gap("Дополни: декоратор наблюдателя", "@loader.___(only_messages=True)", "watcher", "command|listener"),
        gap("Дополни: безопасный текст", "if \"привет\" in (message.raw_text ___ \"\"):", "or", "and|if", explain = "У сообщения может не быть текста, тогда raw_text пустой."),
        fix("Исправь: watcher тоже async", "def watcher(self, message):", "async def watcher(self, message):"),
        build("Декоратор наблюдателя", "@loader|.|watcher|(|only_messages|=|True|)", "@loader|.|command|(|)"),
        build("Объяви watcher", "async|def|watcher|(|self|,|message|)|:", "def|watcher"),
        type("Возьми текст сообщения", "text = message.raw_text or \"\""),
        build("Проверь слово в тексте", "if|\"привет\"|in|text|:", "==|text|if"),
        type("Ответь в чат", "await message.respond(\"Привет!\")")
    )),
    Lesson("Хранилище self.db", "Модуль может хранить данные между перезапусками в базе юзербота.\n>self.set(\"count\", 5)\n>n = self.get(\"count\", 0)\nset записывает значение, get читает, а второй аргумент — значение по умолчанию.", listOf(
        choice("Зачем self.db?", "Хранить данные между перезапусками", "Ускорить модуль", "Показать меню"),
        choice("Что значит второй аргумент в self.get(\"count\", 0)?", "Значение по умолчанию", "Номер чата", "Тип данных", explain = "Если ключа нет, вернётся 0."),
        gap("Дополни: запиши значение", "self.___(\"count\", 5)", "set", "put|save"),
        gap("Дополни: прочитай значение", "n = self.___(\"count\", 0)", "get", "read|load"),
        fix("Исправь: set принимает ключ и значение", "self.set(\"count\")", "self.set(\"count\", 5)"),
        build("Запиши значение", "self|.|set|(|\"count\"|,|5|)", "self|.|get"),
        type("Прочитай count, по умолчанию 0", "n = self.get(\"count\", 0)"),
        build("Увеличь счётчик на 1", "n|=|n|+|1", "n|-|1"),
        build("Сохрани новое значение", "self|.|set|(|\"count\"|,|n|)", "self|.|get|(|n|)"),
        type("Ответь числом n как строкой", "await utils.answer(message, str(n))")
    )),
    Lesson("Inline-кнопки в модуле", "Форма с кнопками создаётся через self.inline.form.\n>await self.inline.form(\n>    text=\"Привет\",\n>    message=message,\n>    reply_markup=[[{\"text\": \"Кнопка\", \"callback\": self.on_btn}]],\n>)\n>async def on_btn(self, call):\n>    await call.answer(\"Нажато\")\nКнопка — словарь с текстом и функцией, которую нужно вызвать.", listOf(
        choice("Чем создают форму с кнопками?", "self.inline.form", "bot.send_form", "utils.buttons", explain = "Это функция юзербота, а не telebot."),
        choice("Что такое кнопка в reply_markup?", "Словарь с текстом и действием", "Просто строка", "Число"),
        gap("Дополни: текст кнопки", "{\"text\": \"Кнопка\", \"___\": self.on_btn}", "callback", "action|click", explain = "callback это функция, которая сработает при нажатии."),
        gap("Дополни: ответ на нажатие", "await call.___(\"Нажато\")", "answer", "reply|send"),
        fix("Исправь: перед inline.form нужен await", "self.inline.form(text=\"Привет\", message=message)", "await self.inline.form(text=\"Привет\", message=message)"),
        build("Создай форму с кнопкой", "await|self|.|inline|.|form|(", "await|utils|.|answer"),
        build("Кнопка со словарём", "{|\"text\"|:|\"Кнопка\"|,|\"callback\"|:|self|.|on_btn|}", "[|\"Кнопка\"|]"),
        type("Объяви обработчик нажатия", "async def on_btn(self, call):"),
        type("Ответь на нажатие", "await call.answer(\"Нажато\")"),
        build("Какой параметр хранит кнопки", "reply_markup", "text|message")
    )),
    Lesson("Тестируем модуль на телефоне", "В приложении YfeeCode модуль проверяют без Telegram.\n>mod MyMod.py\n>mod MyMod.py .hello Аня\nПервая команда покажет список команд, вторая запустит команду hello с аргументом. Если есть ошибка, она появится в терминале сразу.", listOf(
        choice("Что делает mod MyMod.py?", "Показывает список команд модуля", "Запускает бота", "Удаляет модуль", explain = "Модуль проверяется без Telegram."),
        choice("Как запустить команду hello с аргументом Аня?", "mod MyMod.py .hello Аня", "run MyMod hello", "log hello"),
        gap("Дополни: проверить модуль", "___ MyMod.py", "mod", "run|log"),
        gap("Дополни: запустить команду", "mod MyMod.py .___ Аня", "hello", "hellocmd|cmd"),
        fix("Исправь: команда в терминале начинается с точки", "mod MyMod.py hello Аня", "mod MyMod.py .hello Аня"),
        build("Покажи команды модуля", "mod|MyMod.py", "run|MyMod.py"),
        build("Запусти команду hello с аргументом", "mod|MyMod.py|.hello|Аня", "run|.hello"),
        type("Проверь модуль CfgMod.py", "mod CfgMod.py"),
        build("Где смотреть ошибки бота", "log|30", "stop|mod"),
        type("Запусти команду word из CfgMod.py", "mod CfgMod.py .word")
    ))
)

// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons8(): List<Lesson> = listOf(
    Lesson("Настройки модуля: config", "Конфиг даёт пользователю менять параметры модуля, не трогая код.\n>def __init__(self):\n>    self.config = loader.ModuleConfig(\n>        loader.ConfigValue(\"word\", \"привет\", \"Слово для поиска\"),\n>    )\nЗначение читают так: self.config[\"word\"].", listOf(
        build("Создай конфиг модуля", "self|.|config|=|loader|.|ModuleConfig|(", "self|.|db|=|dict"),
        build("Одно значение конфига", "loader|.|ConfigValue|(|\"word\"|,|\"привет\"|,|\"Слово\"|)", "loader|.|command|(|)"),
        type("Прочитай значение word из конфига", "word = self.config[\"word\"]"),
        build("Где объявляют конфиг", "def|__init__|(|self|)|:", "def|hellocmd"),
        type("Объяви метод __init__", "def __init__(self):")
    )),
    Lesson("Наблюдатель watcher", "Watcher реагирует на сообщения без команды.\n>@loader.watcher(only_messages=True)\n>async def watcher(self, message):\n>    if \"привет\" in (message.raw_text or \"\"):\n>        await message.respond(\"Привет!\")\nWatcher вызывается на каждое сообщение, поэтому внутри сначала проверяй условие и выходи.", listOf(
        build("Декоратор наблюдателя", "@loader|.|watcher|(|only_messages|=|True|)", "@loader|.|command|(|)"),
        build("Объяви watcher", "async|def|watcher|(|self|,|message|)|:", "def|watcher"),
        type("Возьми текст сообщения", "text = message.raw_text or \"\""),
        build("Проверь слово в тексте", "if|\"привет\"|in|text|:", "==|text|if"),
        type("Ответь в чат", "await message.respond(\"Привет!\")")
    )),
    Lesson("Хранилище self.db", "Модуль может хранить данные между перезапусками в базе юзербота.\n>self.set(\"count\", 5)\n>n = self.get(\"count\", 0)\nset записывает значение, get читает, а второй аргумент — значение по умолчанию.", listOf(
        build("Запиши значение", "self|.|set|(|\"count\"|,|5|)", "self|.|get"),
        type("Прочитай count, по умолчанию 0", "n = self.get(\"count\", 0)"),
        build("Увеличь счётчик на 1", "n|=|n|+|1", "n|-|1"),
        build("Сохрани новое значение", "self|.|set|(|\"count\"|,|n|)", "self|.|get|(|n|)"),
        type("Ответь числом n как строкой", "await utils.answer(message, str(n))")
    )),
    Lesson("Inline-кнопки в модуле", "Форма с кнопками создаётся через self.inline.form.\n>await self.inline.form(\n>    text=\"Привет\",\n>    message=message,\n>    reply_markup=[[{\"text\": \"Кнопка\", \"callback\": self.on_btn}]],\n>)\n>async def on_btn(self, call):\n>    await call.answer(\"Нажато\")\nКнопка — словарь с текстом и функцией, которую нужно вызвать.", listOf(
        build("Создай форму с кнопкой", "await|self|.|inline|.|form|(", "await|utils|.|answer"),
        build("Кнопка со словарём", "{|\"text\"|:|\"Кнопка\"|,|\"callback\"|:|self|.|on_btn|}", "[|\"Кнопка\"|]"),
        type("Объяви обработчик нажатия", "async def on_btn(self, call):"),
        type("Ответь на нажатие", "await call.answer(\"Нажато\")"),
        build("Какой параметр хранит кнопки", "reply_markup", "text|message")
    )),
    Lesson("Тестируем модуль на телефоне", "В приложении YfeeCode модуль проверяют без Telegram.\n>mod MyMod.py\n>mod MyMod.py .hello Аня\nПервая команда покажет список команд, вторая запустит команду hello с аргументом. Если есть ошибка, она появится в терминале сразу.", listOf(
        build("Покажи команды модуля", "mod|MyMod.py", "run|MyMod.py"),
        build("Запусти команду hello с аргументом", "mod|MyMod.py|.hello|Аня", "run|.hello"),
        type("Проверь модуль CfgMod.py", "mod CfgMod.py"),
        build("Где смотреть ошибки бота", "log|30", "stop|mod"),
        type("Запусти команду word из CfgMod.py", "mod CfgMod.py .word")
    ))
)

// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons1(): List<Lesson> = listOf(
    Lesson("Что такое бот", "Telegram-бот — это программа, которая отвечает на сообщения людей.\nБот работает на сервере или на твоём телефоне: он постоянно спрашивает Telegram, нет ли новых сообщений (это называется polling).\nНаписать бота можно на Python. Для этого нужна библиотека: pyTelegramBotAPI (импорт telebot) или aiogram.", listOf(
        build("Импортируй библиотеку telebot", "import|telebot", "from|aiogram", "Библиотека pyTelegramBotAPI импортируется как telebot."),
        type("Напиши импорт библиотеки telebot", "import telebot"),
        build("Постоянный опрос Telegram называется", "polling", "print|import", "Polling значит, что бот сам регулярно спрашивает Telegram о новых сообщениях."),
        build("Импортируй aiogram", "import|aiogram", "telebot|from"),
        type("Выведи слово Бот", "print(\"Бот\")")
    )),
    Lesson("BotFather и токен", "Чтобы создать бота, напиши в Telegram пользователю @BotFather и отправь команду /newbot.\nBotFather спросит имя и username (username заканчивается на bot) и выдаст токен — длинную строку вида 123456:ABC-DEF.\nТокен — это пароль бота. Не показывай его другим и не загружай в публичный git. Храни токен в файле token.txt или в переменной окружения.", listOf(
        build("Команда BotFather для создания бота", "/newbot", "/start|/help", "Создание бота начинается командой /newbot."),
        build("Прочитай токен из файла token.txt", "TOKEN|=|open|(|\"token.txt\"|)|.|read|(|)", "write|input", "open открывает файл, read читает всё содержимое."),
        type("Положи токен из файла в переменную TOKEN", "TOKEN = open(\"token.txt\").read().strip()", "TOKEN = open('token.txt').read().strip()", explain = "strip() убирает пробелы и перевод строки."),
        build("Прочитай токен из окружения", "import|os", "from|sys"),
        type("Возьми токен из переменной окружения BOT_TOKEN", "TOKEN = os.environ[\"BOT_TOKEN\"]", "TOKEN = os.getenv(\"BOT_TOKEN\")")
    )),
    Lesson("Установка библиотеки", "Библиотеки ставит pip. Во вкладке Linux:\n>pip install pyTelegramBotAPI\n>pip install aiogram\nВ Heroku нужные библиотеки перечисляют в файле requirements.txt, по одной на строку.", listOf(
        build("Установи pyTelegramBotAPI", "pip|install|pyTelegramBotAPI", "uninstall|apt", "Название пакета для pip: pyTelegramBotAPI."),
        type("Установи aiogram", "pip install aiogram"),
        build("Какой файл хранит список библиотек для Heroku", "requirements.txt", "Procfile|token.txt"),
        type("Установи flask", "pip install flask", "pip install Flask"),
        build("Обнови пакет aiogram", "pip|install|-U|aiogram", "remove|get")
    )),
    Lesson("Первый бот", "Минимальный бот на telebot:\n>import telebot\n>bot = telebot.TeleBot(TOKEN)\n>@bot.message_handler(commands=[\"start\"])\n>def start(message):\n>    bot.reply_to(message, \"Привет!\")\n>bot.infinity_polling()\nФункция под @bot.message_handler вызывается, когда приходит подходящее сообщение.", listOf(
        build("Создай объект бота", "bot|=|telebot|.|TeleBot|(|TOKEN|)", "Bot|Telegram", "Класс называется TeleBot, ему передают токен."),
        type("Создай бота: bot с токеном TOKEN", "bot = telebot.TeleBot(TOKEN)"),
        build("Обработчик команды /start", "@bot|.|message_handler|(|commands|=|[|\"start\"|]|)", "on_message|handler", "commands принимает список команд без слеша."),
        build("Ответь на сообщение", "bot|.|reply_to|(|message|,|\"Привет!\"|)", "send|answer"),
        type("Запусти бесконечный опрос", "bot.infinity_polling()", explain = "Эта строка идёт в конце файла: она не завершается, пока бот работает.")
    )),
    Lesson("Запуск и остановка", "В приложении YfeeCode запусти файл кнопкой ▶ или командой run bot.py. Бот работает в фоне, даже когда экран выключен.\nЛог смотри командой log, остановка — stop.\nЕсли в логе ошибка про токен, проверь token.txt: в нём должен быть только токен, без пробелов.", listOf(
        build("Запустить файл bot.py в терминале", "run|bot.py", "stop|log"),
        build("Показать лог бота", "log", "run|stop", "log показывает последние строки лога бота."),
        type("Остановить бота", "stop"),
        build("Показать последние 50 строк лога", "log|50", "stop|run"),
        type("Сохранить токен командой терминала", "env set BOT_TOKEN 123:ABC", explain = "Файл .env подхватывается при запуске бота.")
    ))
)

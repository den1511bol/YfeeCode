// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons1(): List<Lesson> = listOf(
    Lesson("Что такое бот", "Telegram-бот — это программа, которая отвечает на сообщения людей.\nБот работает на сервере или на твоём телефоне: он постоянно спрашивает Telegram, нет ли новых сообщений (это называется polling).\nНаписать бота можно на Python. Для этого нужна библиотека: pyTelegramBotAPI (импорт telebot) или aiogram.", listOf(
        choice("Что такое Telegram-бот?", "Программа, которая отвечает на сообщения людей", "Приложение для звонков", "Новый вид аккаунта без телефона", explain = "Бот это обычная программа: она получает сообщения и отвечает на них."),
        choice("Что значит polling?", "Бот сам регулярно спрашивает Telegram о новых сообщениях", "Бот отправляет фото", "Бот выключается на ночь", explain = "Polling значит опрос: бот спрашивает, нет ли новых сообщений."),
        choice("На каком языке мы пишем ботов в этом курсе?", "Python", "HTML", "CSS"),
        build("Импортируй библиотеку telebot", "import|telebot", "from|aiogram", "Библиотека pyTelegramBotAPI импортируется как telebot."),
        type("Напиши импорт библиотеки telebot", "import telebot"),
        build("Постоянный опрос Telegram называется", "polling", "print|import", "Polling значит, что бот сам регулярно спрашивает Telegram о новых сообщениях."),
        build("Импортируй aiogram", "import|aiogram", "telebot|from"),
        type("Выведи слово Бот", "print(\"Бот\")")
    )),
    Lesson("BotFather и токен", "Чтобы создать бота, напиши в Telegram пользователю @BotFather и отправь команду /newbot.\nBotFather спросит имя и username (username заканчивается на bot) и выдаст токен — длинную строку вида 123456:ABC-DEF.\nТокен — это пароль бота. Не показывай его другим и не загружай в публичный git. Храни токен в файле token.txt или в переменной окружения.", listOf(
        choice("Кто выдаёт токен бота?", "@BotFather", "@Telegram", "Служба поддержки Heroku", explain = "Токен выдаёт официальный бот @BotFather после команды /newbot."),
        choice("Что нельзя делать с токеном?", "Показывать другим и загружать в публичный git", "Хранить в файле token.txt", "Читать из переменной окружения", explain = "Токен это пароль бота: с ним любой может управлять твоим ботом."),
        choice("Каким словом заканчивается username бота?", "bot", "app", "tg"),
        gap("Дополни: открой файл token.txt и прочитай его", "TOKEN = ___(\"token.txt\").read()", "open", "print|input", explain = "open открывает файл, а read читает содержимое."),
        build("Команда BotFather для создания бота", "/newbot", "/start|/help", "Создание бота начинается командой /newbot."),
        build("Прочитай токен из файла token.txt", "TOKEN|=|open|(|\"token.txt\"|)|.|read|(|)", "write|input", "open открывает файл, read читает всё содержимое."),
        type("Положи токен из файла в переменную TOKEN", "TOKEN = open(\"token.txt\").read().strip()", "TOKEN = open('token.txt').read().strip()", explain = "strip() убирает пробелы и перевод строки."),
        build("Прочитай токен из окружения", "import|os", "from|sys"),
        type("Возьми токен из переменной окружения BOT_TOKEN", "TOKEN = os.environ[\"BOT_TOKEN\"]", "TOKEN = os.getenv(\"BOT_TOKEN\")")
    )),
    Lesson("Установка библиотеки", "Библиотеки ставит pip. Во вкладке Linux:\n>pip install pyTelegramBotAPI\n>pip install aiogram\nВ Heroku нужные библиотеки перечисляют в файле requirements.txt, по одной на строку.", listOf(
        choice("Какая команда ставит библиотеку?", "pip install", "run", "apk update", explain = "pip это установщик библиотек Python."),
        choice("Что хранится в requirements.txt?", "Список библиотек, по одной на строку", "Токен бота", "Лог бота", explain = "Heroku читает этот файл и сам ставит нужные библиотеки."),
        gap("Дополни команду установки", "pip ___ aiogram", "install", "remove|run"),
        fix("Исправь команду: библиотеку ставят через pip install", "pip add aiogram", "pip install aiogram", explain = "У pip нет команды add, нужна install."),
        build("Установи pyTelegramBotAPI", "pip|install|pyTelegramBotAPI", "uninstall|apt", "Название пакета для pip: pyTelegramBotAPI."),
        type("Установи aiogram", "pip install aiogram"),
        build("Какой файл хранит список библиотек для Heroku", "requirements.txt", "Procfile|token.txt"),
        type("Установи flask", "pip install flask", "pip install Flask"),
        build("Обнови пакет aiogram", "pip|install|-U|aiogram", "remove|get")
    )),
    Lesson("Первый бот", "Минимальный бот на telebot:\n>import telebot\n>bot = telebot.TeleBot(TOKEN)\n>@bot.message_handler(commands=[\"start\"])\n>def start(message):\n>    bot.reply_to(message, \"Привет!\")\n>bot.infinity_polling()\nФункция под @bot.message_handler вызывается, когда приходит подходящее сообщение.", listOf(
        choice("Что делает bot.infinity_polling()?", "Постоянно ждёт новые сообщения", "Отправляет одно сообщение", "Закрывает бота", explain = "Эту строку ставят в самом конце файла."),
        choice("Что передают в TeleBot(...)?", "Токен", "Имя файла", "Номер телефона"),
        gap("Дополни обработчик команды /start", "@bot.message_handler(commands=[\"___\"])", "start", "help|begin", explain = "В commands пишут имя команды без слеша."),
        fix("Найди ошибку: команды пишут без слеша", "@bot.message_handler(commands=[\"/start\"])", "@bot.message_handler(commands=[\"start\"])", explain = "В commands слеш не нужен."),
        fix("Исправь опечатку в названии класса", "bot = telebot.Telebot(TOKEN)", "bot = telebot.TeleBot(TOKEN)", explain = "Регистр важен: TeleBot, с большой B."),
        build("Создай объект бота", "bot|=|telebot|.|TeleBot|(|TOKEN|)", "Bot|Telegram", "Класс называется TeleBot, ему передают токен."),
        type("Создай бота: bot с токеном TOKEN", "bot = telebot.TeleBot(TOKEN)"),
        build("Обработчик команды /start", "@bot|.|message_handler|(|commands|=|[|\"start\"|]|)", "on_message|handler", "commands принимает список команд без слеша."),
        build("Ответь на сообщение", "bot|.|reply_to|(|message|,|\"Привет!\"|)", "send|answer"),
        type("Запусти бесконечный опрос", "bot.infinity_polling()", explain = "Эта строка идёт в конце файла: она не завершается, пока бот работает.")
    )),
    Lesson("Запуск и остановка", "В приложении YfeeCode запусти файл кнопкой ▶ или командой run bot.py. Бот работает в фоне, даже когда экран выключен.\nЛог смотри командой log, остановка — stop.\nЕсли в логе ошибка про токен, проверь token.txt: в нём должен быть только токен, без пробелов.", listOf(
        choice("Какой кнопкой запускают бота в приложении?", "▶ в редакторе", "A+", "Дублировать", explain = "Ещё можно написать run bot.py в терминале."),
        choice("Что значит ошибка про токен в логе?", "Проверь token.txt: там должен быть только токен", "Нужно перезагрузить телефон", "Нужно удалить бота", explain = "Лишние пробелы или строки в token.txt ломают токен."),
        choice("Как остановить бота?", "Команда stop", "Команда log", "Команда env"),
        fix("Исправь команду запуска", "start bot.py", "run bot.py", explain = "Файл запускают командой run."),
        build("Запустить файл bot.py в терминале", "run|bot.py", "stop|log"),
        build("Показать лог бота", "log", "run|stop", "log показывает последние строки лога бота."),
        type("Остановить бота", "stop"),
        build("Показать последние 50 строк лога", "log|50", "stop|run"),
        type("Сохранить токен командой терминала", "env set BOT_TOKEN 123:ABC", explain = "Файл .env подхватывается при запуске бота.")
    ))
)

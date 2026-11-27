// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons9(): List<Lesson> = listOf(
    Lesson("Что нужно Heroku", "Чтобы Heroku запустил бота, в репозитории нужны файлы:\n>Procfile — как запускать (worker: python3 bot.py)\n>requirements.txt — какие библиотеки поставить\n>runtime.txt — версия Python (python-3.11.9)\nProcfile пишут без расширения, с большой буквы. Тип worker означает «процесс без сайта», как у бота на polling.", listOf(
        choice("Что делает Procfile?", "Говорит Heroku, как запускать бота", "Хранит токен", "Список библиотек", explain = "Пример: worker: python3 bot.py"),
        choice("Как правильно назвать файл?", "Procfile без расширения", "procfile.txt", "Procfile.py"),
        gap("Дополни Procfile для бота", "___: python3 bot.py", "worker", "web2|bot", explain = "worker это процесс без сайта, подходит для polling."),
        gap("Дополни: файл версии Python", "___.txt", "runtime", "python|version"),
        fix("Исправь: список библиотек лежит в requirements.txt", "requirement.txt", "requirements.txt", explain = "Название с буквой s на конце."),
        build("Procfile для бота на polling", "worker:|python3|bot.py", "web:|bot.py"),
        build("Файл со списком библиотек", "requirements.txt", "Procfile|runtime.txt"),
        build("Файл с версией Python", "runtime.txt", "token.txt|Procfile"),
        build("Создай нужные файлы командой", "yfee-heroku|files", "yfee-heroku|login"),
        type("Запиши в requirements строку pyTelegramBotAPI", "pyTelegramBotAPI")
    )),
    Lesson("Установка и вход", "Heroku CLI — программа для управления приложениями. В Linux вкладки YfeeCode:\n>yfee-heroku install\n>yfee-heroku login\ninstall нужен один раз, login откроет вход в аккаунт Heroku. Если браузера нет, вход идёт по ссылке из терминала.", listOf(
        choice("Что такое Heroku CLI?", "Программа для управления приложениями", "Редактор кода", "Бот в Telegram"),
        choice("Сколько раз нужен yfee-heroku install?", "Один раз", "Перед каждым запуском", "Каждый день", explain = "Дальше хватит yfee-heroku login."),
        gap("Дополни: войти в аккаунт", "yfee-heroku ___", "login", "install|init"),
        gap("Дополни: установить CLI", "yfee-heroku ___", "install", "login|files"),
        fix("Исправь команду входа", "yfee-heroku log", "yfee-heroku login"),
        build("Установи Heroku CLI", "yfee-heroku|install", "pkg|install"),
        build("Войди в аккаунт", "yfee-heroku|login", "heroku|logout"),
        type("Покажи подсказку по деплою", "yfee-heroku"),
        build("Команда CLI для входа", "heroku|login|-i", "heroku|run"),
        type("Проверь, кто вошёл", "heroku auth:whoami")
    )),
    Lesson("Создание приложения", "Приложение на Heroku создают командой, а код отправляют через git.\n>heroku create my-bot-name\n>git add -A\n>git commit -m deploy\n>git push heroku main\nКоманда heroku create добавляет удалённый репозиторий heroku. Имя приложения должно быть уникальным.", listOf(
        choice("Чем отправляют код на Heroku?", "git push heroku main", "run heroku", "pip install heroku", explain = "heroku create добавляет удалённый репозиторий heroku."),
        choice("Каким должно быть имя приложения?", "Уникальным", "Любым", "Только цифрами"),
        gap("Дополни: создай приложение", "heroku ___ my-bot-name", "create", "start|new"),
        gap("Дополни: добавь все файлы", "git ___ -A", "add", "push|init"),
        fix("Исправь: коммит нужен перед push", "git push heroku main\ngit commit -m deploy", "git commit -m deploy", explain = "Сначала git add -A и git commit, потом push."),
        build("Создай приложение", "heroku|create|my-bot-name", "heroku|login"),
        build("Добавь все файлы в git", "git|add|-A", "git|push"),
        type("Сделай коммит с сообщением deploy", "git commit -m deploy", "git commit -m \"deploy\""),
        build("Отправь код на Heroku", "git|push|heroku|main", "git|pull|origin"),
        type("Покажи удалённые репозитории", "git remote -v")
    )),
    Lesson("Переменные и запуск", "Токены на Heroku хранят в config vars, а не в коде.\n>heroku config:set BOT_TOKEN=123:ABC\n>heroku ps:scale worker=1\n>heroku logs --tail\nps:scale worker=1 включает процесс бота, worker=0 выключает. Для всех переменных из .env есть yfee-heroku config.", listOf(
        choice("Где хранить токен на Heroku?", "В config vars", "В коде", "В публичном README", explain = "Команда: heroku config:set BOT_TOKEN=..."),
        choice("Что делает ps:scale worker=0?", "Выключает бота", "Включает бота", "Удаляет приложение"),
        gap("Дополни: включи бота", "heroku ps:scale worker=___", "1", "0|2x"),
        gap("Дополни: смотри логи", "heroku logs --___", "tail", "head|show", explain = "--tail показывает логи в реальном времени."),
        fix("Исправь команду переменной", "heroku config:set BOT_TOKEN 123:ABC", "heroku config:set BOT_TOKEN=123:ABC", explain = "Между именем и значением нужен знак =."),
        build("Задай токен на Heroku", "heroku|config:set|BOT_TOKEN=123:ABC", "heroku|logs"),
        build("Включи процесс бота", "heroku|ps:scale|worker=1", "worker=0|stop"),
        type("Выключи процесс бота", "heroku ps:scale worker=0"),
        build("Смотри логи в реальном времени", "heroku|logs|--tail", "log|30"),
        build("Передай переменные из .env", "yfee-heroku|config", "yfee-heroku|files")
    )),
    Lesson("Webhook или polling", "Polling — бот сам спрашивает Telegram, подходит для процесса worker и тестов на телефоне.\nWebhook — Telegram сам присылает обновления на адрес приложения, нужен процесс web и сайт на Flask. Webhook удобен, когда у приложения уже есть сайт.\nПроверить, что бот работает: heroku ps.", listOf(
        choice("Как работает polling?", "Бот сам спрашивает Telegram", "Telegram присылает обновления на адрес", "Бот не работает"),
        choice("Для чего нужен webhook?", "Telegram сам присылает обновления на адрес приложения", "Для хранения файлов", "Для логов", explain = "Нужен процесс web и сайт, например на Flask."),
        choice("Какой процесс подходит для polling?", "worker", "web", "cron2"),
        gap("Дополни Procfile для webhook", "___: python3 app.py", "web", "worker|bot"),
        fix("Исправь: для webhook нужен процесс web", "worker: python3 app.py", "web: python3 app.py", explain = "web это процесс, который принимает запросы из интернета."),
        build("Тип процесса для polling", "worker", "web|cron"),
        build("Тип процесса для сайта и webhook", "web", "worker|bot"),
        type("Покажи запущенные процессы", "heroku ps"),
        build("Перезапусти приложение", "heroku|restart", "heroku|create"),
        build("Шаблон для webhook в приложении", "Flask|для|Heroku", "aiogram|3", "В списке шаблонов есть «Бот: Flask для Heroku (web)».")
    )),
    Lesson("Проект по шагам: эхо-бот", "Эхо-бот повторяет любое сообщение. Это основа любого бота.\n>pip install aiogram\n>bot = Bot(token=TOKEN)\n>@dp.message()\n>async def echo(message): await message.answer(message.text)\n>asyncio.run(dp.start_polling(bot))\nШаги: токен от @BotFather, библиотека, обработчик, запуск polling. Токен храни в переменной окружения.", listOf(
        choice("Где взять токен бота?", "У @BotFather", "В настройках Android", "В Procfile"),
        choice("Что делает обработчик сообщения?", "Отвечает на каждое сообщение", "Хранит токен", "Запускает Heroku"),
        gap("Дополни: ответь тем же текстом", "await message.___(message.text)", "answer", "echo|start"),
        gap("Дополни: запусти приём обновлений", "asyncio.run(dp.start____(bot))", "polling", "webhook|loop"),
        fix("Исправь: обработчик должен быть async", "def echo(message): await message.answer(message.text)", "async def echo(message): await message.answer(message.text)", explain = "await работает только внутри async def."),
        build("Установи библиотеку", "pip|install|aiogram", "pip|remove"),
        build("Создай бота с токеном", "bot|=|Bot(token=TOKEN)", "bot|==|Bot"),
        type("Запусти бота", "python3 bot.py")
    ))
)

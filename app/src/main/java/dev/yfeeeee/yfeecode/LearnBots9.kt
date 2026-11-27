// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons9(): List<Lesson> = listOf(
    Lesson("Что нужно Heroku", "Чтобы Heroku запустил бота, в репозитории нужны файлы:\n>Procfile — как запускать (worker: python3 bot.py)\n>requirements.txt — какие библиотеки поставить\n>runtime.txt — версия Python (python-3.11.9)\nProcfile пишут без расширения, с большой буквы. Тип worker означает «процесс без сайта», как у бота на polling.", listOf(
        build("Procfile для бота на polling", "worker:|python3|bot.py", "web:|bot.py"),
        build("Файл со списком библиотек", "requirements.txt", "Procfile|runtime.txt"),
        build("Файл с версией Python", "runtime.txt", "token.txt|Procfile"),
        build("Создай нужные файлы командой", "yfee-heroku|files", "yfee-heroku|login"),
        type("Запиши в requirements строку pyTelegramBotAPI", "pyTelegramBotAPI")
    )),
    Lesson("Установка и вход", "Heroku CLI — программа для управления приложениями. В Linux вкладки YfeeCode:\n>yfee-heroku install\n>yfee-heroku login\ninstall нужен один раз, login откроет вход в аккаунт Heroku. Если браузера нет, вход идёт по ссылке из терминала.", listOf(
        build("Установи Heroku CLI", "yfee-heroku|install", "pkg|install"),
        build("Войди в аккаунт", "yfee-heroku|login", "heroku|logout"),
        type("Покажи подсказку по деплою", "yfee-heroku"),
        build("Команда CLI для входа", "heroku|login|-i", "heroku|run"),
        type("Проверь, кто вошёл", "heroku auth:whoami")
    )),
    Lesson("Создание приложения", "Приложение на Heroku создают командой, а код отправляют через git.\n>heroku create my-bot-name\n>git add -A\n>git commit -m deploy\n>git push heroku main\nКоманда heroku create добавляет удалённый репозиторий heroku. Имя приложения должно быть уникальным.", listOf(
        build("Создай приложение", "heroku|create|my-bot-name", "heroku|login"),
        build("Добавь все файлы в git", "git|add|-A", "git|push"),
        type("Сделай коммит с сообщением deploy", "git commit -m deploy", "git commit -m \"deploy\""),
        build("Отправь код на Heroku", "git|push|heroku|main", "git|pull|origin"),
        type("Покажи удалённые репозитории", "git remote -v")
    )),
    Lesson("Переменные и запуск", "Токены на Heroku хранят в config vars, а не в коде.\n>heroku config:set BOT_TOKEN=123:ABC\n>heroku ps:scale worker=1\n>heroku logs --tail\nps:scale worker=1 включает процесс бота, worker=0 выключает. Для всех переменных из .env есть yfee-heroku config.", listOf(
        build("Задай токен на Heroku", "heroku|config:set|BOT_TOKEN=123:ABC", "heroku|logs"),
        build("Включи процесс бота", "heroku|ps:scale|worker=1", "worker=0|stop"),
        type("Выключи процесс бота", "heroku ps:scale worker=0"),
        build("Смотри логи в реальном времени", "heroku|logs|--tail", "log|30"),
        build("Передай переменные из .env", "yfee-heroku|config", "yfee-heroku|files")
    )),
    Lesson("Webhook или polling", "Polling — бот сам спрашивает Telegram, подходит для процесса worker и тестов на телефоне.\nWebhook — Telegram сам присылает обновления на адрес приложения, нужен процесс web и сайт на Flask. На Heroku бесплатные процессы засыпают, поэтому webhook удобнее для web-приложений.\nПроверить, что бот работает: heroku ps.", listOf(
        build("Тип процесса для polling", "worker", "web|cron"),
        build("Тип процесса для сайта и webhook", "web", "worker|bot"),
        type("Покажи запущенные процессы", "heroku ps"),
        build("Перезапусти приложение", "heroku|restart", "heroku|create"),
        build("Шаблон для webhook в приложении", "Flask|для|Heroku", "aiogram|3", "В списке шаблонов есть «Бот: Flask для Heroku (web)».")
    ))
)

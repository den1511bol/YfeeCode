// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons6(): List<Lesson> = listOf(
    Lesson("Переменные окружения и .env", "Секреты (токены, пароли) не пишут прямо в код. Их кладут в переменные окружения или файл .env.\n>import os\n>TOKEN = os.environ[\"BOT_TOKEN\"]\n>ADMIN = os.getenv(\"ADMIN_ID\", \"0\")\nos.environ[\"ключ\"] выдаст ошибку, если переменной нет, а os.getenv вернёт значение по умолчанию. В YfeeCode команда env set КЛЮЧ значение пишет в .env.", listOf(
        choice("Почему токен не пишут прямо в код?", "Код могут увидеть другие люди", "Так бот медленнее", "Python это запрещает", explain = "Токен хранят в .env или переменной окружения, а .env не загружают в git."),
        choice("Чем os.environ[\"X\"] отличается от os.getenv(\"X\")?", "environ даёт ошибку, если ключа нет", "getenv быстрее", "Ничем", explain = "getenv вернёт None или значение по умолчанию."),
        gap("Дополни: прочитай токен", "TOKEN = os.___[\"BOT_TOKEN\"]", "environ", "getenv|env"),
        gap("Дополни: значение по умолчанию", "ADMIN = os.___(\"ADMIN_ID\", \"0\")", "getenv", "environ|get2"),
        fix("Исправь: нужен импорт os", "import OS", "import os", explain = "Названия модулей пишутся строчными."),
        build("Возьми токен из окружения", "TOKEN|=|os|.|environ|[|\"BOT_TOKEN\"|]", "input|open"),
        type("Возьми ADMIN_ID, а если нет, то \"0\"", "ADMIN = os.getenv(\"ADMIN_ID\", \"0\")"),
        build("Сохрани токен командой терминала", "env|set|BOT_TOKEN|123:ABC", "run|log"),
        build("Какой файл не загружают в git", ".env", "bot.py|Procfile", ".env с токенами нужно внести в .gitignore."),
        type("Переведи строку ADMIN в число", "admin = int(ADMIN)")
    )),
    Lesson("Хранение данных в JSON", "Для простых данных подойдёт файл JSON.\n>import json\n>data = {\"users\": [1, 2, 3]}\n>with open(\"data.json\", \"w\", encoding=\"utf-8\") as f:\n>    json.dump(data, f, ensure_ascii=False)\n>with open(\"data.json\", encoding=\"utf-8\") as f:\n>    data = json.load(f)\nensure_ascii=False оставляет русские буквы читаемыми.", listOf(
        choice("Что такое JSON?", "Текстовый формат для хранения данных", "Язык программирования", "База данных", explain = "Он похож на словари и списки Python."),
        choice("Зачем ensure_ascii=False?", "Чтобы русский текст не превращался в коды", "Чтобы файл был меньше", "Чтобы ускорить запись"),
        gap("Дополни: запиши данные в файл", "json.___(data, f, ensure_ascii=False)", "dump", "load|dumps2"),
        gap("Дополни: прочитай данные из файла", "data = json.___(f)", "load", "dump|read"),
        fix("Исправь: режим записи это w", "with open(\"data.json\", \"r\", encoding=\"utf-8\") as f:\n    json.dump(data, f)", "with open(\"data.json\", \"w\", encoding=\"utf-8\") as f:", explain = "В режиме r файл только читают. Для записи нужен w."),
        build("Импортируй json", "import|json", "from|os"),
        build("Запиши данные в файл", "json|.|dump|(|data|,|f|,|ensure_ascii|=|False|)", "load|dumps"),
        type("Прочитай данные из файла f", "data = json.load(f)"),
        build("Открой файл на запись в UTF-8", "open|(|\"data.json\"|,|\"w\"|,|encoding|=|\"utf-8\"|)", "\"r\"|read"),
        type("Добавь пользователя 5 в список data[\"users\"]", "data[\"users\"].append(5)")
    )),
    Lesson("SQLite: база в одном файле", "sqlite3 встроен в Python и не требует сервера.\n>import sqlite3\n>db = sqlite3.connect(\"bot.db\")\n>db.execute(\"CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)\")\n>db.execute(\"INSERT INTO users (name) VALUES (?)\", (\"Аня\",))\n>db.commit()\nЗнак ? подставляет значение безопасно, не склеивай SQL строкой.", listOf(
        choice("Нужен ли серверу для sqlite3 отдельный сервер?", "Нет, база лежит в одном файле", "Да, нужен Postgres", "Да, нужен Heroku", explain = "sqlite3 встроен в Python."),
        choice("Зачем знак ? в запросе?", "Безопасно подставляет значения", "Спрашивает пользователя", "Отключает ошибки", explain = "Так нельзя сломать запрос и защищает от SQL-инъекций."),
        gap("Дополни: сохрани изменения", "db.___()", "commit", "save|close2", explain = "Без commit данные не запишутся в файл."),
        gap("Дополни: открой базу", "db = sqlite3.___(\"bot.db\")", "connect", "open|load"),
        fix("Исправь: значения передаются кортежем", "db.execute(\"INSERT INTO users (name) VALUES (?)\", \"Аня\")", "db.execute(\"INSERT INTO users (name) VALUES (?)\", (\"Аня\",))", explain = "Кортеж из одного элемента пишут с запятой."),
        build("Импортируй sqlite3", "import|sqlite3", "from|json"),
        build("Подключись к базе", "db|=|sqlite3|.|connect|(|\"bot.db\"|)", "open|read"),
        build("Сохрани изменения", "db|.|commit|(|)", "save|close"),
        type("Выполни запрос SELECT * FROM users", "rows = db.execute(\"SELECT * FROM users\").fetchall()"),
        build("Безопасная подстановка значения", "db|.|execute|(|\"DELETE FROM users WHERE id = ?\"|,|(|5|,|)|)", "format|+")
    )),
    Lesson("Логирование", "Вместо print в больших ботах используют logging: у сообщений есть уровни и время.\n>import logging\n>logging.basicConfig(level=logging.INFO)\n>logging.info(\"Бот запущен\")\n>logging.error(\"Ошибка: %s\", e)\nВ YfeeCode логи видны командой log, на Heroku — командой heroku logs --tail.", listOf(
        choice("Чем logging лучше print?", "Есть уровни и время сообщений", "Он быстрее", "Он красивее"),
        choice("Какой уровень у сообщения об ошибке?", "ERROR", "INFO", "DEBUG"),
        gap("Дополни: включи вывод INFO", "logging.basicConfig(level=logging.___)", "INFO", "DEBUGGING|PRINT"),
        gap("Дополни: записать ошибку", "logging.___(\"Ошибка: %s\", e)", "error", "print|err2"),
        fix("Исправь: нужен импорт logging", "import logger", "import logging"),
        build("Импортируй logging", "import|logging", "import|log"),
        build("Включи уровень INFO", "logging|.|basicConfig|(|level|=|logging|.|INFO|)", "level|=|print"),
        type("Запиши информационное сообщение", "logging.info(\"Бот запущен\")"),
        build("Запиши ошибку e", "logging|.|error|(|\"Ошибка: %s\"|,|e|)", "print|info"),
        build("Логи на Heroku", "heroku|logs|--tail", "log|50|stop")
    )),
    Lesson("Чтение и запись текстовых файлов", "Простой текстовый файл подходит для списков и заметок.\n>with open(\"notes.txt\", \"a\", encoding=\"utf-8\") as f:\n>    f.write(\"Привет\\n\")\n>with open(\"notes.txt\", encoding=\"utf-8\") as f:\n>    lines = f.read().splitlines()\nРежим \"a\" дописывает в конец, \"w\" перезаписывает файл, \"r\" читает.", listOf(
        choice("Что делает режим a при открытии файла?", "Дописывает в конец", "Стирает файл", "Только читает", explain = "a от append, добавить."),
        choice("Зачем with open(...)?", "Файл закроется сам", "Файл станет быстрее", "Файл зашифруется"),
        gap("Дополни: режим добавления", "open(\"notes.txt\", \"___\", encoding=\"utf-8\")", "a", "r|x2"),
        gap("Дополни: прочитай файл по строкам", "lines = f.read().___()", "splitlines", "split2|lines"),
        fix("Исправь: чтобы запись начиналась с новой строки, нужен \\n", "f.write(\"Привет\")", "f.write(\"Привет\\n\")", explain = "\\n это перенос строки."),
        build("Дописать в конец файла", "open|(|\"notes.txt\"|,|\"a\"|,|encoding|=|\"utf-8\"|)", "\"w\"|\"r\""),
        type("Запиши строку Привет и перевод строки в f", "f.write(\"Привет\\n\")"),
        build("Прочитай все строки списком", "f|.|read|(|)|.|splitlines|(|)", "write|append"),
        build("Проверь, что файл есть", "os|.|path|.|exists|(|\"notes.txt\"|)", "open|read"),
        type("Удали файл notes.txt", "os.remove(\"notes.txt\")")
    ))
)

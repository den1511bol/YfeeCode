# Ночной цикл v5: курс «Боты и Heroku» (разработчик @yfeeeee)
Цель: новый курс id "bots" (🤖) с уроками и практикой. Работа в ветке dev (CI не собирает dev). 14 сессий по 10 минут.
Формат (см. LearnPython.kt): Lesson("Название", "теория; строки кода начинаются с '>'", listOf(build("задание","токен|токен|...","лишние|блоки","пояснение"), type("задание","ответ1","ответ2"))). В теории и ответах Kotlin-строки: `$` писать как `\$`, кавычки `\"`. 5-6 упражнений на урок, 4-6 уроков за сессию. Проверка type сравнивает токены (кавычки ' и " равны).
Файлы: LearnBots1.kt, LearnBots2.kt ... каждый содержит `fun botsLessonsN(): List<Lesson>`. LearnBots.kt: `fun botsCourse() = Course("bots", "Боты и Heroku", "🤖", botsLessons1() + ...)`. Практика: BotsPractices.kt `fun botsPractices(): List<Practice>` (Lang.PYTHON, без expectOut, только must()-проверки regex); подключить в Practices.forCourse для id "bots". Добавить botsCourse() в Courses.all (LearnModel.kt).
Правила: не ломать Updater.kt, app/yfeecode.jks, applicationId, строку "Commit: <sha>" в build.yml, versionCode из GITHUB_RUN_NUMBER, AutoSpace.kt, TermHints.kt, WakeLock, шаг "Fetch proot". Kotlin локально не собрать: писать консервативно, скобки считать. Без вопросов, экономить лимит. Каждая сессия: ReadNotifications, поставить следующее напоминание, ОДИН пункт, коммит в dev, отметка [x].

- [x] 1. Каркас (LearnBots.kt, подключение в Courses и Practices) + LearnBots1.kt: что такое бот, BotFather и токен, pip install, первый бот telebot (4-5 уроков).
- [x] 2. LearnBots2.kt: telebot: команды, обработчики текста, reply_to, клавиатуры (reply и inline).
- [x] 3. LearnBots3.kt: telebot: callback_query, register_next_step_handler, отправка фото/файлов, parse_mode, обработка ошибок.
- [x] 4. LearnBots4.kt: aiogram 3: Bot, Dispatcher, Command, async/await, polling.
- [x] 5. LearnBots5.kt: aiogram 3: фильтры, F-фильтры, inline-клавиатуры, callback, FSM.
- [x] 6. LearnBots6.kt: хранение данных: json, sqlite3, .env и os.environ, логирование.
- [x] 7. LearnBots7.kt: модули Hikka/Heroku: структура, @loader.tds, strings, команды, utils.answer, get_args_raw.
- [x] 8. LearnBots8.kt: модули: ModuleConfig, watcher, self.db, inline-формы, тестирование командой mod.
- [x] 9. LearnBots9.kt: деплой на Heroku: Procfile, requirements.txt, runtime.txt, git push heroku, config:set, логи, webhook vs polling.
- [x] 10. BotsPractices.kt: 10-12 практик (regex-проверки) по всем темам + уроки-проекты (LearnBots10.kt: бот-заметки, модуль-счётчик).
- [x] 10b. Python 3.13 во встроенном рантайме (выбор пользователя). Корень build.gradle.kts: com.chaquo.python 17.0.0 (вместо 16.0.0); app/build.gradle.kts: chaquopy defaultConfig version = "3.13". ПРОБЛЕМА: aiogram==2.25.2 требует aiohttp<3.9, а в репозитории Chaquopy (chaquo.com/pypi-13.1) для cp313 есть только aiohttp 3.10.10 (для cp312 тоже только 3.9.1+). Решение: в блоке pip использовать options("--no-deps") и перечислить зависимости вручную с версиями, которые есть в репозитории Chaquopy или чистые из PyPI: requests, urllib3, charset-normalizer, idna, certifi, pyTelegramBotAPI==4.21.0, aiogram==2.25.2, Babel, magic-filter, aiohttp==3.10.10, aiohappyeyeballs, aiosignal, attrs, frozenlist, multidict, yarl, propcache, pyrogram==2.0.106, pyaes, pysocks, tgcrypto (смотреть список wheel на chaquo.com/pypi-13.1/<имя>/). aiogram 3 во встроенном Python невозможен (pydantic-core нет в репозитории Chaquopy). Также добавить Linux-скрипт yfee-python (pip install uv; uv python install NN; uv venv --python NN venvNN) и в yfee-help. ОТКАТ: если после слияния сборка красная из-за Python/пакетов и двух попыток исправить мало, вернуть 16.0.0 / "3.11" / исходные install(...) (git revert коммита апгрейда), убедиться в зелёной сборке и сообщить пользователю в финале, что 3.13 во встроенном рантайме не получилось и почему.
- [x] 10c. «Всё готово с первого запуска» (выбор пользователя): Linux-скрипт yfee-all (apk add bash git python3 py3-pip nodejs npm openssh nano vim curl; pip install uv; uv python install 3.13 + ссылка python313 в /usr/local/bin; вывод по шагам с галочками, повтор безопасен) + запуск его автоматически после первой установки Linux (в LinuxScreen/LinuxTerm, где идёт `linux install`; если места нет, то подсказка «Выполни yfee-all» в конце установки) + строка в yfee-help. Быстрый нативный режим без proot признан нереальным (бинарники должны быть пересобраны под путь приложения, запуск из filesDir запрещён с targetSdk 29+), в финале объяснить это пользователю.
- [x] 11. Обновить README (счётчики уроков/практик), CHANGELOG, HelpScreen; ревью `git diff main..dev` на ошибки компиляции (скобки, `$`, запятые между Lesson).
- [x] 12. Слить dev в main (checkout main, pull, merge --ff-only dev, push), ждать сборку (gh run list --workflow build.yml --limit 1).
- [x] 13. Если красная: gh run view <id> --log-failed | grep -E " e: |error:", починить, повторить.
- [x] 14. КОНТРОЛЬНАЯ ТОЧКА (не финал!): SendUserMessage по-русски, коротко: что добавлено с прошлой точки, как обновиться (приложение предложит или APK из релиза latest den1511bol/YfeeCode), что не проверено на телефоне, ссылка (gh run list --workflow build.yml --status success --limit 1 --json url). Напоминание ставить ДАЛЬШЕ: цикл бесконечный (решение пользователя: делать «лучше Termux» хоть несколько суток), берём пункты из БЭКЛОГА ниже.

## БЭКЛОГ (после пункта 14; после каждых 7 пунктов бэклога: ревью, слияние dev в main, зелёная сборка, короткий SendUserMessage с итогами — это новая контрольная точка; затем снова бэклог)
- [ ] B1. Менеджер ботов: экран/вкладка со списком запусков, статус, кнопки старт/стоп/лог; автоперезапуск бота при падении (с задержкой 5-60 с, лимит попыток) в BotService (осторожно с WakeLock).
- [ ] B2. Просмотр лога бота: поиск по логу, фильтр «только ошибки», кнопка «копировать».
- [ ] B3. Traceback кликабельный: в выводе ошибки «File "x.py", line N» нажатие открывает файл в редакторе на этой строке.
- [ ] B4. Git-помощники в Linux: yfee-gitpush (add, commit с сообщением, push), yfee-gitlog, yfee-gitst; подсказки в yfee-help.
- [ ] B5. Мастер нового проекта: «Новый проект» (бот telebot / aiogram 3 / модуль Hikka / Flask для Heroku) создаёт папку с main-файлом, Procfile, requirements.txt, .gitignore, .env.
- [ ] B6. Свои сниппеты: пользователь сохраняет выделенный код как сниппет (имя) и вставляет по имени; хранение в SharedPreferences/файле.
- [ ] B7. Редактор: переименование слова во всём файле (replace по целым словам), переход к определению def/class по тапу на имя + кнопка «назад» к месту.
- [ ] B8. Тестер Hikka-модулей: mod файл.py .команда с аргументами и имитацией reply, self.db и config в песочнице (hikka_sandbox.py) — расширить и проверить на шаблонах.
- [ ] B9. Курс: углубление (asyncio, sqlite, requests, логирование) и проекты по шагам, ещё 10+ практик.
- [ ] B10. Резервная копия проектов одной кнопкой в /sdcard/Download (zip) + восстановление; проверка существующих ZIP↑/ZIP↓.
- [ ] B11. Цвета терминала: схемы (чёрно-красная, зелёная, светлая), переключатель в настройках; только если можно сделать без внутренностей эмулятора.
- [ ] B12. Автодополнение: больше слов и методов для telebot/aiogram/Hikka (контекстные после точки: bot., message., utils., loader.).
- [ ] B13. Проверка зависимостей: при импорте отсутствующего пакета в Linux предлагать команду pip install имя; в встроенном Python подсказка уже есть.
- [ ] B14. Улучшения по отзывам пользователя (проверить сообщения в чате и CHANGELOG; если нет новых, пойти по списку идей: производительность терминала, поиск по проектам, быстрое переключение файлов).

- [ ] B15. Терминал в режиме sh: если введена явно питоновская строка (print(...), import ..., def ..., x = ...), напечатать подсказку «Это Python: нажми кнопку py вверху». Не мешает обычным командам.
- [ ] B16. Автозакрытие скобок и кавычек во вкладке Linux (клавиатура пишет в pty): при вводе "(" отправлять "()" + стрелку влево (ESC [ D), вести стек ожидающих закрывающих, при вводе закрывающей — стрелка вправо вместо символа; стек сбрасывать на Enter и Ctrl-клавишах; тумблер в настройках.
- [ ] B17. Linux-скрипт yfee-clone <ссылка> [ветка]: клонирует (приватный репозиторий через токен, настроенный yfee-git-setup), создаёт venv, ставит requirements.txt (с понятными подсказками при ошибках сборки: gcc musl-dev python3-dev), подхватывает .env, спрашивает главный файл (main.py/bot.py/__main__.py ищет сам) и запускает через yfee-keepalive; yfee-pull для обновления и перезапуска. Добавить в yfee-help.

Заметка к 10b: коммит апгрейда Python = 9747fab (build.gradle.kts, app/build.gradle.kts). Откат: git revert 9747fab (вернёт Chaquopy 16.0.0, Python 3.11, abiFilters arm64+armeabi-v7a и исходные install). yfee-python остаётся.

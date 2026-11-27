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
- [x] U1 (движок: choice/gap/fix, ошибочные задания повторяются в конце; MATCH и сердечки не делал). ПРИОРИТЕТ (желание пользователя 27.11: «практика буквально на каждый шаг как в Дуолинго, максимально понятно»). Движок уроков: новые виды заданий Kind.CHOICE (выбери ответ из 3-4 вариантов), Kind.FIX (найди и исправь ошибку в строке: ввод), Kind.GAP (вставь пропущенное слово: блоки), Kind.MATCH (соедини пары), плюс экран результата урока с разбором ошибок и повтором ошибочных заданий в конце, сердечки/серия по желанию. Хелперы в LearnModel.kt: choice(prompt, correct, wrong..., explain=), fix(prompt, broken, answers..., explain=), gap(prompt, template с ___, answers, extra, explain=). UI в LearnScreen. Сначала движок и UI (U1), потом наполнение (U2..).
- [ ] U5. Подсказки «на все случаи жизни» (желание 27.11): сделано 35 Python-сниппетов (цитаты, HTML-теги Telegram, эмодзи, файлы, json, sql, requests). Сниппеты для JS/HTML/CSS/C++/C# (по 10-13 на язык) сделаны. Подсказки по типичным ошибкам в красной пометке (= вместо ==, await вне async, return/break вне цикла, запятая, невидимые символы) сделаны. Дальше: контекстные подсказки внутри строк (после `<` предлагать теги Telegram в строках, после `f"` скобки), подсказка по ошибкам на лету (обычные ошибки новичка: = вместо ==, забытое двоеточие, await), список «Шпаргалка» в редакторе по кнопке.
- [x] U2. Наполнение: в каждый урок курса «Боты и Heroku» добавить до 10-12 шагов: после каждого абзаца теории мини-задание (choice/gap/fix), всего минимум 2 задания на каждую мысль. Делать по 1 файлу LearnBotsN.kt за сессию (U2.1..U2.9).
  (подпункты U2.1..U2.9 = LearnBots1..9.kt; U2.1 готов (LearnBots1), U2.2, U2.3, U2.9 готовы (весь курс «Боты и Heroku»))
- [x] U3. (Python: все 41 урок готовы; JS, HTML, CSS готовы; C# готов: U3 выполнен полностью; helper /tmp/ap2.py apply2(fn, {индекс: [строки]}); дальше JS, HTML, CSS, C++, C#). То же для Python (41 урок), затем JS, HTML, CSS, C++, C#: добавить choice/gap/fix к существующим урокам. По 1-2 урока курса за сессию, не ломать существующие.
- [ ] U4. Понятность: к каждой теории пример «что напечатается», словарик терминов в уроке, подсказка к заданию (кнопка «Подсказка»), объяснение после каждого ответа (сейчас есть не везде). (кнопка подсказки + список ошибок сделаны)
- [x] B1 (только автоперезапуск, команда autorestart on|off; экран менеджера вынесен в B1b). Менеджер ботов: экран/вкладка со списком запусков, статус, кнопки старт/стоп/лог; автоперезапуск бота при падении (с задержкой 5-60 с, лимит попыток) в BotService (осторожно с WakeLock).
- [x] B1b (простая версия: команда status; полноценный экран позже). Экран менеджера ботов: список запусков, статус, старт/стоп/лог.
- [x] B2 (log err, log find слово; кнопки в UI позже). Просмотр лога бота: поиск по логу, фильтр «только ошибки», кнопка «копировать».
- [x] B3. Traceback кликабельный: в выводе ошибки «File "x.py", line N» нажатие открывает файл в редакторе на этой строке.
- [x] B4. Git-помощники в Linux: yfee-gitpush (add, commit с сообщением, push), yfee-gitlog, yfee-gitst; подсказки в yfee-help.
- [x] B5 (команда new). Мастер нового проекта: «Новый проект» (бот telebot / aiogram 3 / модуль Hikka / Flask для Heroku) создаёт папку с main-файлом, Procfile, requirements.txt, .gitignore, .env.
- [x] B6 (snip add|list|del). Свои сниппеты: пользователь сохраняет выделенный код как сниппет (имя) и вставляет по имени; хранение в SharedPreferences/файле.
- [x] B7 (кнопка слово в поиске; переход к определению позже). Редактор: переименование слова во всём файле (replace по целым словам), переход к определению def/class по тапу на имя + кнопка «назад» к месту.
- [x] B8 (расширены заглушки utils, loader, Message). Тестер Hikka-модулей: mod файл.py .команда с аргументами и имитацией reply, self.db и config в песочнице (hikka_sandbox.py) — расширить и проверить на шаблонах.
- [ ] B9. Курс: углубление (asyncio, sqlite, requests, логирование) и проекты по шагам, ещё 10+ практик.
- [x] B10 (команда backup; восстановление через ZIP↓). Резервная копия проектов одной кнопкой в /sdcard/Download (zip) + восстановление; проверка существующих ZIP↑/ZIP↓.
- [x] B11 (theme red|green|light). Цвета терминала: схемы (чёрно-красная, зелёная, светлая), переключатель в настройках; только если можно сделать без внутренностей эмулятора.
- [x] B12. Автодополнение: больше слов и методов для telebot/aiogram/Hikka (контекстные после точки: bot., message., utils., loader.).
- [x] B13 (yfee-py). Проверка зависимостей: при импорте отсутствующего пакета в Linux предлагать команду pip install имя; в встроенном Python подсказка уже есть.
- [ ] B14. Улучшения по отзывам пользователя (проверить сообщения в чате и CHANGELOG; если нет новых, пойти по списку идей: производительность терминала, поиск по проектам, быстрое переключение файлов).

- [x] B15. Терминал в режиме sh: если введена явно питоновская строка (print(...), import ..., def ..., x = ...), напечатать подсказку «Это Python: нажми кнопку py вверху». Не мешает обычным командам.
- [x] B16. Автозакрытие скобок и кавычек во вкладке Linux (клавиатура пишет в pty): при вводе "(" отправлять "()" + стрелку влево (ESC [ D), вести стек ожидающих закрывающих, при вводе закрывающей — стрелка вправо вместо символа; стек сбрасывать на Enter и Ctrl-клавишах; тумблер в настройках.
- [x] B17. Linux-скрипт yfee-clone <ссылка> [ветка]: клонирует (приватный репозиторий через токен, настроенный yfee-git-setup), создаёт venv, ставит requirements.txt (с понятными подсказками при ошибках сборки: gcc musl-dev python3-dev), подхватывает .env, спрашивает главный файл (main.py/bot.py/__main__.py ищет сам) и запускает через yfee-keepalive; yfee-pull для обновления и перезапуска. Добавить в yfee-help.

Заметка к 10b: коммит апгрейда Python = 9747fab (build.gradle.kts, app/build.gradle.kts). Откат: git revert 9747fab (вернёт Chaquopy 16.0.0, Python 3.11, abiFilters arm64+armeabi-v7a и исходные install). yfee-python остаётся.
- [x] N1. reqs файл.py (готово). [x] N2 tokencheck (готово).
- [x] N3 procfile, gitignore (готово). [x] N4 справка (готово).
- [x] N5 tree (готово). [x] N6 ver (готово).
- [x] N7 addcmd (готово). [x] N8 wc/head/tail (готово).
- [x] N10 ip (готово). [x] N11 diff (готово).
- [x] N12 open (готово).
- [x] N13 envinit (готово).
- [x] N14 grep (готово). [ ] N15 check файл.py.
- [x] N15 check (готово).
- [x] N16 сниппеты (готово: hcmd/hwatch/hcfg/quote/emoji/sphoto уже были; добавлены ikb, tbcb, aiocmd, aiocb, aiofsm).
- [x] N18 справка в приложении (готово).
- [x] N19 слова Telethon (готово).
- [x] N20 cat -n (работает через sh, правка не нужна). [x] N21 аудит: флаги передаются в sh.

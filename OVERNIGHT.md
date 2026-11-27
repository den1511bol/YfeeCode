# Ночной цикл v5: курс «Боты и Heroku» (разработчик @yfeeeee)
Цель: новый курс id "bots" (🤖) с уроками и практикой. Работа в ветке dev (CI не собирает dev). 14 сессий по 10 минут.
Формат (см. LearnPython.kt): Lesson("Название", "теория; строки кода начинаются с '>'", listOf(build("задание","токен|токен|...","лишние|блоки","пояснение"), type("задание","ответ1","ответ2"))). В теории и ответах Kotlin-строки: `$` писать как `\$`, кавычки `\"`. 5-6 упражнений на урок, 4-6 уроков за сессию. Проверка type сравнивает токены (кавычки ' и " равны).
Файлы: LearnBots1.kt, LearnBots2.kt ... каждый содержит `fun botsLessonsN(): List<Lesson>`. LearnBots.kt: `fun botsCourse() = Course("bots", "Боты и Heroku", "🤖", botsLessons1() + ...)`. Практика: BotsPractices.kt `fun botsPractices(): List<Practice>` (Lang.PYTHON, без expectOut, только must()-проверки regex); подключить в Practices.forCourse для id "bots". Добавить botsCourse() в Courses.all (LearnModel.kt).
Правила: не ломать Updater.kt, app/yfeecode.jks, applicationId, строку "Commit: <sha>" в build.yml, versionCode из GITHUB_RUN_NUMBER, AutoSpace.kt, TermHints.kt, WakeLock, шаг "Fetch proot". Kotlin локально не собрать: писать консервативно, скобки считать. Без вопросов, экономить лимит. Каждая сессия: ReadNotifications, поставить следующее напоминание, ОДИН пункт, коммит в dev, отметка [x].

- [x] 1. Каркас (LearnBots.kt, подключение в Courses и Practices) + LearnBots1.kt: что такое бот, BotFather и токен, pip install, первый бот telebot (4-5 уроков).
- [x] 2. LearnBots2.kt: telebot: команды, обработчики текста, reply_to, клавиатуры (reply и inline).
- [ ] 3. LearnBots3.kt: telebot: callback_query, register_next_step_handler, отправка фото/файлов, parse_mode, обработка ошибок.
- [ ] 4. LearnBots4.kt: aiogram 3: Bot, Dispatcher, Command, async/await, polling.
- [ ] 5. LearnBots5.kt: aiogram 3: фильтры, F-фильтры, inline-клавиатуры, callback, FSM.
- [ ] 6. LearnBots6.kt: хранение данных: json, sqlite3, .env и os.environ, логирование.
- [ ] 7. LearnBots7.kt: модули Hikka/Heroku: структура, @loader.tds, strings, команды, utils.answer, get_args_raw.
- [ ] 8. LearnBots8.kt: модули: ModuleConfig, watcher, self.db, inline-формы, тестирование командой mod.
- [ ] 9. LearnBots9.kt: деплой на Heroku: Procfile, requirements.txt, runtime.txt, git push heroku, config:set, логи, webhook vs polling.
- [ ] 10. BotsPractices.kt: 10-12 практик (regex-проверки) по всем темам + уроки-проекты (LearnBots10.kt: бот-заметки, модуль-счётчик).
- [ ] 11. Обновить README (счётчики уроков/практик), CHANGELOG, HelpScreen; ревью `git diff main..dev` на ошибки компиляции (скобки, `$`, запятые между Lesson).
- [ ] 12. Слить dev в main (checkout main, pull, merge --ff-only dev, push), ждать сборку (gh run list --workflow build.yml --limit 1).
- [ ] 13. Если красная: gh run view <id> --log-failed | grep -E " e: |error:", починить, повторить.
- [ ] 14. ФИНАЛ: без нового напоминания; SendUserMessage по-русски: что добавлено (сколько уроков и практик), как обновиться (приложение предложит или APK из релиза latest den1511bol/YfeeCode), что не проверено, ссылка (gh run list --workflow build.yml --status success --limit 1 --json url).

# Ночной цикл v4 (разработчик @yfeeeee)
Цель: YfeeCode лучше Termux. 14 сессий по 10 минут. Работа в ветке dev (CI не собирает dev). Сессия 12 сливает dev в main (ff-only) и ждёт зелёную сборку, 13 чинит если красная, 14 финал (без нового напоминания, SendUserMessage по-русски с итогами и ссылкой на сборку).
Правила: не ломать Updater.kt, app/yfecode.jks, applicationId, строку "Commit: <sha>" в build.yml, versionCode из GITHUB_RUN_NUMBER, AutoSpace.kt, TermHints.kt, WakeLock в BotService/LinuxService, шаг "Fetch proot". В Kotlin-строках `$` как `\$`. Kotlin локально не собрать: писать консервативно. Без вопросов, экономить лимит. Каждая сессия: ReadNotifications, поставить следующее напоминание, ОДИН пункт, коммит в dev, отметка [x].

- [x] 1. Редактор: подсветка парной скобки у курсора (EditorState + CodeEditor), мягко и безопасно.
- [x] 2. Редактор: кнопки «вверх/вниз» для перемещения строки (moveLineUp/Down) в мини-панели.
- [x] 3. Терминал (TerminalState): команда `env` для токена бота (env set/list/del в файле .env проекта) и автоподхват .env при run.
- [x] 4. Linux-скрипты: yfee-update (pkg upgrade + проверка), yfee-ports (что слушает), yfee-tmux-подсказка; добавить в yfee-help.
- [x] 5. Heroku: yfee-heroku deploy (runtime.txt, app.json, .gitignore, Procfile) расширить.
- [x] 6. Шаблоны (Templates.kt): FastAPI/Flask-бот вебхук для Heroku, Hikka-модуль с конфигом и watcher.
- [x] 7. Сниппеты: aiogram FSM, inline-клавиатура, telebot inline, Hikka inline.
- [x] 8. Подсказки ошибок (runner.py _hint): добавить частые ошибки aiogram/telebot/Hikka.
- [x] 9. Терминал: история команд стрелками вверх/вниз во встроенном терминале, если ещё нет; иначе пропустить. (пропущено: история стрелками уже есть)
- [x] 10. Обновить HelpScreen.kt, README.md, CHANGELOG.md, yfee-help.
- [x] 11. Ревью `git diff main..dev` на ошибки компиляции (скобки, импорты, `$`, типы).
- [x] 12. Слить dev в main (checkout main, pull, merge --ff-only dev, push), ждать сборку (gh run list --workflow build.yml --limit 1).
- [ ] 13. Если красная: gh run view <id> --log-failed | grep -E " e: |error:", починить, повторить; если зелёная ничего.
- [ ] 14. ФИНАЛ: без нового напоминания; SendUserMessage по-русски: что добавлено, как обновиться (приложение предложит или APK из релиза latest den1511bol/YfeeCode), что не проверено на телефоне, ссылка (gh run list --workflow build.yml --status success --limit 1 --json url).

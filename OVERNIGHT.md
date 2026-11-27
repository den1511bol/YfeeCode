# План работ v3 (внутренний файл, разработчик @yfeeeee)

Цель пользователя: сделать YfeeCode лучше Termux. Приоритеты: боты и модули Heroku, удобство терминала и клавиатуры, редактор как в IDE, надёжность Linux. Работа в ветке `dev` (сборка CI на неё НЕ запускается), в `main` вливается одним обновлением в сессии 12.

## Правила для каждой сессии
1. СРАЗУ поставь следующее напоминание (send_later, delay_minutes 10, initiation human_request, имя «YfeeCode v3: сессия N+1 из 14», текст: «Сессия N+1 из 14. Прочитай OVERNIGHT.md в репо den1511bol/YfeeCode (клон /home/claude/yfeecode, ветка dev; если папки нет: add_repo + git clone, git checkout dev) и выполни ОДИН следующий невыполненный пункт плана. Недельный лимит пользователя ограничен: работай экономно, мало чтения, короткий ответ. Пользователь спит, вопросов не задавай.»). После сессии 14 напоминание НЕ ставь.
2. Сделай ровно один пункт (первый без [x]), отметь [x], коммит в `dev`, push `origin dev` (НЕ в main, кроме пункта 12). В конце коммита строки `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>` и `Claude-Session: https://claude.ai/code/session_01CNTekWn644PFieFcqoekAv`. Цепочки через &&.
3. Сборки нет: Kotlin пиши предельно аккуратно (импорты, скобки, типы, `$` в строках экранируй `\$`, только знакомые API). Лучше простое решение, которое точно соберётся. Для скриптов в Linux.kt: проверяй, что в строке 4 неэкранированных кавычки (ключ+значение) и нет неэкранированных `$`.
4. НЕ ломай: Updater.kt, app/yfeecode.jks, applicationId, строку «Commit: <sha>» в notes релиза (build.yml), versionCode из GITHUB_RUN_NUMBER, AutoSpace.kt, TermHints.kt, WakeLock в BotService/LinuxService, шаг «Fetch proot» в build.yml. Опасных удалений нет, сеть/права не обходить.
5. Везде указывай разработчика @yfeeeee. Запись в CHANGELOG.md на каждый пункт (1 строка). Системная клавиатура на устройстве пользователя не работает: только встроенная DevKeyboard.

## Пункты плана
- [x] 1. Терминал-вкладка (TerminalState.kt): команда `log [N]` печатает последние N строк лога бота (по умолчанию 30, файл store.logFile); добавь `log` в быстрые кнопки терминала (App.kt, список quick) и в help.
- [x] 2. Терминал: `mod файл.py` без команды печатает список команд модуля (hikka_sandbox.commands(path) через Chaquopy), вместо подсказки «Использование».
- [x] 3. Linux: скрипт `yfee-heroku` (установка Heroku CLI через npm, login, подсказки по деплою Procfile + git push heroku main).
- [x] 4. Linux-вкладка: в ряд клавиш добавить ^Z ^L ^R ^A ^E ^W ^U.
- [x] 5. Редактор: кнопки перехода по словам («←слово», «слово→») и «в начало», «в конец» файла (EditorState + мини-панель).
- [x] 6. Linux: при каждом запуске оболочки гарантировать /etc/resolv.conf с DNS (1.1.1.1, 8.8.8.8), если файла нет или он пуст (Linux.kt, installScripts).
- [x] 7. Linux: скрипт `yfee-doctor` (диагностика: сеть, DNS, место, версии, pkg, запись отчёта в /work/yfee-doctor.txt) и упоминание в yfee-help.
- [x] 8. Редактор: для HTML при вводе `>` автоматически вставлять закрывающий тег (кроме void-тегов, комментариев, закрывающих и самозакрывающихся), курсор между тегами.
- [x] 9. Подсказки/сниппеты (Completer.kt/Hints.kt): добавить сниппеты для Hikka-модуля и aiogram, если структура позволяет простую вставку.
- [x] 10. Обновить HelpScreen.kt, README.md, CHANGELOG.md, yfee-help под всё новое.
- [x] 11. Ревизия: перечитай `git diff main..dev` по Kotlin и Python, найди ошибки компиляции и исправь.
- [x] 12. Влей dev в main (`git checkout main && git pull && git merge --ff-only dev`), push main, дождись сборки (gh run list --workflow build.yml --limit 1). Красная: `gh run view <id> --log-failed | grep -E " e: |error:"`, исправь, повтори.
- [ ] 13. Если сборка красная: довести до зелёной. Если зелёная: ничего не делай.
- [ ] 14. Финал: НЕ ставь напоминаний. SendUserMessage с подробным итогом по-русски (что добавлено, как обновиться, что не проверено на телефоне) и ссылкой на последнюю успешную сборку (gh run list --workflow build.yml --status success --limit 1 --json url).

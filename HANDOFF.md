# Передача работы в новый чат (автор: @yfeeeee)

Скопируй блок ниже в новый чат целиком.

```
Ты продолжаешь работу над моим Android-приложением YfeeCode. Я разработчик @yfeeeee, во всём, что ты создаёшь, указывай меня разработчиком. Отвечай по-русски, коротко (я с телефона), сначала ответ. Вопросы мне задавай формой с вариантами ответов. Я в основном делаю модули для Heroku (Hikka) и ботов Telegram.

РЕПОЗИТОРИЙ: https://github.com/den1511bol/YfeeCode (владелец den1511bol). Если локальной папки нет: add_repo + git clone, затем git checkout dev. Пакет dev.yfeeeee.yfeecode. Работаем в ветке dev, CI собирает main.

ПЕРВЫМ ДЕЛОМ: прочитай OVERNIGHT.md, CHANGELOG.md и HANDOFF.md в репозитории, там вся история и очередь пунктов. Убедись, что подключены мои плагины (ListPlugins): kotlin-lsp, code-review, code-simplifier, superpowers, design, frontend-design, telegram, Context7 и плагины экономии токенов. Если kotlin-lsp доступен, пользуйся его диагностикой: локального компилятора Kotlin нет. Также проверь Railway (whoami, list-projects): там хостятся мои боты, читать логи и статусы можно свободно, а деплой, удаление и смену переменных делай только после моего подтверждения.

КОНТРОЛЬНАЯ ТОЧКА после каждого пункта: git add -A && git commit; git checkout main && git merge --ff-only dev && git push origin main; git checkout dev; git push origin dev; gh run list --workflow build.yml --limit 1; gh run watch <id> --exit-status; при красной сборке: gh run view <id> --log-failed | grep " e: ", починить. Коммиты заканчивай строками Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com> и Claude-Session со ссылкой этой сессии. Правки Kotlin маленькие и аккуратные: в Kotlin-строках без символа доллара, кавычки экранировать.

ОЧЕРЕДЬ (бери ОДИН пункт за раз, сверяй находку с реальным кодом перед правкой):
- F4: configChanges в AndroidManifest (uiMode|locale|fontScale|layoutDirection|screenLayout|smallestScreenSize), чтобы терминал не терялся при смене темы.
- Дальше мелочи из ревью: доступность в LearnUi.kt, LinuxScreen.kt, HelpScreen.kt (role и описания, прогресс-бары), деление на ноль в LearnUi при пустом списке, защита экспорта и импорта zip в MainActivity от падений и ANR.
- Практики (N96 cpp34, N97 cs33, N98 html32, N99 css32 и по кругу) ведёт ДРУГОЙ чат по таймеру, их не трогай, пока я не скажу. Перед добавлением практики проверь grep, что такой темы ещё нет. После каждой практики: README (счётчик практик, сейчас 194, правильная форма слова «практик/практика/практики»), строка в CHANGELOG.md, отметка в OVERNIGHT.md.

ПЛАНИРОВАНИЕ: в начале сессии составь короткий план на ближайшие пункты (список задач через TaskCreate, для крупных изменений скилл superpowers:writing-plans), по ходу отмечай выполненное и дописывай новые находки в OVERNIGHT.md. Раз в несколько пунктов пересматривай очередь: что важнее для пользователя, то и вперёд. Мои новые пожелания важнее очереди.

ЦИКЛ: после каждого пункта поставь напоминание через 10 минут (send_later) с названием «YfeeCode v5: <следующий пункт>» и полным текстом задания, чтобы цикл шёл сам. Цикл бесконечный. Отчитывайся мне только когда сделано что-то заметное, коротко.

ГОТОВО К ЭТОМУ МОМЕНТУ: практики до N95 (194 штуки), HelpRich для Heroku (hikka_modules/HelpRich.py, v19), гайд-страница HelpRich: https://claude.ai/artifact/Sx9GXvenk7ckSFk85CGw3Z, исправления F1 (BotService commit), F2 (терминал try/catch и автоскролл), F3 (контраст, кнопки 48dp, TalkBack у акцентов).
```

// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons3(): List<Lesson> = listOf(
    Lesson("Нажатия inline-кнопок", "Когда человек жмёт inline-кнопку, приходит callback.\n>@bot.callback_query_handler(func=lambda c: True)\n>def on_click(call):\n>    bot.answer_callback_query(call.id, \"Нажато\")\n>    bot.send_message(call.message.chat.id, call.data)\ncall.data — то, что мы положили в callback_data. answer_callback_query нужен, чтобы у кнопки исчезли «часики».", listOf(
        choice("Какой обработчик ловит нажатия inline-кнопок?", "callback_query_handler", "message_handler", "button_handler", explain = "Нажатие приходит не как сообщение, а как callback."),
        choice("Зачем вызывать answer_callback_query?", "Чтобы убрать часики на кнопке", "Чтобы удалить кнопку", "Чтобы отправить фото", explain = "Без ответа на кнопке крутятся часики."),
        gap("Дополни: данные нажатой кнопки лежат в call.___", "if call.___ == \"btn1\":", "data", "text|id", explain = "В call.data лежит то, что мы задали в callback_data."),
        fix("Исправь: сравнение пишется через ==", "if call.data = \"btn1\":", "if call.data == \"btn1\":"),
        fix("Исправь название обработчика", "@bot.callback_handler(func=lambda c: True)", "@bot.callback_query_handler(func=lambda c: True)"),
        build("Обработчик нажатий кнопок", "@bot|.|callback_query_handler|(|func|=|lambda|c|:|True|)", "message_handler|commands|="),
        build("Ответь на нажатие", "bot|.|answer_callback_query|(|call|.|id|)", "reply_to|message", "Без answer_callback_query кнопка будет «вечно грузиться»."),
        type("Получи данные кнопки в переменную data", "data = call.data"),
        build("Проверь, какая кнопка нажата", "if|call|.|data|==|\"btn1\"|:", "message|text|elif"),
        type("Номер чата из callback в переменную chat_id", "chat_id = call.message.chat.id")
    )),
    Lesson("Диалог из нескольких шагов", "Чтобы задать вопрос и получить ответ, используют register_next_step_handler.\n>def ask(message):\n>    msg = bot.send_message(message.chat.id, \"Как зовут?\")\n>    bot.register_next_step_handler(msg, got_name)\n>def got_name(message):\n>    bot.reply_to(message, \"Привет, \" + message.text)\nСледующее сообщение человека попадёт в функцию got_name.", listOf(
        choice("Для чего нужен register_next_step_handler?", "Чтобы получить ответ на заданный вопрос", "Чтобы остановить бота", "Чтобы отправить файл", explain = "Следующее сообщение человека придёт в указанную функцию."),
        choice("Что передают в register_next_step_handler первым параметром?", "Сообщение, которое отправил бот (msg)", "Токен", "Номер телефона"),
        gap("Дополни: ждём ответ в функции get_name", "bot.register_next_step_handler(msg, ___)", "get_name", "ask|print", explain = "Передаём имя функции без скобок."),
        fix("Исправь: функцию передают без скобок", "bot.register_next_step_handler(msg, get_name())", "bot.register_next_step_handler(msg, get_name)", explain = "Со скобками функция вызвалась бы сразу."),
        fix("Исправь: нужно сохранить сообщение бота в msg", "bot.send_message(message.chat.id, \"Как зовут?\")", "msg = bot.send_message(message.chat.id, \"Как зовут?\")"),
        build("Жди следующий ответ в функции got_name", "bot|.|register_next_step_handler|(|msg|,|got_name|)", "message_handler|next"),
        type("Сохрани отправленное сообщение в переменную msg", "msg = bot.send_message(message.chat.id, \"Как зовут?\")"),
        build("Ответь с именем", "bot|.|reply_to|(|message|,|\"Привет, \"|+|message|.|text|)", "send|chat"),
        build("Объяви функцию got_name", "def|got_name|(|message|)|:", "class|lambda"),
        type("Проверь, что пришёл именно текст", "if message.content_type == \"text\":", "if message.content_type == 'text':")
    )),
    Lesson("Файлы и картинки", "Бот умеет отправлять файлы, фото и документы.\n>with open(\"cat.jpg\", \"rb\") as f:\n>    bot.send_photo(message.chat.id, f)\n>bot.send_document(message.chat.id, open(\"data.txt\", \"rb\"))\nФайл открывают в режиме \"rb\" — чтение байтов.", listOf(
        choice("В каком режиме открывают файл для отправки?", "\"rb\" (бинарный)", "\"w\" (запись)", "\"r\" (текст)", explain = "Фото и документы отправляют как байты: rb."),
        choice("Каким методом отправляют картинку?", "send_photo", "send_image_file", "reply_pic"),
        gap("Дополни: открой файл для отправки", "with open(\"cat.jpg\", \"___\") as f:", "rb", "w|r", explain = "rb значит read binary."),
        gap("Дополни: отправь документ", "bot.___(message.chat.id, open(\"data.txt\", \"rb\"))", "send_document", "send_file|send_photo"),
        fix("Исправь режим открытия фото", "with open(\"cat.jpg\", \"r\") as f:", "with open(\"cat.jpg\", \"rb\") as f:", explain = "Для фото нужен бинарный режим rb."),
        build("Отправь фото из файла f", "bot|.|send_photo|(|message|.|chat|.|id|,|f|)", "send_message|reply"),
        build("Открой файл как байты", "open|(|\"cat.jpg\"|,|\"rb\"|)", "\"r\"|write"),
        type("Отправь документ из файла f в чат", "bot.send_document(message.chat.id, f)"),
        build("Отправь фото по ссылке", "bot|.|send_photo|(|message|.|chat|.|id|,|\"https://example.com/a.jpg\"|)", "document|file"),
        type("Подпиши фото: параметр caption", "bot.send_photo(message.chat.id, f, caption=\"Кот\")")
    )),
    Lesson("Оформление текста", "Текст можно оформлять жирным и курсивом через parse_mode.\n>bot.send_message(message.chat.id, \"<b>Жирный</b> и <i>курсив</i>\", parse_mode=\"HTML\")\nВ HTML-режиме тег <b> делает жирный, <i> курсив, <code> моноширинный. Символы < и > в обычном тексте нужно заменять на &lt; и &gt;, иначе будет ошибка разметки.", listOf(
        choice("Как включить HTML-оформление?", "parse_mode=\"HTML\"", "style=\"HTML\"", "format=html", explain = "parse_mode говорит Telegram, как читать разметку."),
        choice("Какой тег делает текст жирным?", "<b>", "<i>", "<u2>"),
        gap("Дополни: курсив", "\"<___>наклон</i>\"", "i", "b|em2"),
        fix("Исправь: тег нужно закрыть", "\"<b>Жирный\"", "\"<b>Жирный</b>\"", explain = "Каждому открывающему тегу нужен закрывающий."),
        fix("Исправь название параметра", "bot.send_message(chat_id, \"<b>Hi</b>\", parsemode=\"HTML\")", "bot.send_message(chat_id, \"<b>Hi</b>\", parse_mode=\"HTML\")"),
        build("Включи HTML-режим", "parse_mode|=|\"HTML\"", "mode|=|html"),
        build("Жирное слово в HTML", "<b>|Привет|</b>", "<i>|</i>|</b>"),
        type("Курсив для слова Привет", "<i>Привет</i>"),
        build("Моноширинный текст", "<code>|text|</code>", "<b>|</code>"),
        type("Отправь сообщение с HTML-режимом", "bot.send_message(message.chat.id, text, parse_mode=\"HTML\")")
    )),
    Lesson("Ошибки и устойчивость", "Бот не должен падать от одной ошибки. Ошибку ловят через try/except, а polling можно запустить так, чтобы он не останавливался.\n>try:\n>    bot.send_message(chat_id, \"Привет\")\n>except Exception as e:\n>    print(e)\ninfinity_polling сам перезапускается при сбоях сети. Ошибки видны командой log.", listOf(
        choice("Что делает try/except?", "Ловит ошибку, чтобы бот не упал", "Ускоряет бота", "Скрывает токен", explain = "Код в except выполнится, если в try случилась ошибка."),
        choice("Что значит [done] crashed в логе?", "Бот упал с ошибкой", "Бот спокойно завершился", "Бот обновился", explain = "Над этой строкой в логе будет traceback с причиной."),
        gap("Дополни: поймай любую ошибку", "try:\n    bot.send_message(chat_id, \"Привет\")\n___ Exception as e:\n    print(e)", "except", "catch|finally"),
        fix("Исправь: в Python пишут except, а не catch", "catch Exception as e:", "except Exception as e:"),
        fix("Исправь: после except нужно двоеточие", "except Exception as e", "except Exception as e:"),
        build("Поймай ошибку", "try|:", "while|if"),
        build("Вторая часть конструкции", "except|Exception|as|e|:", "catch|then"),
        type("Выведи ошибку e", "print(e)"),
        build("Запусти polling без остановки", "bot|.|infinity_polling|(|)", "polling|stop"),
        build("Посмотреть ошибки бота в терминале", "log|30", "run|stop")
    ))
)

// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons3(): List<Lesson> = listOf(
    Lesson("Нажатия inline-кнопок", "Когда человек жмёт inline-кнопку, приходит callback.\n>@bot.callback_query_handler(func=lambda c: True)\n>def on_click(call):\n>    bot.answer_callback_query(call.id, \"Нажато\")\n>    bot.send_message(call.message.chat.id, call.data)\ncall.data — то, что мы положили в callback_data. answer_callback_query нужен, чтобы у кнопки исчезли «часики».", listOf(
        build("Обработчик нажатий кнопок", "@bot|.|callback_query_handler|(|func|=|lambda|c|:|True|)", "message_handler|commands|="),
        build("Ответь на нажатие", "bot|.|answer_callback_query|(|call|.|id|)", "reply_to|message", "Без answer_callback_query кнопка будет «вечно грузиться»."),
        type("Получи данные кнопки в переменную data", "data = call.data"),
        build("Проверь, какая кнопка нажата", "if|call|.|data|==|\"btn1\"|:", "message|text|elif"),
        type("Номер чата из callback в переменную chat_id", "chat_id = call.message.chat.id")
    )),
    Lesson("Диалог из нескольких шагов", "Чтобы задать вопрос и получить ответ, используют register_next_step_handler.\n>def ask(message):\n>    msg = bot.send_message(message.chat.id, \"Как зовут?\")\n>    bot.register_next_step_handler(msg, got_name)\n>def got_name(message):\n>    bot.reply_to(message, \"Привет, \" + message.text)\nСледующее сообщение человека попадёт в функцию got_name.", listOf(
        build("Жди следующий ответ в функции got_name", "bot|.|register_next_step_handler|(|msg|,|got_name|)", "message_handler|next"),
        type("Сохрани отправленное сообщение в переменную msg", "msg = bot.send_message(message.chat.id, \"Как зовут?\")"),
        build("Ответь с именем", "bot|.|reply_to|(|message|,|\"Привет, \"|+|message|.|text|)", "send|chat"),
        build("Объяви функцию got_name", "def|got_name|(|message|)|:", "class|lambda"),
        type("Проверь, что пришёл именно текст", "if message.content_type == \"text\":", "if message.content_type == 'text':")
    )),
    Lesson("Файлы и картинки", "Бот умеет отправлять файлы, фото и документы.\n>with open(\"cat.jpg\", \"rb\") as f:\n>    bot.send_photo(message.chat.id, f)\n>bot.send_document(message.chat.id, open(\"data.txt\", \"rb\"))\nФайл открывают в режиме \"rb\" — чтение байтов.", listOf(
        build("Отправь фото из файла f", "bot|.|send_photo|(|message|.|chat|.|id|,|f|)", "send_message|reply"),
        build("Открой файл как байты", "open|(|\"cat.jpg\"|,|\"rb\"|)", "\"r\"|write"),
        type("Отправь документ из файла f в чат", "bot.send_document(message.chat.id, f)"),
        build("Отправь фото по ссылке", "bot|.|send_photo|(|message|.|chat|.|id|,|\"https://example.com/a.jpg\"|)", "document|file"),
        type("Подпиши фото: параметр caption", "bot.send_photo(message.chat.id, f, caption=\"Кот\")")
    )),
    Lesson("Оформление текста", "Текст можно оформлять жирным и курсивом через parse_mode.\n>bot.send_message(message.chat.id, \"<b>Жирный</b> и <i>курсив</i>\", parse_mode=\"HTML\")\nВ HTML-режиме тег <b> делает жирный, <i> курсив, <code> моноширинный. Символы < и > в обычном тексте нужно заменять на &lt; и &gt;, иначе будет ошибка разметки.", listOf(
        build("Включи HTML-режим", "parse_mode|=|\"HTML\"", "mode|=|html"),
        build("Жирное слово в HTML", "<b>|Привет|</b>", "<i>|</i>|</b>"),
        type("Курсив для слова Привет", "<i>Привет</i>"),
        build("Моноширинный текст", "<code>|text|</code>", "<b>|</code>"),
        type("Отправь сообщение с HTML-режимом", "bot.send_message(message.chat.id, text, parse_mode=\"HTML\")")
    )),
    Lesson("Ошибки и устойчивость", "Бот не должен падать от одной ошибки. Ошибку ловят через try/except, а polling можно запустить так, чтобы он не останавливался.\n>try:\n>    bot.send_message(chat_id, \"Привет\")\n>except Exception as e:\n>    print(e)\ninfinity_polling сам перезапускается при сбоях сети. Ошибки видны командой log.", listOf(
        build("Поймай ошибку", "try|:|", "while|if"),
        build("Вторая часть конструкции", "except|Exception|as|e|:", "catch|then"),
        type("Выведи ошибку e", "print(e)"),
        build("Запусти polling без остановки", "bot|.|infinity_polling|(|)", "polling|stop"),
        build("Посмотреть ошибки бота в терминале", "log|30", "run|stop")
    ))
)

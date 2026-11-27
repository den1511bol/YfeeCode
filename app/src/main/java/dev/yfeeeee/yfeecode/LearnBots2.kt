// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons2(): List<Lesson> = listOf(
    Lesson("Несколько команд", "Каждая команда — отдельная функция с декоратором.\n>@bot.message_handler(commands=[\"help\"])\n>def help_cmd(message):\n>    bot.send_message(message.chat.id, \"Я умею отвечать\")\nmessage.chat.id — номер чата, куда нужно отправить ответ. send_message отправляет сообщение в чат.", listOf(
        build("Обработчик команды /help", "@bot|.|message_handler|(|commands|=|[|\"help\"|]|)", "on|text"),
        build("Отправь сообщение в чат", "bot|.|send_message|(|message|.|chat|.|id|,|\"Привет\"|)", "reply|answer"),
        type("Получи номер чата в переменную chat_id", "chat_id = message.chat.id"),
        build("Одна функция для двух команд", "commands|=|[|\"start\"|,|\"hello\"|]", "command|=|(|)"),
        type("Определи функцию help_cmd с параметром message", "def help_cmd(message):")
    )),
    Lesson("Обработка текста", "Чтобы ответить на любой текст, используй обработчик без команд.\n>@bot.message_handler(func=lambda m: True)\n>def echo(message):\n>    bot.reply_to(message, message.text)\nmessage.text — текст, который прислал человек. Обработчик без условий нужно ставить последним, иначе он перехватит команды.", listOf(
        build("Обработчик любого сообщения", "@bot|.|message_handler|(|func|=|lambda|m|:|True|)", "commands|=|def"),
        build("Эхо: ответь тем же текстом", "bot|.|reply_to|(|message|,|message|.|text|)", "send|chat"),
        type("Получи текст сообщения в переменную text", "text = message.text"),
        build("Условие: текст равен «привет»", "if|message|.|text|==|\"привет\"|:", "=|elif|def"),
        build("Сделай текст маленькими буквами", "message|.|text|.|lower|(|)", "upper|len", "lower() приводит текст к нижнему регистру: проверка не зависит от заглавных.")
    )),
    Lesson("Клавиатура с кнопками", "Обычная клавиатура заменяет поле ввода кнопками.\n>from telebot import types\n>kb = types.ReplyKeyboardMarkup(resize_keyboard=True)\n>kb.add(\"Да\", \"Нет\")\n>bot.send_message(message.chat.id, \"Выбери\", reply_markup=kb)\nКогда человек нажмёт кнопку, бот получит обычное сообщение с текстом кнопки.", listOf(
        build("Импортируй types из telebot", "from|telebot|import|types", "import|aiogram"),
        build("Создай клавиатуру", "kb|=|types|.|ReplyKeyboardMarkup|(|resize_keyboard|=|True|)", "InlineKeyboardMarkup|False"),
        type("Добавь на клавиатуру кнопки Да и Нет", "kb.add(\"Да\", \"Нет\")", "kb.add('Да', 'Нет')"),
        build("Отправь сообщение с клавиатурой", "bot|.|send_message|(|message|.|chat|.|id|,|\"Выбери\"|,|reply_markup|=|kb|)", "keyboard|=|kb"),
        build("Убери клавиатуру", "types|.|ReplyKeyboardRemove|(|)", "ReplyKeyboardMarkup|Delete")
    )),
    Lesson("Inline-кнопки", "Inline-кнопки показываются под сообщением. Каждая кнопка несёт данные callback_data.\n>kb = types.InlineKeyboardMarkup()\n>kb.add(types.InlineKeyboardButton(\"Нажми\", callback_data=\"btn1\"))\n>bot.send_message(message.chat.id, \"Кнопка:\", reply_markup=kb)\nНажатие приходит не как сообщение, а как callback — его мы разберём в следующем уроке.", listOf(
        build("Создай inline-клавиатуру", "kb|=|types|.|InlineKeyboardMarkup|(|)", "ReplyKeyboardMarkup|Button"),
        build("Создай кнопку «Нажми»", "types|.|InlineKeyboardButton|(|\"Нажми\"|,|callback_data|=|\"btn1\"|)", "text|data"),
        type("Добавь кнопку btn на клавиатуру kb", "kb.add(btn)"),
        build("Кнопка-ссылка", "types|.|InlineKeyboardButton|(|\"Сайт\"|,|url|=|\"https://example.com\"|)", "callback_data|link"),
        type("Две кнопки в одном ряду", "kb.row(btn1, btn2)", explain: "row кладёт кнопки в одну строку, add — по умолчанию по одной в строке.")
    )),
    Lesson("Тип сообщения и ответы", "Бот может получить не только текст: фото, документ, голос. Обработчик выбирают по content_types.\n>@bot.message_handler(content_types=[\"photo\"])\n>def photo(message):\n>    bot.reply_to(message, \"Красивое фото\")\nОтветить можно разными способами: reply_to отвечает цитатой, send_message просто пишет в чат.", listOf(
        build("Обработчик фотографий", "@bot|.|message_handler|(|content_types|=|[|\"photo\"|]|)", "commands|=|text"),
        build("Обработчик документов", "content_types|=|[|\"document\"|]", "commands|=|[|photo"),
        type("Ответь на фото словом Круто", "bot.reply_to(message, \"Круто\")"),
        build("Отправь текст в чат без цитаты", "bot|.|send_message", "reply_to|answer"),
        type("Получи имя пользователя в переменную name", "name = message.from_user.first_name")
    ))
)

// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons2(): List<Lesson> = listOf(
    Lesson("Несколько команд", "Каждая команда — отдельная функция с декоратором.\n>@bot.message_handler(commands=[\"help\"])\n>def help_cmd(message):\n>    bot.send_message(message.chat.id, \"Я умею отвечать\")\nmessage.chat.id — номер чата, куда нужно отправить ответ. send_message отправляет сообщение в чат.", listOf(
        choice("Что такое message.chat.id?", "Номер чата, куда отвечать", "Текст сообщения", "Токен бота", explain = "По chat.id бот понимает, кому отправлять ответ."),
        choice("Какой метод отправляет сообщение в чат по его номеру?", "send_message", "reply_chat", "post_text"),
        gap("Дополни: отправь текст в чат", "bot.___(message.chat.id, \"Привет\")", "send_message", "get_message|print", explain = "send_message принимает номер чата и текст."),
        fix("Исправь: у функции-обработчика после def нужны скобки и двоеточие", "def help_cmd message:", "def help_cmd(message):", explain = "Обработчик получает один параметр message."),
        fix("Исправь команду /help (без слеша)", "@bot.message_handler(commands=[\"/help\"])", "@bot.message_handler(commands=[\"help\"])"),
        build("Обработчик команды /help", "@bot|.|message_handler|(|commands|=|[|\"help\"|]|)", "on|text"),
        build("Отправь сообщение в чат", "bot|.|send_message|(|message|.|chat|.|id|,|\"Привет\"|)", "reply|answer"),
        type("Получи номер чата в переменную chat_id", "chat_id = message.chat.id"),
        build("Одна функция для двух команд", "commands|=|[|\"start\"|,|\"hello\"|]", "command|=|(|)"),
        type("Определи функцию help_cmd с параметром message", "def help_cmd(message):")
    )),
    Lesson("Обработка текста", "Чтобы ответить на любой текст, используй обработчик без команд.\n>@bot.message_handler(func=lambda m: True)\n>def echo(message):\n>    bot.reply_to(message, message.text)\nmessage.text — текст, который прислал человек. Обработчик без условий нужно ставить последним, иначе он перехватит команды.", listOf(
        choice("Что хранит message.text?", "Текст, который прислал человек", "Номер чата", "Имя бота", explain = "Если пришло фото, text будет None."),
        choice("Где ставить обработчик «на любой текст»?", "В самом конце, после остальных", "В самом начале", "Не имеет значения", explain = "Обработчики проверяются по порядку, первый подходящий выигрывает."),
        gap("Дополни: ответ эхом", "bot.___(message, message.text)", "reply_to", "send_photo|print"),
        gap("Дополни условие: обработчик для любых сообщений", "@bot.message_handler(func=___ m: True)", "lambda", "def|import", explain = "lambda это короткая функция без имени."),
        fix("Исправь: сравнение пишется двумя знаками равно", "if message.text = \"привет\":", "if message.text == \"привет\":", explain = "Один знак = присваивает, два == сравнивают."),
        build("Обработчик любого сообщения", "@bot|.|message_handler|(|func|=|lambda|m|:|True|)", "commands|=|def"),
        build("Эхо: ответь тем же текстом", "bot|.|reply_to|(|message|,|message|.|text|)", "send|chat"),
        type("Получи текст сообщения в переменную text", "text = message.text"),
        build("Условие: текст равен «привет»", "if|message|.|text|==|\"привет\"|:", "=|elif|def"),
        build("Сделай текст маленькими буквами", "message|.|text|.|lower|(|)", "upper|len", "lower() приводит текст к нижнему регистру: проверка не зависит от заглавных.")
    )),
    Lesson("Клавиатура с кнопками", "Обычная клавиатура заменяет поле ввода кнопками.\n>from telebot import types\n>kb = types.ReplyKeyboardMarkup(resize_keyboard=True)\n>kb.add(\"Да\", \"Нет\")\n>bot.send_message(message.chat.id, \"Выбери\", reply_markup=kb)\nКогда человек нажмёт кнопку, бот получит обычное сообщение с текстом кнопки.", listOf(
        choice("Что получит бот, когда человек нажмёт кнопку обычной клавиатуры?", "Обычное сообщение с текстом кнопки", "Сигнал callback", "Ничего", explain = "Для сигналов без сообщения нужны inline-кнопки."),
        choice("Что делает resize_keyboard=True?", "Делает кнопки компактными", "Скрывает клавиатуру", "Удаляет бота"),
        gap("Дополни: создай клавиатуру", "kb = types.___(resize_keyboard=True)", "ReplyKeyboardMarkup", "InlineKeyboardMarkup|ReplyMarkup"),
        gap("Дополни: добавь две кнопки", "kb.___(\"Да\", \"Нет\")", "add", "send|push"),
        fix("Исправь: для клавиатуры нужен импорт types из telebot", "import types from telebot", "from telebot import types", explain = "Правильный порядок: from модуль import имя."),
        build("Импортируй types из telebot", "from|telebot|import|types", "import|aiogram"),
        build("Создай клавиатуру", "kb|=|types|.|ReplyKeyboardMarkup|(|resize_keyboard|=|True|)", "InlineKeyboardMarkup|False"),
        type("Добавь на клавиатуру кнопки Да и Нет", "kb.add(\"Да\", \"Нет\")", "kb.add('Да', 'Нет')"),
        build("Отправь сообщение с клавиатурой", "bot|.|send_message|(|message|.|chat|.|id|,|\"Выбери\"|,|reply_markup|=|kb|)", "keyboard|=|kb"),
        build("Убери клавиатуру", "types|.|ReplyKeyboardRemove|(|)", "ReplyKeyboardMarkup|Delete")
    )),
    Lesson("Inline-кнопки", "Inline-кнопки показываются под сообщением. Каждая кнопка несёт данные callback_data.\n>kb = types.InlineKeyboardMarkup()\n>kb.add(types.InlineKeyboardButton(\"Нажми\", callback_data=\"btn1\"))\n>bot.send_message(message.chat.id, \"Кнопка:\", reply_markup=kb)\nНажатие приходит не как сообщение, а как callback — его мы разберём в следующем уроке.", listOf(
        choice("Где показываются inline-кнопки?", "Под сообщением", "Вместо клавиатуры", "В настройках бота", explain = "Это кнопки прямо под текстом сообщения."),
        choice("Что несёт callback_data?", "Данные, по которым бот узнает, какая кнопка нажата", "Текст сообщения", "Токен бота"),
        gap("Дополни: кнопка с данными", "types.InlineKeyboardButton(\"Нажми\", ___=\"btn1\")", "callback_data", "text|data"),
        fix("Исправь: класс называется InlineKeyboardMarkup", "kb = types.InlineKeyboard()", "kb = types.InlineKeyboardMarkup()", explain = "Полное имя класса важно, иначе будет AttributeError."),
        fix("Исправь: сообщение отправляется с reply_markup", "bot.send_message(message.chat.id, \"Кнопка:\", markup=kb)", "bot.send_message(message.chat.id, \"Кнопка:\", reply_markup=kb)"),
        build("Создай inline-клавиатуру", "kb|=|types|.|InlineKeyboardMarkup|(|)", "ReplyKeyboardMarkup|Button"),
        build("Создай кнопку «Нажми»", "types|.|InlineKeyboardButton|(|\"Нажми\"|,|callback_data|=|\"btn1\"|)", "text|data"),
        type("Добавь кнопку btn на клавиатуру kb", "kb.add(btn)"),
        build("Кнопка-ссылка", "types|.|InlineKeyboardButton|(|\"Сайт\"|,|url|=|\"https://example.com\"|)", "callback_data|link"),
        type("Две кнопки в одном ряду", "kb.row(btn1, btn2)", explain = "row кладёт кнопки в одну строку, add — по умолчанию по одной в строке.")
    )),
    Lesson("Тип сообщения и ответы", "Бот может получить не только текст: фото, документ, голос. Обработчик выбирают по content_types.\n>@bot.message_handler(content_types=[\"photo\"])\n>def photo(message):\n>    bot.reply_to(message, \"Красивое фото\")\nОтветить можно разными способами: reply_to отвечает цитатой, send_message просто пишет в чат.", listOf(
        choice("Чем выбирают обработчик для фото?", "content_types=[\"photo\"]", "commands=[\"photo\"]", "func=photo", explain = "content_types задаёт, какой тип содержимого ждёт обработчик."),
        choice("Чем reply_to отличается от send_message?", "reply_to отвечает на конкретное сообщение", "reply_to работает только с фото", "Ничем"),
        gap("Дополни: обработчик документов", "@bot.message_handler(content_types=[\"___\"])", "document", "file|doc2"),
        gap("Дополни: обработчик голосовых", "@bot.message_handler(content_types=[\"___\"])", "voice", "audio2|mic"),
        fix("Исправь: список content_types нужен в квадратных скобках", "@bot.message_handler(content_types=\"photo\")", "@bot.message_handler(content_types=[\"photo\"])", explain = "Это список, даже если тип один."),
        build("Обработчик фотографий", "@bot|.|message_handler|(|content_types|=|[|\"photo\"|]|)", "commands|=|text"),
        build("Обработчик документов", "content_types|=|[|\"document\"|]", "commands|=|[|photo"),
        type("Ответь на фото словом Круто", "bot.reply_to(message, \"Круто\")"),
        build("Отправь текст в чат без цитаты", "bot|.|send_message", "reply_to|answer"),
        type("Получи имя пользователя в переменную name", "name = message.from_user.first_name")
    ))
)

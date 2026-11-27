// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons4(): List<Lesson> = listOf(
    Lesson("aiogram 3: что нового", "aiogram — асинхронная библиотека для ботов. Версия 3 работает на asyncio: функции пишутся с async def, а вызовы ждут через await.\nВ приложении aiogram 3 запускается во вкладке Linux: pip install aiogram, затем python3 bot.py (токен из .env подхвати командой set -a; . ./.env; set +a).\nКоманда pip install aiogram ставит последнюю версию.", listOf(
        choice("На чём работает aiogram 3?", "На asyncio", "На потоках без async", "На Flask", explain = "Поэтому функции пишутся с async def и await."),
        choice("Где в приложении запускать aiogram 3?", "Во вкладке Linux", "Во встроенном Python", "В HTML-редакторе", explain = "Во встроенном Python стоит aiogram 2."),
        choice("Какое слово ставят перед async-вызовом, чтобы дождаться результата?", "await", "wait", "sync"),
        gap("Дополни: объяви асинхронную функцию", "___ def main():", "async", "await|sync"),
        fix("Исправь: функцию объявляют через async def", "await def main():", "async def main():"),
        build("Импортируй классы Bot и Dispatcher", "from|aiogram|import|Bot|,|Dispatcher", "import|telebot"),
        build("Поставь aiogram", "pip|install|aiogram", "apt|run"),
        type("Объяви асинхронную функцию main", "async def main():"),
        build("Дождись выполнения", "await|asyncio|.|sleep|(|1|)", "async|def"),
        type("Запусти main через asyncio", "asyncio.run(main())")
    )),
    Lesson("Первый бот на aiogram 3", "Минимальный бот:\n>import asyncio\n>from aiogram import Bot, Dispatcher, types\n>from aiogram.filters import Command\n>bot = Bot(token=TOKEN)\n>dp = Dispatcher()\n>@dp.message(Command(\"start\"))\n>async def start(message: types.Message):\n>    await message.answer(\"Привет!\")\n>async def main():\n>    await dp.start_polling(bot)\n>asyncio.run(main())\nDispatcher распределяет сообщения по обработчикам, а Bot хранит токен.", listOf(
        choice("Что хранит Bot в aiogram 3?", "Токен бота", "Список обработчиков", "Лог", explain = "Bot общается с Telegram, Dispatcher распределяет сообщения."),
        choice("Что делает Dispatcher?", "Распределяет сообщения по обработчикам", "Хранит токен", "Рисует кнопки"),
        gap("Дополни: запусти бота", "await dp.___(bot)", "start_polling", "run|infinity_polling", explain = "В aiogram 3 это start_polling."),
        gap("Дополни: запусти главную функцию", "asyncio.___(main())", "run", "start|await"),
        fix("Исправь: у aiogram 3 команда задаётся фильтром Command", "@dp.message(commands=[\"start\"])", "@dp.message(Command(\"start\"))", explain = "Фильтры в aiogram 3 передают объектами."),
        build("Создай бота с токеном", "bot|=|Bot|(|token|=|TOKEN|)", "Bot|TeleBot"),
        type("Создай диспетчер dp", "dp = Dispatcher()"),
        build("Обработчик команды /start", "@dp|.|message|(|Command|(|\"start\"|)|)", "message_handler|commands"),
        build("Ответь на сообщение", "await|message|.|answer|(|\"Привет!\"|)", "reply_to|bot"),
        type("Запусти polling для бота bot", "await dp.start_polling(bot)")
    )),
    Lesson("Обработчики сообщений", "Обработчик без фильтров ловит любые сообщения. Тип параметра — types.Message.\n>@dp.message()\n>async def echo(message: types.Message):\n>    await message.answer(message.text)\nЧтобы ответить цитатой, используй message.reply, а чтобы написать в чат — bot.send_message(chat_id, текст).", listOf(
        choice("Какого типа параметр у обработчика сообщений?", "types.Message", "str", "int", explain = "Из message берут text, chat, from_user и другое."),
        choice("Чем отвечают на сообщение в aiogram 3?", "await message.answer(...)", "bot.reply_to(...)", "print(...)"),
        gap("Дополни: ответь эхом", "await message.___(message.text)", "answer", "send|reply_to"),
        fix("Исправь: перед answer нужен await", "message.answer(message.text)", "await message.answer(message.text)", explain = "Без await ответ не отправится."),
        fix("Исправь: обработчик должен быть async", "def echo(message: types.Message):", "async def echo(message: types.Message):"),
        build("Обработчик любого сообщения", "@dp|.|message|(|)", "message_handler|commands"),
        build("Объяви обработчик echo", "async|def|echo|(|message|:|types|.|Message|)|:", "def|lambda"),
        type("Ответь тем же текстом", "await message.answer(message.text)"),
        build("Ответь цитатой", "await|message|.|reply|(|\"Ок\"|)", "answer|send"),
        type("Отправь сообщение в чат chat_id через бота", "await bot.send_message(chat_id, \"Привет\")")
    )),
    Lesson("Фильтры", "Фильтры решают, какой обработчик получит сообщение.\n>from aiogram import F\n>@dp.message(F.text == \"привет\")\n>async def hi(message: types.Message):\n>    await message.answer(\"И тебе привет\")\nF.text — текст сообщения, F.photo — фото. Фильтры можно комбинировать через & (и) и | (или).", listOf(
        choice("Что означает F.text == \"привет\"?", "Сообщение с текстом привет", "Любое фото", "Команда привет", explain = "F это магический фильтр по полям сообщения."),
        choice("Какой импорт нужен для F?", "from aiogram import F", "import F", "from filters import F"),
        gap("Дополни: фильтр на фото", "@dp.message(F.___)", "photo", "text|voice"),
        gap("Дополни: фильтры через И", "@dp.message(F.text ___ F.chat.type == \"private\")", "&", "and|+", explain = "& значит «и», | значит «или»."),
        fix("Исправь: в фильтре сравнивают через ==", "@dp.message(F.text = \"привет\")", "@dp.message(F.text == \"привет\")"),
        build("Импортируй магический фильтр F", "from|aiogram|import|F", "import|Command"),
        build("Обработчик текста «привет»", "@dp|.|message|(|F|.|text|==|\"привет\"|)", "commands|=|["),
        build("Обработчик фотографий", "@dp|.|message|(|F|.|photo|)", "F|.|text"),
        type("Обработчик команды help", "@dp.message(Command(\"help\"))"),
        build("Условие «и»", "F|.|text|&|F|.|from_user", "and|or")
    )),
    Lesson("Асинхронность без боли", "Главное правило: перед вызовом async-функции пиши await, иначе она не выполнится.\n>await message.answer(\"Привет\")\nНе используй time.sleep, используй await asyncio.sleep(секунды): он не блокирует других пользователей.\nЕсли забыл await, в логе будет предупреждение coroutine was never awaited.", listOf(
        choice("Что делать вместо time.sleep(5)?", "await asyncio.sleep(5)", "sleep(5)", "wait(5)", explain = "time.sleep блокирует всех, asyncio.sleep нет."),
        choice("Что будет без await перед async-вызовом?", "Вызов не выполнится", "Всё будет быстрее", "Ничего не изменится", explain = "Python лишь создаст объект и покажет предупреждение never awaited."),
        gap("Дополни: пауза в async-коде", "await asyncio.___(2)", "sleep", "wait|pause"),
        fix("Исправь: используй асинхронную паузу", "time.sleep(3)", "await asyncio.sleep(3)", explain = "Так бот продолжит отвечать другим людям."),
        fix("Исправь: забыт await", "message.answer(\"Привет\")", "await message.answer(\"Привет\")"),
        build("Подожди 2 секунды правильно", "await|asyncio|.|sleep|(|2|)", "time|.|sleep"),
        build("Объяви асинхронный обработчик", "async|def|handler|(|message|)|:", "def|handler"),
        type("Подожди секунду без блокировки", "await asyncio.sleep(1)"),
        build("Импортируй asyncio", "import|asyncio", "from|time"),
        type("Запусти главную функцию", "asyncio.run(main())")
    ))
)

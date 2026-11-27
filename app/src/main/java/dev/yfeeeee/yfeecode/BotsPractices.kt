// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Практика курса «Боты и Heroku» (проверка по шаблонам кода). */
fun botsPractices(): List<Practice> = listOf(
    Practice("bt1", "Первый бот", "Создай бота на telebot: импорт, TeleBot(TOKEN), обработчик /start с reply_to и infinity_polling.",
        "import telebot\n\nTOKEN = open(\"token.txt\").read().strip()\n", Lang.PYTHON,
        listOf(
            must("""telebot\.TeleBot\s*\(""", "Создай бота: telebot.TeleBot(TOKEN)"),
            must("""message_handler\s*\(\s*commands\s*=""", "Добавь обработчик @bot.message_handler(commands=[\"start\"])"),
            must("""reply_to\s*\(""", "Ответь пользователю через bot.reply_to(...)"),
            must("""infinity_polling\s*\(""", "Запусти бота: bot.infinity_polling()")
        )),
    Practice("bt2", "Токен из окружения", "Возьми токен из переменной окружения BOT_TOKEN, а не из файла, и создай бота.",
        "import os\nimport telebot\n\n", Lang.PYTHON,
        listOf(
            must("""os\.(environ|getenv)""", "Читай токен через os.environ[\"BOT_TOKEN\"] или os.getenv"),
            must("""BOT_TOKEN""", "Имя переменной: BOT_TOKEN"),
            must("""TeleBot\s*\(""", "Создай бота: telebot.TeleBot(...)")
        )),
    Practice("bt3", "Эхо-бот", "Бот отвечает любому тексту тем же текстом. Используй обработчик без команд.",
        "import telebot\n\nbot = telebot.TeleBot(\"TOKEN\")\n\n", Lang.PYTHON,
        listOf(
            must("""message_handler\s*\(""", "Нужен @bot.message_handler(...)"),
            must("""message\.text""", "Используй message.text"),
            must("""reply_to\s*\(|send_message\s*\(""", "Ответь через reply_to или send_message")
        )),
    Practice("bt4", "Клавиатура", "Отправь сообщение с обычной клавиатурой из двух кнопок Да и Нет.",
        "import telebot\nfrom telebot import types\n\nbot = telebot.TeleBot(\"TOKEN\")\n\n", Lang.PYTHON,
        listOf(
            must("""ReplyKeyboardMarkup\s*\(""", "Создай types.ReplyKeyboardMarkup(...)"),
            must("""\.add\s*\(""", "Добавь кнопки через kb.add(...)"),
            must("""reply_markup\s*=""", "Передай клавиатуру в send_message(reply_markup=kb)")
        )),
    Practice("bt5", "Inline-кнопка", "Сделай inline-кнопку с callback_data и обработчик её нажатия.",
        "import telebot\nfrom telebot import types\n\nbot = telebot.TeleBot(\"TOKEN\")\n\n", Lang.PYTHON,
        listOf(
            must("""InlineKeyboardButton\s*\(""", "Нужна types.InlineKeyboardButton(...)"),
            must("""callback_data""", "Укажи callback_data"),
            must("""callback_query_handler\s*\(""", "Добавь @bot.callback_query_handler(...)"),
            must("""answer_callback_query\s*\(""", "Ответь на нажатие: bot.answer_callback_query(call.id)")
        )),
    Practice("bt6", "Диалог из двух шагов", "Спроси имя и ответь «Привет, имя» в следующем сообщении через register_next_step_handler.",
        "import telebot\n\nbot = telebot.TeleBot(\"TOKEN\")\n\n", Lang.PYTHON,
        listOf(
            must("""register_next_step_handler\s*\(""", "Используй bot.register_next_step_handler(msg, функция)"),
            must("""def\s+\w+\s*\(\s*message\s*\)""", "Нужны функции с параметром message"),
            must("""message\.text""", "Возьми имя из message.text")
        )),
    Practice("bt7", "Сохранение в JSON", "Запиши словарь в файл data.json с русскими буквами и прочитай его обратно.",
        "import json\n\ndata = {\"имя\": \"Аня\"}\n", Lang.PYTHON,
        listOf(
            must("""json\.dump\s*\(""", "Запиши через json.dump(...)"),
            must("""ensure_ascii\s*=\s*False""", "Добавь ensure_ascii=False"),
            must("""json\.load\s*\(""", "Прочитай через json.load(...)")
        )),
    Practice("bt8", "SQLite", "Создай базу bot.db, таблицу users и добавь пользователя через безопасный запрос с ?.",
        "import sqlite3\n\n", Lang.PYTHON,
        listOf(
            must("""sqlite3\.connect\s*\(""", "Подключись: sqlite3.connect(\"bot.db\")"),
            must("""CREATE\s+TABLE""", "Создай таблицу: CREATE TABLE IF NOT EXISTS users (...)"),
            must("""INSERT\s+INTO""", "Добавь запись: INSERT INTO users ..."),
            must("""\?""", "Подставь значение безопасно через ?"),
            must("""commit\s*\(""", "Сохрани изменения: db.commit()")
        )),
    Practice("bt9", "Бот на aiogram 3", "Бот на aiogram 3 с обработчиком /start и запуском polling (запускать нужно во вкладке Linux).",
        "import asyncio\nfrom aiogram import Bot, Dispatcher, types\nfrom aiogram.filters import Command\n\n", Lang.PYTHON,
        listOf(
            must("""Dispatcher\s*\(""", "Создай dp = Dispatcher()"),
            must("""dp\.message\s*\(\s*Command""", "Добавь @dp.message(Command(\"start\"))"),
            must("""async\s+def""", "Обработчик должен быть async def"),
            must("""await\s+message\.answer""", "Ответь: await message.answer(...)"),
            must("""start_polling\s*\(""", "Запусти: await dp.start_polling(bot)"),
            must("""asyncio\.run\s*\(""", "Запусти main через asyncio.run(main())")
        )),
    Practice("bt10", "Состояния FSM", "Создай группу состояний Form с состоянием name и поставь его командой.",
        "from aiogram.fsm.state import State, StatesGroup\nfrom aiogram.fsm.context import FSMContext\n\n", Lang.PYTHON,
        listOf(
            must("""class\s+Form\s*\(\s*StatesGroup\s*\)""", "Объяви class Form(StatesGroup):"),
            must("""name\s*=\s*State\s*\(\s*\)""", "Добавь name = State()"),
            must("""set_state\s*\(""", "Поставь состояние: await state.set_state(Form.name)")
        )),
    Practice("bt11", "Модуль Hikka: команда", "Напиши модуль с классом на loader.Module и командой hello, которая отвечает через utils.answer.",
        "from .. import loader, utils\n\n", Lang.PYTHON,
        listOf(
            must("""@loader\.tds""", "Добавь декоратор @loader.tds"),
            must("""class\s+\w+\s*\(\s*loader\.Module\s*\)""", "Класс должен наследовать loader.Module"),
            must("""@loader\.command\s*\(""", "Добавь @loader.command()"),
            must("""async\s+def\s+\w+cmd\s*\(\s*self\s*,\s*message""", "Имя команды заканчивается на cmd: async def hellocmd(self, message)"),
            must("""utils\.answer\s*\(""", "Ответь через await utils.answer(message, ...)")
        )),
    Practice("bt12", "Модуль Hikka: конфиг и счётчик", "В модуле добавь настройку через ModuleConfig и счётчик, который хранится в self.set и self.get.",
        "from .. import loader, utils\n\n", Lang.PYTHON,
        listOf(
            must("""ModuleConfig\s*\(""", "Добавь self.config = loader.ModuleConfig(...)"),
            must("""ConfigValue\s*\(""", "Добавь loader.ConfigValue(...)"),
            must("""self\.set\s*\(""", "Сохраняй значение: self.set(\"count\", n)"),
            must("""self\.get\s*\(""", "Читай значение: self.get(\"count\", 0)")
        )),
    Practice("bt13", "Файлы для Heroku", "Напиши содержимое Procfile для бота bot.py на polling одной строкой.",
        "# Procfile\n", Lang.TEXT,
        listOf(
            must("""worker\s*:\s*python3?\s+bot\.py""", "Формат: worker: python3 bot.py")
        ))
)

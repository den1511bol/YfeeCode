// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons5(): List<Lesson> = listOf(
    Lesson("Inline-кнопки в aiogram 3", "Клавиатуру собирают из кнопок и списка рядов.\n>from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup\n>kb = InlineKeyboardMarkup(inline_keyboard=[\n>    [InlineKeyboardButton(text=\"Нажми\", callback_data=\"btn1\")]\n>])\n>await message.answer(\"Выбери\", reply_markup=kb)\nКаждый вложенный список — это один ряд кнопок.", listOf(
        build("Импортируй типы клавиатуры", "from|aiogram|.|types|import|InlineKeyboardButton|,|InlineKeyboardMarkup", "import|telebot"),
        build("Создай кнопку «Нажми»", "InlineKeyboardButton|(|text|=|\"Нажми\"|,|callback_data|=|\"btn1\"|)", "types|.|Button"),
        type("Создай клавиатуру kb из ряда с кнопкой btn", "kb = InlineKeyboardMarkup(inline_keyboard=[[btn]])"),
        build("Отправь сообщение с клавиатурой", "await|message|.|answer|(|\"Выбери\"|,|reply_markup|=|kb|)", "keyboard|=|kb"),
        build("Кнопка со ссылкой", "InlineKeyboardButton|(|text|=|\"Сайт\"|,|url|=|\"https://example.com\"|)", "callback_data|link")
    )),
    Lesson("Нажатия кнопок", "Нажатие приходит как callback_query.\n>@dp.callback_query(F.data == \"btn1\")\n>async def on_btn(call: types.CallbackQuery):\n>    await call.message.answer(\"Нажато\")\n>    await call.answer()\ncall.answer() убирает «часики» на кнопке. call.message — сообщение, под которым была кнопка.", listOf(
        build("Обработчик нажатия btn1", "@dp|.|callback_query|(|F|.|data|==|\"btn1\"|)", "message|(|F|.|text"),
        build("Объяви обработчик", "async|def|on_btn|(|call|:|types|.|CallbackQuery|)|:", "def|on_btn"),
        type("Убери часики у кнопки", "await call.answer()"),
        type("Ответь в чат под кнопкой", "await call.message.answer(\"Нажато\")"),
        build("Измени текст сообщения с кнопкой", "await|call|.|message|.|edit_text|(|\"Готово\"|)", "answer|delete")
    )),
    Lesson("Конструктор клавиатур", "Для длинных клавиатур есть InlineKeyboardBuilder.\n>from aiogram.utils.keyboard import InlineKeyboardBuilder\n>b = InlineKeyboardBuilder()\n>b.button(text=\"Один\", callback_data=\"1\")\n>b.button(text=\"Два\", callback_data=\"2\")\n>b.adjust(2)\n>await message.answer(\"Выбери\", reply_markup=b.as_markup())\nadjust(2) — по две кнопки в ряду.", listOf(
        build("Импортируй конструктор", "from|aiogram|.|utils|.|keyboard|import|InlineKeyboardBuilder", "import|types"),
        type("Создай конструктор b", "b = InlineKeyboardBuilder()"),
        build("Добавь кнопку", "b|.|button|(|text|=|\"Один\"|,|callback_data|=|\"1\"|)", "add|row"),
        type("Расставь по две кнопки в ряд", "b.adjust(2)"),
        build("Получи готовую клавиатуру", "b|.|as_markup|(|)", "build|get")
    )),
    Lesson("FSM: состояния", "FSM помнит, на каком шаге диалога находится человек.\n>from aiogram.fsm.state import State, StatesGroup\n>class Form(StatesGroup):\n>    name = State()\n>    age = State()\nКаждое состояние — шаг. Группа состояний — класс, наследник StatesGroup.", listOf(
        build("Импортируй State и StatesGroup", "from|aiogram|.|fsm|.|state|import|State|,|StatesGroup", "import|FSM"),
        build("Объяви группу состояний", "class|Form|(|StatesGroup|)|:", "def|Form"),
        type("Создай состояние name", "name = State()"),
        build("Импортируй контекст FSM", "from|aiogram|.|fsm|.|context|import|FSMContext", "import|state"),
        type("Создай состояние age", "age = State()")
    )),
    Lesson("FSM: диалог", "Бот ставит состояние и ловит ответ именно в нём.\n>@dp.message(Command(\"form\"))\n>async def start(message: types.Message, state: FSMContext):\n>    await state.set_state(Form.name)\n>    await message.answer(\"Как зовут?\")\n>@dp.message(Form.name)\n>async def got(message: types.Message, state: FSMContext):\n>    await state.update_data(name=message.text)\n>    data = await state.get_data()\n>    await state.clear()\nstate.clear() завершает диалог.", listOf(
        build("Поставь состояние Form.name", "await|state|.|set_state|(|Form|.|name|)", "get_state|clear"),
        build("Поймай ответ в состоянии name", "@dp|.|message|(|Form|.|name|)", "Command|(|\"form\"|)"),
        type("Запомни имя из сообщения", "await state.update_data(name=message.text)"),
        type("Получи все данные в переменную data", "data = await state.get_data()"),
        build("Закончи диалог", "await|state|.|clear|(|)", "set_state|finish")
    ))
)

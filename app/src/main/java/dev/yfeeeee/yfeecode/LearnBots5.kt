// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun botsLessons5(): List<Lesson> = listOf(
    Lesson("Inline-кнопки в aiogram 3", "Клавиатуру собирают из кнопок и списка рядов.\n>from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup\n>kb = InlineKeyboardMarkup(inline_keyboard=[\n>    [InlineKeyboardButton(text=\"Нажми\", callback_data=\"btn1\")]\n>])\n>await message.answer(\"Выбери\", reply_markup=kb)\nКаждый вложенный список — это один ряд кнопок.", listOf(
        choice("Как в aiogram 3 описывают клавиатуру?", "Списком рядов кнопок", "Строкой с кнопками через запятую", "Файлом keyboard.txt", explain = "inline_keyboard это список рядов, каждый ряд тоже список."),
        choice("Что делает InlineKeyboardButton?", "Создаёт одну кнопку", "Отправляет сообщение", "Удаляет клавиатуру"),
        gap("Дополни: текст кнопки", "InlineKeyboardButton(___=\"Нажми\", callback_data=\"btn1\")", "text", "label|name", explain = "В aiogram 3 параметры кнопки только именованные: text и callback_data."),
        fix("Исправь: клавиатуру отправляют через reply_markup", "await message.answer(\"Выбери\", markup=kb)", "await message.answer(\"Выбери\", reply_markup=kb)"),
        fix("Исправь: перед answer нужен await", "message.answer(\"Выбери\", reply_markup=kb)", "await message.answer(\"Выбери\", reply_markup=kb)"),
        build("Импортируй типы клавиатуры", "from|aiogram|.|types|import|InlineKeyboardButton|,|InlineKeyboardMarkup", "import|telebot"),
        build("Создай кнопку «Нажми»", "InlineKeyboardButton|(|text|=|\"Нажми\"|,|callback_data|=|\"btn1\"|)", "types|.|Button"),
        type("Создай клавиатуру kb из ряда с кнопкой btn", "kb = InlineKeyboardMarkup(inline_keyboard=[[btn]])"),
        build("Отправь сообщение с клавиатурой", "await|message|.|answer|(|\"Выбери\"|,|reply_markup|=|kb|)", "keyboard|=|kb"),
        build("Кнопка со ссылкой", "InlineKeyboardButton|(|text|=|\"Сайт\"|,|url|=|\"https://example.com\"|)", "callback_data|link")
    )),
    Lesson("Нажатия кнопок", "Нажатие приходит как callback_query.\n>@dp.callback_query(F.data == \"btn1\")\n>async def on_btn(call: types.CallbackQuery):\n>    await call.message.answer(\"Нажато\")\n>    await call.answer()\ncall.answer() убирает «часики» на кнопке. call.message — сообщение, под которым была кнопка.", listOf(
        choice("Как приходит нажатие кнопки в aiogram 3?", "Как callback_query", "Как обычное сообщение", "Как фото", explain = "Для него нужен декоратор @dp.callback_query."),
        choice("Что убирает часики на кнопке?", "await call.answer()", "await call.delete()", "print(call)"),
        gap("Дополни: фильтр по данным кнопки", "@dp.callback_query(F.___ == \"btn1\")", "data", "text|id"),
        gap("Дополни: тип параметра", "async def on_btn(call: types.___):", "CallbackQuery", "Message|Update"),
        fix("Исправь: данные кнопки лежат в F.data", "@dp.callback_query(F.text == \"btn1\")", "@dp.callback_query(F.data == \"btn1\")", explain = "У нажатия нет текста, зато есть data."),
        build("Обработчик нажатия btn1", "@dp|.|callback_query|(|F|.|data|==|\"btn1\"|)", "message|(|F|.|text"),
        build("Объяви обработчик", "async|def|on_btn|(|call|:|types|.|CallbackQuery|)|:", "def|on_btn"),
        type("Убери часики у кнопки", "await call.answer()"),
        type("Ответь в чат под кнопкой", "await call.message.answer(\"Нажато\")"),
        build("Измени текст сообщения с кнопкой", "await|call|.|message|.|edit_text|(|\"Готово\"|)", "answer|delete")
    )),
    Lesson("Конструктор клавиатур", "Для длинных клавиатур есть InlineKeyboardBuilder.\n>from aiogram.utils.keyboard import InlineKeyboardBuilder\n>b = InlineKeyboardBuilder()\n>b.button(text=\"Один\", callback_data=\"1\")\n>b.button(text=\"Два\", callback_data=\"2\")\n>b.adjust(2)\n>await message.answer(\"Выбери\", reply_markup=b.as_markup())\nadjust(2) — по две кнопки в ряду.", listOf(
        choice("Зачем нужен InlineKeyboardBuilder?", "Собирать длинные клавиатуры проще", "Отправлять фото", "Хранить токен", explain = "Кнопки добавляют по одной, а adjust раскладывает их по рядам."),
        choice("Что делает adjust(2)?", "Ставит по две кнопки в ряд", "Удаляет две кнопки", "Добавляет 2 секунды"),
        gap("Дополни: добавь кнопку", "b.___(text=\"Один\", callback_data=\"1\")", "button", "add|push"),
        gap("Дополни: готовая клавиатура", "reply_markup=b.___()", "as_markup", "build|get", explain = "as_markup превращает билдер в готовую клавиатуру."),
        fix("Исправь: перед использованием нужно вызвать as_markup()", "await message.answer(\"Выбери\", reply_markup=b)", "await message.answer(\"Выбери\", reply_markup=b.as_markup())"),
        build("Импортируй конструктор", "from|aiogram|.|utils|.|keyboard|import|InlineKeyboardBuilder", "import|types"),
        type("Создай конструктор b", "b = InlineKeyboardBuilder()"),
        build("Добавь кнопку", "b|.|button|(|text|=|\"Один\"|,|callback_data|=|\"1\"|)", "add|row"),
        type("Расставь по две кнопки в ряд", "b.adjust(2)"),
        build("Получи готовую клавиатуру", "b|.|as_markup|(|)", "build|get")
    )),
    Lesson("FSM: состояния", "FSM помнит, на каком шаге диалога находится человек.\n>from aiogram.fsm.state import State, StatesGroup\n>class Form(StatesGroup):\n>    name = State()\n>    age = State()\nКаждое состояние — шаг. Группа состояний — класс, наследник StatesGroup.", listOf(
        choice("Что запоминает FSM?", "На каком шаге диалога человек", "Токен бота", "Список файлов", explain = "FSM это машина состояний."),
        choice("От какого класса наследуют группу состояний?", "StatesGroup", "State", "FSMContext"),
        gap("Дополни: объяви шаг диалога", "class Form(StatesGroup):\n    name = ___()", "State", "Group|Step", explain = "Каждый шаг это State()."),
        fix("Исправь: группа состояний наследуется от StatesGroup", "class Form(State):", "class Form(StatesGroup):"),
        fix("Исправь: нужны скобки у State", "name = State", "name = State()"),
        build("Импортируй State и StatesGroup", "from|aiogram|.|fsm|.|state|import|State|,|StatesGroup", "import|FSM"),
        build("Объяви группу состояний", "class|Form|(|StatesGroup|)|:", "def|Form"),
        type("Создай состояние name", "name = State()"),
        build("Импортируй контекст FSM", "from|aiogram|.|fsm|.|context|import|FSMContext", "import|state"),
        type("Создай состояние age", "age = State()")
    )),
    Lesson("FSM: диалог", "Бот ставит состояние и ловит ответ именно в нём.\n>@dp.message(Command(\"form\"))\n>async def start(message: types.Message, state: FSMContext):\n>    await state.set_state(Form.name)\n>    await message.answer(\"Как зовут?\")\n>@dp.message(Form.name)\n>async def got(message: types.Message, state: FSMContext):\n>    await state.update_data(name=message.text)\n>    data = await state.get_data()\n>    await state.clear()\nstate.clear() завершает диалог.", listOf(
        choice("Как включить шаг диалога?", "await state.set_state(Form.name)", "state = Form.name", "bot.set(Form)", explain = "set_state запоминает, что человек теперь на этом шаге."),
        choice("Как ловят ответ именно на шаге Form.name?", "@dp.message(Form.name)", "@dp.message(name)", "@dp.state(name)"),
        gap("Дополни: сохрани ответ", "await state.___(name=message.text)", "update_data", "save|put", explain = "update_data кладёт значения в память диалога."),
        gap("Дополни: закончи диалог", "await state.___()", "clear", "stop|end"),
        fix("Исправь: set_state нужно ждать через await", "state.set_state(Form.name)", "await state.set_state(Form.name)"),
        build("Поставь состояние Form.name", "await|state|.|set_state|(|Form|.|name|)", "get_state|clear"),
        build("Поймай ответ в состоянии name", "@dp|.|message|(|Form|.|name|)", "Command|(|\"form\"|)"),
        type("Запомни имя из сообщения", "await state.update_data(name=message.text)"),
        type("Получи все данные в переменную data", "data = await state.get_data()"),
        build("Закончи диалог", "await|state|.|clear|(|)", "set_state|finish")
    ))
)

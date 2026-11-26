// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun cssCourse() = Course("css", "CSS", "🎨", listOf(
    Lesson("Правило CSS", "CSS состоит из селектора и блока свойств. Каждое свойство заканчивается точкой с запятой.\n>p {\n>  color: red;\n>}", listOf(
        build("Покрась текст p в красный", "p|{|color|:|red|;|}", "=|("),
        type("Цвет текста blue", "color: blue;"),
        build("Размер шрифта 16px", "font-size|:|16px|;", "=|size"),
        type("Фон black", "background: black;"),
        build("Начни правило для h1", "h1|{", "<|("),
        type("Закрой блок правила", "}")
    )),
    Lesson("Селекторы", "Тег пишут как есть, класс — с точкой, id — с решёткой.\n>p { }\n>.box { }\n>#main { }", listOf(
        build("Селектор класса box", ".|box|{", "#|box"),
        build("Селектор id main", "#|main|{", ".|main"),
        type("Селектор тега div", "div {"),
        type("Селектор класса menu", ".menu {"),
        build("Для всех тегов", "*|{", "#|."),
        type("Селектор id header", "#header {")
    )),
    Lesson("Отступы и рамки", "margin — внешний отступ, padding — внутренний, border — рамка.\n>margin: 10px;\n>padding: 5px;\n>border: 1px solid white;", listOf(
        build("Внешний отступ 10px", "margin|:|10px|;", "padding|="),
        build("Внутренний отступ 5px", "padding|:|5px|;", "margin|="),
        type("Рамка 1px solid white", "border: 1px solid white;"),
        type("Скругление 8px", "border-radius: 8px;"),
        build("Ширина 100%", "width|:|100%|;", "height|="),
        type("Высота 50px", "height: 50px;")
    )),
    Lesson("Текст и цвета", "Свойства текста: color, font-size, font-weight, text-align.\n>text-align: center;\n>font-weight: bold;", listOf(
        build("Текст по центру", "text-align|:|center|;", "align|="),
        type("Жирный шрифт", "font-weight: bold;"),
        build("Цвет в hex", "color|:|#ff00aa|;", "=|("),
        type("Шрифт monospace", "font-family: monospace;"),
        build("Подчёркивание убрать", "text-decoration|:|none|;", "underline|="),
        type("Размер шрифта 20px", "font-size: 20px;")
    )),
    Lesson("Flex-раскладка", "display: flex выстраивает элементы в ряд. justify-content и align-items управляют выравниванием.\n>display: flex;\n>justify-content: center;\n>align-items: center;", listOf(
        build("Включи flex", "display|:|flex|;", "block|="),
        build("Центр по горизонтали", "justify-content|:|center|;", "align-items|="),
        type("Центр по вертикали", "align-items: center;"),
        type("Колонкой", "flex-direction: column;"),
        build("Расстояние 10px между элементами", "gap|:|10px|;", "margin|="),
        type("Пусть элементы переносятся", "flex-wrap: wrap;")
    )),
    Lesson("Grid и адаптивность", "Grid строит сетку, media-запрос меняет стили для узких экранов.\n>display: grid;\n>grid-template-columns: 1fr 1fr;\n>@media (max-width: 600px) {\n>  .box { display: block; }\n>}", listOf(
        build("Включи grid", "display|:|grid|;", "flex|="),
        type("Две равные колонки", "grid-template-columns: 1fr 1fr;"),
        build("Для экранов уже 600px", "@|media|(|max-width|:|600px|)|{", "min-width|media"),
        type("Расстояние между ячейками 8px", "gap: 8px;"),
        build("Абсолютное позиционирование", "position|:|absolute|;", "display|="),
        type("Скрой элемент", "display: none;")
    )),
    Lesson("Переходы и анимации", "transition плавно меняет свойство, а @keyframes описывает свою анимацию, которую запускает свойство animation.\n>.btn { transition: 0.3s; }\n>.btn:hover { transform: scale(1.1); }", listOf(
        build("Плавное изменение за 0.3 секунды", "transition|:|0.3s|;", "animation|color"),
        type("Увеличь элемент при наведении", ".btn:hover { transform: scale(1.1); }"),
        build("Объяви анимацию pulse", "@keyframes|pulse|{", "animation|from"),
        type("Запусти анимацию pulse на 2 секунды", "animation: pulse 2s infinite;"),
        build("Поверни элемент на 45 градусов", "transform|:|rotate|(|45deg|)|;", "scale|margin"),
        type("Измени прозрачность до половины", "opacity: 0.5;")
    )),
    Lesson("Переменные и media", "CSS-переменные задают значение один раз: объявляют в :root, используют через var(). @media применяет стили только на нужной ширине экрана.\n>:root { --main: #C084FC; }\n>a { color: var(--main); }\n>@media (max-width: 600px) { }", listOf(
        type("Объяви переменную --main", ":root { --main: #C084FC; }"),
        type("Используй переменную --main как цвет", "color: var(--main);"),
        build("Стили только для узких экранов", "@media|(|max-width|:|600px|)|{", "@keyframes|from"),
        type("Скрой элемент", "display: none;"),
        build("Сделай ширину не больше 100%", "max-width|:|100%|;", "color|margin"),
        type("Подключи шрифт по умолчанию без засечек", "font-family: sans-serif;")
    )),
    Lesson("Блочная модель и box-sizing", "У блока есть content, padding, border и margin. box-sizing: border-box включает padding и рамку в ширину, так считать проще всего.\n>* { box-sizing: border-box; }\n>.card { width: 200px; padding: 10px; border: 1px solid #ccc; margin: 8px; }", listOf(
        type("Включи border-box для всех", "* { box-sizing: border-box; }"),
        type("Внутренний отступ 10px", "padding: 10px;"),
        type("Внешний отступ 8px", "margin: 8px;"),
        type("Рамка 1px сплошная серая", "border: 1px solid #ccc;"),
        type("Ширина 200px", "width: 200px;"),
        type("Центрируй блок по горизонтали", "margin: 0 auto;")
    )),
    Lesson("Позиционирование", "position задаёт способ размещения: relative сдвигает от своего места, absolute относительно родителя с position, fixed прилипает к окну, sticky залипает при прокрутке. z-index управляет слоями.\n>.box { position: absolute; top: 10px; right: 10px; z-index: 5; }", listOf(
        type("Абсолютное позиционирование", "position: absolute;"),
        type("Родитель для absolute", "position: relative;"),
        type("Прилепи к окну", "position: fixed;"),
        type("Залипание при прокрутке", "position: sticky;"),
        type("Слой выше других", "z-index: 10;"),
        type("Отступ сверху 10px у позиционированного блока", "top: 10px;")
    )),
    Lesson("Псевдоклассы и псевдоэлементы", "Псевдокласс реагирует на состояние: :hover, :focus, :first-child, :nth-child(2). Псевдоэлемент создаёт часть элемента: ::before и ::after с полем content.\n>a:hover { color: red; }\n>li:first-child { font-weight: bold; }\n>p::after { content: \"!\"; }", listOf(
        type("Цвет ссылки при наведении красный", "a:hover { color: red; }"),
        type("Первый элемент списка жирный", "li:first-child { font-weight: bold; }"),
        type("Каждый второй элемент", "li:nth-child(2n)"),
        type("Добавь ! после абзаца", "p::after { content: \"!\"; }"),
        type("Поле в фокусе", "input:focus"),
        type("Последний элемент", "li:last-child")
    )),
    Lesson("Display, overflow, visibility", "display задаёт тип блока: block, inline, none, flex, grid. none убирает элемент совсем, visibility: hidden прячет, но место оставляет. overflow управляет содержимым, которое не влезло.\n>.a { display: none; }\n>.b { visibility: hidden; }\n>.c { overflow: auto; }", listOf(
        type("Убери элемент совсем", "display: none;"),
        type("Спрячь, но оставь место", "visibility: hidden;"),
        type("Прокрутка при переполнении", "overflow: auto;"),
        type("Обрежь лишнее", "overflow: hidden;"),
        type("Строчный блок", "display: inline-block;"),
        type("Курсор-рука", "cursor: pointer;")
    )),
    Lesson("Единицы и шрифты", "px фиксированный размер, rem зависит от размера шрифта страницы, em от родителя, % от родителя, vw и vh от окна. font-family задаёт шрифт, line-height высоту строки.\n>html { font-size: 16px; }\n>h1 { font-size: 2rem; }\n>body { font-family: Arial, sans-serif; line-height: 1.5; }", listOf(
        type("Размер шрифта 2rem", "font-size: 2rem;"),
        type("Шрифт Arial, запасной sans-serif", "font-family: Arial, sans-serif;"),
        type("Высота строки 1.5", "line-height: 1.5;"),
        type("Ширина на весь экран", "width: 100vw;"),
        type("Высота половины окна", "height: 50vh;"),
        type("Жирный шрифт", "font-weight: bold;")
    )),
    Lesson("Тени, градиенты, transform", "box-shadow даёт тень, linear-gradient плавный переход цветов, border-radius скругляет углы, transform поворачивает и масштабирует.\n>.card { box-shadow: 0 4px 10px rgba(0,0,0,0.3); border-radius: 12px; }\n>.bg { background: linear-gradient(to right, red, black); }\n>.r { transform: rotate(10deg); }", listOf(
        type("Тень со смещением вниз 4px и размытием 10px", "box-shadow: 0 4px 10px rgba(0,0,0,0.3);"),
        type("Скругли углы 12px", "border-radius: 12px;"),
        type("Градиент слева направо от красного к чёрному", "background: linear-gradient(to right, red, black);"),
        type("Поверни на 10 градусов", "transform: rotate(10deg);"),
        type("Увеличь в 1.2 раза", "transform: scale(1.2);"),
        type("Круг из квадрата", "border-radius: 50%;")
    )),
    Lesson("Каскад и специфичность", "Когда правила конфликтуют, побеждает более специфичный селектор: id сильнее класса, класс сильнее тега. При равенстве выигрывает правило ниже. !important ломает порядок, пользоваться им лучше редко.\n>p { color: blue; }\n>.t { color: green; }\n>#x { color: red; }", listOf(
        type("Селектор по id x", "#x"),
        type("Селектор по классу t", ".t"),
        type("Селектор тега p", "p"),
        type("Селектор потомка a внутри nav", "nav a"),
        type("Селектор прямого потомка li в ul", "ul > li"),
        type("Усиль правило", "color: red !important;")
    )),
    Lesson("Flex и Grid подробнее", "Во flex есть justify-content (по основной оси), align-items (по поперечной), flex-wrap (перенос), gap (промежутки). В grid задают колонки: grid-template-columns, и промежутки gap.\n>.row { display: flex; justify-content: space-between; align-items: center; gap: 10px; }\n>.grid { display: grid; grid-template-columns: repeat(3, 1fr); }", listOf(
        type("Разнеси элементы по краям", "justify-content: space-between;"),
        type("Выровняй по центру по вертикали", "align-items: center;"),
        type("Разреши перенос", "flex-wrap: wrap;"),
        type("Промежуток 10px", "gap: 10px;"),
        type("Три равные колонки", "grid-template-columns: repeat(3, 1fr);"),
        type("Элемент занимает свободное место", "flex: 1;")
    )),
    Lesson("Grid areas и clamp", "grid-template-areas описывает раскладку словами: каждой области даёшь имя, а элементу задаёшь grid-area. Функция clamp(мин, желаемое, макс) делает размер гибким в заданных пределах, например для шрифта.\n>.page { display: grid; grid-template-areas: \"head head\" \"menu main\"; }\n>.head { grid-area: head; }\n>h1 { font-size: clamp(20px, 5vw, 40px); }", listOf(
        type("Задай области сетки", "grid-template-areas: \"head head\" \"menu main\";"),
        type("Помести элемент в область head", "grid-area: head;"),
        type("Шрифт от 20px до 40px, гибкий", "font-size: clamp(20px, 5vw, 40px);"),
        type("Высота экрана", "height: 100vh;"),
        type("Ширина не больше 600px", "max-width: 600px;"),
        type("Центрируй блок по горизонтали", "margin: 0 auto;")
    )),
    Lesson("Тёмная тема и keyframes", "Медиа-запрос prefers-color-scheme позволяет сделать тёмную тему по настройке телефона. @keyframes описывает шаги анимации, а свойство animation подключает её с длительностью и повторением.\n>@media (prefers-color-scheme: dark) { body { background: #111; color: #eee; } }\n>@keyframes pulse { from { opacity: 1; } to { opacity: 0.4; } }\n>.dot { animation: pulse 1s infinite; }", listOf(
        type("Тёмная тема по настройке системы", "@media (prefers-color-scheme: dark) {"),
        type("Тёмный фон", "background: #111;"),
        type("Объяви анимацию pulse", "@keyframes pulse {"),
        type("Подключи анимацию на 1 секунду бесконечно", "animation: pulse 1s infinite;"),
        type("Прозрачность 0.4", "opacity: 0.4;"),
        type("Плавное изменение цвета за 0.3 секунды", "transition: color 0.3s;")
    ))
))

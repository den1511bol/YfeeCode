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
    ))
))

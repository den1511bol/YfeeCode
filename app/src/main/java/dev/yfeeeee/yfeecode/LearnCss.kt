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
    ))
))

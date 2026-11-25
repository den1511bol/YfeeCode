// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun htmlCourse() = Course("html", "HTML", "🌐", listOf(
    Lesson("Структура страницы", "HTML состоит из тегов. Почти у каждого есть открывающая и закрывающая часть.\n>&lt;!DOCTYPE html>\n>&lt;html> ... &lt;/html>\n>&lt;head> ... &lt;/head>\n>&lt;body> ... &lt;/body>\nВ head лежат настройки, в body — то, что видно на странице.".replace("&lt;", "<"), listOf(
        build("Объяви документ HTML5", "<|!DOCTYPE|html|>", "?|/"),
        build("Открой тег body", "<|body|>", "</|head"),
        type("Закрой тег body", "</body>"),
        build("Задай заголовок вкладки", "<|title|>|Страница|</|title|>", "head|body"),
        type("Открой тег html", "<html>"),
        type("Закрой тег head", "</head>")
    )),
    Lesson("Текст и заголовки", "Заголовки — h1 … h6, абзац — p, жирный — b, курсив — i.\n>&lt;h1>Заголовок&lt;/h1>\n>&lt;p>Текст&lt;/p>\n>&lt;b>жирный&lt;/b>".replace("&lt;", "<"), listOf(
        build("Главный заголовок Привет", "<|h1|>|Привет|</|h1|>", "p|h2"),
        build("Абзац со словом Текст", "<|p|>|Текст|</|p|>", "h1|div"),
        type("Заголовок h2 со словом Меню", "<h2>Меню</h2>"),
        type("Жирное слово Важно", "<b>Важно</b>"),
        build("Перенос строки", "<|br|>", "/|p"),
        type("Курсив со словом Тут", "<i>Тут</i>")
    )),
    Lesson("Ссылки и картинки", "Ссылка — тег a с атрибутом href, картинка — тег img с атрибутом src.\n>&lt;a href=\"https://site.com\">Сайт&lt;/a>\n>&lt;img src=\"cat.png\">".replace("&lt;", "<"), listOf(
        build("Ссылка на site.com со словом Сайт", "<|a|href|=|\"site.com\"|>|Сайт|</|a|>", "src|img"),
        build("Картинка cat.png", "<|img|src|=|\"cat.png\"|>", "href|a"),
        type("Ссылка на a.com со словом Тут", "<a href=\"a.com\">Тут</a>"),
        type("Картинка dog.jpg", "<img src=\"dog.jpg\">"),
        build("Подпись к картинке", "alt|=|\"Кот\"", "href|src"),
        type("Атрибут src со значением x.png", "src=\"x.png\"")
    )),
    Lesson("Списки и блоки", "ul — маркированный список, ol — нумерованный, li — пункт. div — универсальный блок.\n>&lt;ul>\n>  &lt;li>Один&lt;/li>\n>&lt;/ul>\n>&lt;div class=\"box\">&lt;/div>".replace("&lt;", "<"), listOf(
        build("Пункт списка Один", "<|li|>|Один|</|li|>", "ul|ol"),
        build("Открой маркированный список", "<|ul|>", "li|/"),
        type("Закрой список ul", "</ul>"),
        type("Блок div с классом box", "<div class=\"box\">"),
        build("Нумерованный список", "<|ol|>", "ul|li"),
        type("Закрой div", "</div>")
    )),
    Lesson("Формы", "Форма собирает данные. input — поле ввода, button — кнопка.\n>&lt;input type=\"text\" placeholder=\"Имя\">\n>&lt;button>Отправить&lt;/button>".replace("&lt;", "<"), listOf(
        build("Текстовое поле", "<|input|type|=|\"text\"|>", "button|a"),
        build("Кнопка Отправить", "<|button|>|Отправить|</|button|>", "input|form"),
        type("Открой форму", "<form>"),
        type("Закрой форму", "</form>"),
        build("Подсказка в поле", "placeholder|=|\"Имя\"", "href|src"),
        type("Поле для пароля", "<input type=\"password\">")
    )),
    Lesson("Стили и скрипты", "Стили подключают тегом style или link, скрипты — тегом script.\n>&lt;style> p { color: red; } &lt;/style>\n>&lt;script src=\"app.js\">&lt;/script>".replace("&lt;", "<"), listOf(
        build("Открой тег style", "<|style|>", "script|link"),
        build("Подключи app.js", "<|script|src|=|\"app.js\"|>|</|script|>", "style|href"),
        type("Закрой тег style", "</style>"),
        type("Открой тег script", "<script>"),
        build("Подключи стили", "<|link|rel|=|\"stylesheet\"|href|=|\"a.css\"|>", "src|script"),
        type("Закрой тег script", "</script>")
    )),
    Lesson("Таблицы и семантика", "Таблица: table, строка tr, ячейка td. Семантические теги описывают смысл: header, nav, main, footer.\n>&lt;table>\n>  &lt;tr>&lt;td>1&lt;/td>&lt;td>2&lt;/td>&lt;/tr>\n>&lt;/table>\n>&lt;header>&lt;/header>&lt;nav>&lt;/nav>&lt;footer>&lt;/footer>".replace("&lt;", "<"), listOf(
        build("Открой таблицу", "<|table|>", "tr|td"),
        build("Ячейка со словом Имя", "<|td|>|Имя|</|td|>", "tr|th"),
        type("Открой строку таблицы", "<tr>"),
        type("Закрой таблицу", "</table>"),
        build("Шапка страницы", "<|header|>", "footer|nav"),
        type("Подвал страницы", "<footer>")
    ))
))

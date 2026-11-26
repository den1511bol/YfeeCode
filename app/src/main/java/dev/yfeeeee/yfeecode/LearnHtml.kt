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
    )),
    Lesson("Мета-теги и медиа", "В head кладут meta-теги: кодировку и viewport для телефонов. Для звука и видео есть audio и video.\n>&lt;meta charset=\"UTF-8\">\n>&lt;meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">".replace("&lt;", "<"), listOf(
        type("Укажи кодировку UTF-8", "<meta charset=\"UTF-8\">"),
        type("Адаптация под телефон", "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"),
        type("Подключи файл стилей", "<link rel=\"stylesheet\" href=\"style.css\">"),
        type("Вставь видео с кнопками управления", "<video src=\"a.mp4\" controls></video>"),
        type("Вставь аудио с кнопками управления", "<audio src=\"a.mp3\" controls></audio>"),
        type("Подключи скрипт", "<script src=\"app.js\"></script>")
    )),
    Lesson("Атрибуты форм", "Атрибуты настраивают поля: type выбирает вид поля, required делает его обязательным, placeholder показывает подсказку, label подписывает поле.\n>&lt;label for=\"e\">Почта&lt;/label>\n>&lt;input id=\"e\" type=\"email\" required>".replace("&lt;", "<"), listOf(
        type("Поле для почты", "<input type=\"email\">"),
        type("Поле для пароля", "<input type=\"password\">"),
        type("Обязательное поле", "<input required>"),
        type("Подсказка внутри поля", "<input placeholder=\"Имя\">"),
        type("Подпись, связанная с полем id=e", "<label for=\"e\">Почта</label>"),
        type("Выпадающий список", "<select></select>")
    )),
    Lesson("Аудио, видео и iframe", "audio и video встраивают медиа, controls показывает кнопки управления. iframe вставляет другую страницу. source задаёт файл.\n>&lt;audio src=\"a.mp3\" controls>&lt;/audio>\n>&lt;video src=\"v.mp4\" controls>&lt;/video>\n>&lt;iframe src=\"https://example.com\">&lt;/iframe>".replace("&lt;", "<"), listOf(
        type("Аудио a.mp3 с кнопками", "<audio src=\"a.mp3\" controls></audio>"),
        type("Видео v.mp4 с кнопками", "<video src=\"v.mp4\" controls></video>"),
        type("Вставь страницу через iframe", "<iframe src=\"https://example.com\"></iframe>"),
        type("Атрибут, показывающий кнопки плеера", "controls"),
        type("Источник внутри video", "<source src=\"v.mp4\" type=\"video/mp4\">"),
        type("Видео без звука и с автозапуском (атрибуты)", "<video src=\"v.mp4\" autoplay muted></video>")
    )),
    Lesson("Доступность", "Доступная страница понятна и людям, и программам чтения. У картинки пишут alt, у поля label, роли и подписи задают через aria-атрибуты. Структура заголовков идёт по порядку h1, h2, h3.\n>&lt;img src=\"a.png\" alt=\"Кот\">\n>&lt;label for=\"n\">Имя&lt;/label>\n>&lt;button aria-label=\"Закрыть\">x&lt;/button>".replace("&lt;", "<"), listOf(
        type("Картинка с описанием Кот", "<img src=\"a.png\" alt=\"Кот\">"),
        type("Подпись к полю с id n", "<label for=\"n\">Имя</label>"),
        type("Кнопка с подписью Закрыть для программ чтения", "<button aria-label=\"Закрыть\">x</button>"),
        type("Язык страницы русский", "<html lang=\"ru\">"),
        type("Роль навигации", "<nav role=\"navigation\">"),
        type("Спрячь элемент от программ чтения", "<span aria-hidden=\"true\"></span>")
    )),
    Lesson("Интерактивные теги", "details и summary делают раскрывающийся блок без скриптов. dialog создаёт окно, progress и meter показывают шкалы. data-атрибуты хранят свои данные.\n>&lt;details>&lt;summary>Подробнее&lt;/summary>Текст&lt;/details>\n>&lt;progress value=\"30\" max=\"100\">&lt;/progress>\n>&lt;div data-id=\"5\">&lt;/div>".replace("&lt;", "<"), listOf(
        type("Раскрывающийся блок", "<details><summary>Подробнее</summary>Текст</details>"),
        type("Шкала прогресса 30 из 100", "<progress value=\"30\" max=\"100\"></progress>"),
        type("Свой атрибут id со значением 5", "<div data-id=\"5\"></div>"),
        type("Тег окна диалога", "<dialog>"),
        type("Тег измерителя", "<meter value=\"0.5\"></meter>"),
        type("Заголовок раскрывающегося блока", "<summary>")
    )),
    Lesson("SVG и canvas", "svg рисует векторную графику прямо в HTML, canvas даёт холст для рисования скриптом. В svg есть circle, rect, line, text.\n>&lt;svg width=\"100\" height=\"100\">\n>  &lt;circle cx=\"50\" cy=\"50\" r=\"40\" fill=\"red\"/>\n>&lt;/svg>\n>&lt;canvas id=\"c\" width=\"200\" height=\"100\">&lt;/canvas>".replace("&lt;", "<"), listOf(
        type("Тег векторной графики", "<svg>"),
        type("Красный круг r=40 в центре 50 50", "<circle cx=\"50\" cy=\"50\" r=\"40\" fill=\"red\"/>"),
        type("Прямоугольник", "<rect width=\"10\" height=\"10\"/>"),
        type("Холст с id c", "<canvas id=\"c\"></canvas>"),
        type("Линия", "<line x1=\"0\" y1=\"0\" x2=\"10\" y2=\"10\"/>"),
        type("Текст внутри svg", "<text x=\"10\" y=\"20\">Привет</text>")
    )),
    Lesson("SEO и социальные сети", "Поисковики читают title и description. Open Graph мета-теги задают, как ссылка выглядит в мессенджерах. canonical указывает основную версию страницы.\n>&lt;title>Мой сайт&lt;/title>\n>&lt;meta name=\"description\" content=\"Описание\">\n>&lt;meta property=\"og:title\" content=\"Заголовок\">".replace("&lt;", "<"), listOf(
        type("Заголовок страницы", "<title>Мой сайт</title>"),
        type("Описание страницы", "<meta name=\"description\" content=\"Описание\">"),
        type("Open Graph заголовок", "<meta property=\"og:title\" content=\"Заголовок\">"),
        type("Картинка для превью ссылки", "<meta property=\"og:image\" content=\"a.png\">"),
        type("Основной адрес страницы", "<link rel=\"canonical\" href=\"https://example.com\">"),
        type("Иконка сайта", "<link rel=\"icon\" href=\"favicon.ico\">")
    )),
    Lesson("Шаблон страницы целиком", "Полная страница собирает всё вместе: doctype, html с языком, head с кодировкой и viewport, body с шапкой, основным содержимым и подвалом.\n>&lt;!DOCTYPE html>\n>&lt;html lang=\"ru\">\n>&lt;head>&lt;meta charset=\"UTF-8\">&lt;title>Сайт&lt;/title>&lt;/head>\n>&lt;body>&lt;header>&lt;/header>&lt;main>&lt;/main>&lt;footer>&lt;/footer>&lt;/body>\n>&lt;/html>".replace("&lt;", "<"), listOf(
        type("Кодировка UTF-8", "<meta charset=\"UTF-8\">"),
        type("Адаптация под телефон", "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"),
        type("Шапка страницы", "<header></header>"),
        type("Основное содержимое", "<main></main>"),
        type("Подвал страницы", "<footer></footer>"),
        type("Подключи файл стилей style.css", "<link rel=\"stylesheet\" href=\"style.css\">")
    )),
    Lesson("Проверка полей формы", "Браузер сам проверяет поля формы, если задать атрибуты. required делает поле обязательным, minlength и maxlength ограничивают длину, pattern задаёт шаблон, а type=email и type=number проверяют формат.\n>&lt;input type=\"email\" required>\n>&lt;input minlength=\"3\" maxlength=\"20\">\n>&lt;input pattern=\"[0-9]+\">\n>&lt;input type=\"number\" min=\"1\" max=\"10\">".replace("&lt;", "<"), listOf(
        type("Поле почты, обязательное", "<input type=\"email\" required>"),
        type("Минимальная длина 3", "minlength=\"3\""),
        type("Максимальная длина 20", "maxlength=\"20\""),
        type("Шаблон только цифры", "pattern=\"[0-9]+\""),
        type("Число от 1 до 10", "<input type=\"number\" min=\"1\" max=\"10\">"),
        type("Подсказка внутри поля", "placeholder=\"Имя\"")
    )),
    Lesson("details, data-атрибуты и шаблоны", "Тег details с summary делает раскрывающийся блок без JavaScript. Атрибуты data- хранят свои данные в теге, их читает скрипт. Тег template хранит заготовку, которая не показывается, пока скрипт её не вставит.\n>&lt;details>&lt;summary>Подробнее&lt;/summary>Текст&lt;/details>\n>&lt;div data-id=\"5\">&lt;/div>\n>&lt;template>&lt;li>Пункт&lt;/li>&lt;/template>".replace("&lt;", "<"), listOf(
        type("Раскрывающийся блок", "<details></details>"),
        type("Заголовок раскрывающегося блока", "<summary>Подробнее</summary>"),
        type("Свой атрибут data-id со значением 5", "data-id=\"5\""),
        type("Скрытая заготовка", "<template></template>"),
        type("Блок открыт сразу", "<details open>"),
        type("Иконка сайта в head", "<link rel=\"icon\" href=\"icon.png\">")
    ))
))

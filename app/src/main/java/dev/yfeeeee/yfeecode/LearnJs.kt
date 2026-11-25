// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

fun jsCourse() = Course("js", "JavaScript", "🟨", listOf(
    Lesson("Вывод: console.log", "console.log печатает значение в консоль. Строки пишут в кавычках, а в конце команды ставят точку с запятой.\n>console.log(\"Привет\");\n>console.log(2 + 3);", listOf(
        build("Выведи слово Привет", "console|.|log|(|\"Привет\"|)|;", "print|echo"),
        build("Выведи число 7", "console|.|log|(|7|)|;", "alert|\"7\""),
        type("Выведи Hello", "console.log(\"Hello\");"),
        build("Выведи сумму 1 и 2", "console|.|log|(|1|+|2|)|;", "-|*"),
        type("Выведи 3 * 4", "console.log(3 * 4);"),
        type("Выведи слово JS", "console.log(\"JS\");")
    )),
    Lesson("Переменные: let и const", "let создаёт изменяемую переменную, const — постоянную.\n>let score = 0;\n>const name = \"Аня\";\n>score = score + 1;", listOf(
        build("Создай переменную x = 10", "let|x|=|10|;", "const|=="),
        type("Создай константу PI = 3.14", "const PI = 3.14;"),
        build("Выведи переменную name", "console|.|log|(|name|)|;", "\"name\"|print"),
        type("Создай переменную age = 18", "let age = 18;"),
        build("Увеличь score на 1", "score|=|score|+|1|;", "let|-"),
        type("Выведи переменную score", "console.log(score);")
    )),
    Lesson("Условия: if", "if проверяет условие в круглых скобках, тело блока пишут в фигурных.\n>if (age >= 18) {\n>  console.log(\"Взрослый\");\n>} else {\n>  console.log(\"Ребёнок\");\n>}\nСравнивай через === (строгое равенство).", listOf(
        build("Если x больше 5", "if|(|x|>|5|)|{", "for|then|:"),
        type("Если age больше или равно 18", "if (age >= 18) {"),
        build("Строгое сравнение a и b", "if|(|a|===|b|)|{", "=|:"),
        type("Напиши else", "} else {"),
        build("Закрой блок", "}", "{|)"),
        type("Если name равно \"Аня\"", "if (name === \"Аня\") {")
    )),
    Lesson("Циклы", "for повторяет код по счётчику, while — пока условие верно.\n>for (let i = 0; i < 3; i++) {\n>  console.log(i);\n>}\n>while (n < 10) { n++; }", listOf(
        build("Цикл от 0 до 2", "for|(|let|i|=|0|;|i|<|3|;|i|++|)|{", "in|range|:"),
        type("Пока n меньше 10", "while (n < 10) {"),
        build("Увеличь i на 1", "i|++|;", "--|=="),
        type("Бесконечный цикл", "while (true) {"),
        build("Прерви цикл", "break|;", "stop|exit"),
        type("Продолжи со следующей итерации", "continue;")
    )),
    Lesson("Функции", "Функция — именованный блок кода. function создаёт её, return возвращает результат. Есть и короткая стрелочная запись.\n>function add(a, b) {\n>  return a + b;\n>}\n>const mul = (a, b) => a * b;", listOf(
        build("Объяви функцию hello", "function|hello|(|)|{", "def|func"),
        type("Объяви функцию add(a, b)", "function add(a, b) {"),
        build("Верни сумму a и b", "return|a|+|b|;", "print|=="),
        type("Вызови add(2, 3)", "add(2, 3);"),
        build("Стрелочная функция double", "const|double|=|x|=>|x|*|2|;", "function|let"),
        type("Выведи результат add(1, 2)", "console.log(add(1, 2));")
    )),
    Lesson("Массивы", "Массив хранит набор значений в квадратных скобках.\n>const nums = [1, 2, 3];\n>nums.push(4);\n>console.log(nums.length);", listOf(
        build("Создай массив nums", "const|nums|=|[|1|,|2|,|3|]|;", "(|)"),
        type("Добавь 4 в конец nums", "nums.push(4);"),
        build("Длина массива nums", "nums|.|length", "len|size"),
        type("Выведи длину nums", "console.log(nums.length);"),
        build("Первый элемент nums", "nums|[|0|]", "(|1"),
        type("Выведи последний элемент nums", "console.log(nums[nums.length - 1]);")
    )),
    Lesson("Запросы fetch", "fetch делает HTTP-запрос. Он асинхронный, поэтому используют async и await. Так работают простые боты Telegram в WebView.\n>const r = await fetch(url);\n>const data = await r.json();\n>async function main() { }", listOf(
        build("Сделай запрос по url", "const|r|=|await|fetch|(|url|)|;", "get|then"),
        type("Прочитай ответ как JSON", "const data = await r.json();"),
        build("Объяви асинхронную функцию main", "async|function|main|(|)|{", "await|def"),
        type("Запусти функцию main", "main();"),
        build("Ловить ошибки", "try|{", "catch|if"),
        type("Блок обработки ошибки", "} catch (e) {")
    )),
    Lesson("DOM и события", "DOM — это дерево элементов страницы. Находим элемент через querySelector, слушаем события через addEventListener.\n>const btn = document.querySelector(\"#btn\");\n>btn.addEventListener(\"click\", () => {\n>  btn.textContent = \"Нажато\";\n>});", listOf(
        build("Найди элемент с id btn", "const|btn|=|document|.|querySelector|(|\"#btn\"|)|;", "getElement|fetch"),
        type("Слушай клик по btn", "btn.addEventListener(\"click\", () => {"),
        build("Поменяй текст элемента", "btn|.|textContent|=|\"Привет\"|;", "console|log"),
        type("Добавь класс active", "btn.classList.add(\"active\");"),
        build("Прочитай значение поля input", "const|v|=|input|.|value|;", "text|get"),
        type("Закрой обработчик события", "});")
    )),
    Lesson("Промисы и async", "Promise — это обещание результата в будущем. then получает результат, catch ловит ошибку. await делает то же короче.\n>fetch(url)\n>  .then(r => r.json())\n>  .catch(e => console.error(e));\n>await new Promise(r => setTimeout(r, 1000));", listOf(
        build("Обработай результат промиса", "fetch|(|url|)|.|then|(|r|=>|r|.|json|(|)|)", "catch|await"),
        type("Лови ошибку промиса", ".catch(e => console.error(e));"),
        build("Подожди 1 секунду", "await|new|Promise|(|r|=>|setTimeout|(|r|,|1000|)|)|;", "fetch|then"),
        type("Объяви асинхронную стрелочную функцию", "const run = async () => {"),
        build("Дождись всех промисов", "await|Promise|.|all|(|list|)|;", "then|race"),
        type("Выведи ошибку в консоль", "console.error(e);")
    ))
))

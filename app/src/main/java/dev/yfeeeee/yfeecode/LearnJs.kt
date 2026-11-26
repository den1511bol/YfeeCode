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
    )),
    Lesson("map, filter, reduce", "Методы массивов принимают функцию. map преобразует каждый элемент, filter отбирает нужные, reduce сворачивает массив в одно значение.\n>const d = [1, 2, 3].map(x => x * 2)\n>const e = d.filter(x => x > 2)", listOf(
        build("Удвой каждое число", "nums|.|map|(|x|=>|x|*|2|)", "filter|for"),
        type("Оставь числа больше 10", "nums.filter(x => x > 10)"),
        build("Сумма через reduce", "nums|.|reduce|(|(|a|,|b|)|=>|a|+|b|,|0|)", "map|filter"),
        type("Пройди по массиву через forEach", "nums.forEach(x => console.log(x))"),
        build("Найди первый элемент больше 3", "nums|.|find|(|x|=>|x|>|3|)", "filter|map"),
        type("Проверь, есть ли 5 в массиве", "nums.includes(5)")
    )),
    Lesson("Классы и деструктуризация", "class описывает объект, constructor задаёт начальные поля. Деструктуризация достаёт значения из массива или объекта в одну строку.\n>class Dog { constructor(name) { this.name = name } }\n>const { name } = dog", listOf(
        build("Объяви класс Dog", "class|Dog|{", "function|const"),
        type("Конструктор с параметром name", "constructor(name) {"),
        type("Запиши имя в объект", "this.name = name;"),
        build("Создай объект Dog", "new|Dog|(|\"Rex\"|)", "class|this"),
        type("Достань name из объекта dog", "const { name } = dog;"),
        type("Достань два элемента из массива arr", "const [a, b] = arr;")
    )),
    Lesson("Строки и шаблоны", "Строки в JS удобно собирать шаблонными литералами в обратных кавычках: подстановка через \${...}. Методы: toUpperCase, trim, split, includes, replace, slice.\n>const s = `Привет, \${name}!`;\n>s.toUpperCase();\n>\"a,b\".split(\",\");\n>s.includes(\"при\");", listOf(
        type("Строка-шаблон с подстановкой name", "const s = `Привет, \${name}!`;"),
        type("Верхний регистр строки s", "s.toUpperCase()"),
        type("Убери пробелы по краям", "s.trim()"),
        type("Раздели строку по запятой", "s.split(\",\")"),
        type("Проверь, содержит ли s слово при", "s.includes(\"при\")"),
        type("Длина строки s", "s.length")
    )),
    Lesson("Объекты", "Объект хранит пары ключ-значение. К полю обращаются через точку или квадратные скобки. Object.keys, values и entries перечисляют поля.\n>const user = { name: \"Аня\", age: 20 };\n>user.name;\n>user[\"age\"];\n>Object.keys(user);", listOf(
        type("Создай объект user с полем name Аня", "const user = { name: \"Аня\" };"),
        type("Прочитай поле name", "user.name"),
        type("Прочитай поле age через скобки", "user[\"age\"]"),
        type("Задай полю age значение 21", "user.age = 21;"),
        type("Получи список ключей объекта", "Object.keys(user)"),
        type("Удали поле age", "delete user.age;")
    )),
    Lesson("Замыкания и область видимости", "Переменная let видна только в своём блоке. Функция запоминает переменные, где была создана: это замыкание. Так делают счётчики и приватные данные.\n>function counter() {\n>  let n = 0;\n>  return () => ++n;\n>}\n>const c = counter();\n>c(); // 1", listOf(
        type("Объяви счётчик n равный 0", "let n = 0;"),
        type("Верни стрелочную функцию с увеличением n", "return () => ++n;"),
        type("Создай счётчик c", "const c = counter();"),
        type("Вызови c", "c();"),
        type("Увеличь n на 1 (короткая запись)", "n++;"),
        type("Функция выражением f", "const f = function() {};")
    )),
    Lesson("Стрелочные функции и this", "Стрелка короче обычной функции: (a, b) => a + b. Если в теле одно выражение, return не нужен. Стрелка не имеет своего this.\n>const add = (a, b) => a + b;\n>const sq = x => x * x;\n>const hi = () => { console.log(\"hi\"); };", listOf(
        type("Стрелка сложения a и b", "const add = (a, b) => a + b;"),
        type("Стрелка квадрата x с одним параметром", "const sq = x => x * x;"),
        type("Стрелка без параметров, пишет hi", "const hi = () => { console.log(\"hi\"); };"),
        type("Передай стрелку в map", "arr.map(x => x * 2)"),
        type("Вызови add с 1 и 2", "add(1, 2)"),
        type("Функция по умолчанию b равно 2", "function f(a, b = 2) {}")
    )),
    Lesson("Spread и rest", "Три точки разворачивают массив или объект (spread) и собирают остальные аргументы (rest). Так копируют и объединяют данные.\n>const a = [1, 2];\n>const b = [...a, 3];\n>const o2 = { ...o, x: 1 };\n>function f(...args) {}", listOf(
        type("Новый массив из a и числа 3", "const b = [...a, 3];"),
        type("Копия объекта o с полем x равным 1", "const o2 = { ...o, x: 1 };"),
        type("Функция с любым числом аргументов", "function f(...args) {}"),
        type("Склей массивы a и b", "const c = [...a, ...b];"),
        type("Максимум из массива a", "Math.max(...a)"),
        type("Первый элемент и остальные", "const [first, ...rest] = a;")
    )),
    Lesson("Map, Set и JSON", "Map хранит пары с любыми ключами, Set только уникальные значения. JSON.stringify превращает объект в строку, JSON.parse обратно.\n>const m = new Map();\n>m.set(\"a\", 1);\n>const s = new Set([1, 2, 2]);\n>JSON.stringify(obj);\n>JSON.parse(text);", listOf(
        type("Создай Map", "const m = new Map();"),
        type("Положи в m ключ a со значением 1", "m.set(\"a\", 1);"),
        type("Создай Set из 1, 2, 2", "const s = new Set([1, 2, 2]);"),
        type("Объект obj в строку JSON", "JSON.stringify(obj)"),
        type("Строку text в объект", "JSON.parse(text)"),
        type("Размер множества s", "s.size")
    )),
    Lesson("Ошибки: try/catch", "try пробует код, catch ловит ошибку, finally выполняется всегда. throw создаёт свою ошибку. Для async-кода try/catch работает вместе с await.\n>try {\n>  JSON.parse(text);\n>} catch (e) {\n>  console.log(e.message);\n>}", listOf(
        type("Начни блок try", "try {"),
        type("Поймай ошибку в e", "} catch (e) {"),
        type("Выведи сообщение ошибки", "console.log(e.message);"),
        type("Блок, который выполнится всегда", "} finally {"),
        type("Брось свою ошибку", "throw new Error(\"плохо\");"),
        type("Проверь тип значения x", "typeof x")
    )),
    Lesson("Таймеры и цикл событий", "setTimeout запускает функцию один раз позже, setInterval повторяет. Они не блокируют программу: JS ставит задачу в очередь и идёт дальше.\n>setTimeout(() => console.log(\"ок\"), 1000);\n>const id = setInterval(tick, 500);\n>clearInterval(id);", listOf(
        type("Выведи ок через 1000 мс", "setTimeout(() => console.log(\"ок\"), 1000);"),
        type("Повторяй tick каждые 500 мс", "const id = setInterval(tick, 500);"),
        type("Останови интервал id", "clearInterval(id);"),
        type("Отмени таймер t", "clearTimeout(t);"),
        type("Пауза через промис на 1 секунду", "await new Promise(r => setTimeout(r, 1000));"),
        type("Текущее время в миллисекундах", "Date.now()")
    )),
    Lesson("Модули import и export", "Модули делят код на файлы. export открывает функцию, import подключает. В Node.js ещё встречается require.\n>export function add(a, b) { return a + b; }\n>import { add } from \"./math.js\";\n>export default add;", listOf(
        type("Экспортируй функцию add", "export function add(a, b) { return a + b; }"),
        type("Импортируй add из math.js", "import { add } from \"./math.js\";"),
        type("Экспорт по умолчанию", "export default add;"),
        type("Импортируй всё как m", "import * as m from \"./math.js\";"),
        type("Подключи fs через require", "const fs = require(\"fs\");"),
        type("Экспорт через module.exports", "module.exports = add;")
    )),
    Lesson("Node.js: файлы и скрипты", "Node.js запускает JS без браузера. Модуль fs читает и пишет файлы, process.argv хранит аргументы. Пакеты ставят через npm.\n>const fs = require(\"fs\");\n>const t = fs.readFileSync(\"a.txt\", \"utf8\");\n>fs.writeFileSync(\"b.txt\", \"привет\");\n>npm install express", listOf(
        type("Прочитай a.txt как utf8", "fs.readFileSync(\"a.txt\", \"utf8\")"),
        type("Запиши привет в b.txt", "fs.writeFileSync(\"b.txt\", \"привет\");"),
        type("Аргументы командной строки", "process.argv"),
        type("Установи express", "npm install express"),
        type("Запусти файл app.js в Node", "node app.js"),
        type("Создай package.json", "npm init -y")
    )),
    Lesson("Регулярные выражения", "Регулярное выражение пишут между слэшами. \\d цифра, \\w буква, + один или больше. Методы: test, match, replace.\n>/\\d+/.test(\"abc123\");\n>\"a1b22\".match(/\\d+/g);\n>s.replace(/\\s+/g, \" \");", listOf(
        type("Проверь, есть ли цифры в строке s", "/\\d+/.test(s)"),
        type("Найди все числа в строке s", "s.match(/\\d+/g)"),
        type("Замени все пробелы на один", "s.replace(/\\s+/g, \" \")"),
        type("Флаг глобального поиска", "g"),
        type("Любая буква или цифра в шаблоне", "\\w"),
        type("Создай регулярку из строки", "new RegExp(\"a+\")")
    )),
    Lesson("Продвинутые массивы", "find ищет первый подходящий элемент, some и every проверяют условие, sort сортирует, flat выравнивает вложенные массивы, includes проверяет наличие.\n>arr.find(x => x > 2);\n>arr.some(x => x > 2);\n>arr.sort((a, b) => a - b);\n>arr.includes(3);", listOf(
        type("Найди первый элемент больше 2", "arr.find(x => x > 2)"),
        type("Есть ли хоть один больше 2", "arr.some(x => x > 2)"),
        type("Все ли больше 0", "arr.every(x => x > 0)"),
        type("Отсортируй числа по возрастанию", "arr.sort((a, b) => a - b)"),
        type("Проверь наличие 3", "arr.includes(3)"),
        type("Склей массив через дефис", "arr.join(\"-\")")
    )),
    Lesson("npm и package.json", "npm это менеджер пакетов Node.js. npm init -y создаёт package.json, где записаны зависимости. npm install ставит пакет в папку node_modules. Запуск файла: node index.js.\n>npm init -y\n>npm install express\n>node index.js\n>const express = require(\"express\")", listOf(
        type("Создай package.json", "npm init -y"),
        type("Установи пакет express", "npm install express"),
        type("Запусти файл index.js", "node index.js"),
        type("Подключи express (require)", "const express = require(\"express\")"),
        type("Установи все зависимости из package.json", "npm install"),
        type("Установи пакет только для разработки nodemon", "npm install --save-dev nodemon")
    )),
    Lesson("Сервер на Express", "Express создаёт веб-сервер за несколько строк. app.get описывает адрес и ответ, res.send отправляет текст, res.json отправляет JSON, app.listen запускает сервер на порту.\n>const express = require(\"express\")\n>const app = express()\n>app.get(\"/\", (req, res) => {\n>  res.send(\"Привет\")\n>})\n>app.listen(3000)", listOf(
        type("Создай приложение express", "const app = express()"),
        type("Опиши GET-адрес /hello", "app.get(\"/hello\", (req, res) => {"),
        type("Отправь текст Привет", "res.send(\"Привет\")"),
        type("Отправь объект data как JSON", "res.json(data)"),
        type("Включи разбор JSON в теле запроса", "app.use(express.json())"),
        type("Запусти сервер на порту 3000", "app.listen(3000)")
    )),
    Lesson("Telegram-боты на Node.js", "Бот на Node.js: пакет node-telegram-bot-api. Создаёшь бота с токеном и polling, onText ловит команды, sendMessage отвечает. Токен храни в переменной окружения, а не в коде.\n>const TelegramBot = require(\"node-telegram-bot-api\")\n>const bot = new TelegramBot(process.env.TOKEN, { polling: true })\n>bot.onText(/\\/start/, (msg) => {\n>  bot.sendMessage(msg.chat.id, \"Привет\")\n>})", listOf(
        type("Установи пакет бота", "npm install node-telegram-bot-api"),
        type("Подключи библиотеку", "const TelegramBot = require(\"node-telegram-bot-api\")"),
        type("Создай бота с токеном из окружения", "const bot = new TelegramBot(process.env.TOKEN, { polling: true })"),
        type("Ловить команду /start", "bot.onText(/\\/start/, (msg) => {"),
        type("Ответь в тот же чат", "bot.sendMessage(msg.chat.id, \"Привет\")"),
        type("Прочитай токен из окружения", "process.env.TOKEN")
    )),
    Lesson("DOM: создание элементов", "JavaScript умеет создавать элементы на лету. createElement делает тег, textContent задаёт текст, appendChild добавляет в страницу, remove убирает. Так строят списки задач и карточки.\n>const li = document.createElement(\"li\")\n>li.textContent = \"Купить хлеб\"\n>list.appendChild(li)\n>li.remove()", listOf(
        type("Создай элемент li", "const li = document.createElement(\"li\")"),
        type("Задай текст элемента", "li.textContent = \"Купить хлеб\""),
        type("Добавь li в список list", "list.appendChild(li)"),
        type("Удали элемент li", "li.remove()"),
        type("Добавь класс done", "li.classList.add(\"done\")"),
        type("Найди элемент с id list", "document.getElementById(\"list\")")
    )),
    Lesson("localStorage: память браузера", "localStorage хранит строки между запусками страницы. setItem записывает, getItem читает. Объекты и массивы превращают в строку через JSON.stringify и обратно через JSON.parse.\n>localStorage.setItem(\"name\", \"Аня\")\n>const n = localStorage.getItem(\"name\")\n>localStorage.setItem(\"list\", JSON.stringify(arr))\n>const arr = JSON.parse(localStorage.getItem(\"list\"))", listOf(
        type("Запиши name в память", "localStorage.setItem(\"name\", \"Аня\")"),
        type("Прочитай name из памяти", "const n = localStorage.getItem(\"name\")"),
        type("Сохрани массив arr как строку", "localStorage.setItem(\"list\", JSON.stringify(arr))"),
        type("Прочитай массив обратно", "const arr = JSON.parse(localStorage.getItem(\"list\"))"),
        type("Удали ключ name", "localStorage.removeItem(\"name\")"),
        type("Очисти всю память", "localStorage.clear()")
    ))
))

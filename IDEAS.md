# IDEAS (разработчик @yfeeeee)

Находки и идеи по ходу работы. OVERNIGHT.md не трогаем (им пользуется цикл практик).

## Сделано: доступность (LearnUi, LinuxScreen, HelpScreen)
- LearnUi: role=Button и onClickLabel у карточек курсов, уроков и практик; stateDescription (пройден, закрыт, доступен); RadioButton + selected у вариантов ответа.
- LearnUi: прогресс-бары (уровень, урок) с ProgressBarRangeInfo, а не просто цветные Box.
- LearnUi: деление на ноль. Для пустого списка уроков или упражнений доля прогресса считалась как NaN, а fillMaxWidth(NaN) падает. Теперь frac() возвращает 0f. Урок без упражнений сразу завершается.
- LinuxScreen: описания у кнопок-символов (^C, A−, A+, ⌨↓, спецсимволы), состояние Ctrl и Alt, selected у вкладок сессий, описание у самого терминала.
- HelpScreen: заголовок экрана как heading, секции как кнопки со свёрнуто и развёрнуто, нечитаемые символы ▾ и ▸ скрыты от TalkBack.

## Идеи на потом
- DevKeyboard (Keyboard.kt): проверить contentDescription у клавиш.
- PracticeUi.kt, CodeEditor.kt: тот же проход по доступности (не входило в задачу).
- Один общий helper для прогресс-бара вместо копий Box в разных экранах.

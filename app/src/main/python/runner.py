# YfeeCode — developer: @yfeeeee
"""Runs a user script in the :bot process. Output goes to a log file."""
import io
import os
import runpy
import sys
import traceback


class _LogWriter:
    def __init__(self, path):
        self.f = open(path, "a", encoding="utf-8", buffering=1)

    def write(self, s):
        self.f.write(s)
        self.f.flush()
        return len(s)

    def flush(self):
        self.f.flush()

    def isatty(self):
        return False


def _prepare_thread_env():
    """User scripts run in a worker thread: give them an event loop and tolerate signal calls."""
    import asyncio
    import signal

    asyncio.set_event_loop(asyncio.new_event_loop())

    _orig = signal.signal

    def _safe_signal(sig, handler):
        try:
            return _orig(sig, handler)
        except ValueError:
            return None

    signal.signal = _safe_signal
    try:
        import asyncio.unix_events as ue
        _orig_add = ue._UnixSelectorEventLoop.add_signal_handler

        def _safe_add(self, sig, cb, *args):
            try:
                return _orig_add(self, sig, cb, *args)
            except (ValueError, RuntimeError, NotImplementedError):
                return None

        ue._UnixSelectorEventLoop.add_signal_handler = _safe_add
    except Exception:
        pass
    # no console: input() would block forever
    sys.stdin = io.StringIO("")


def _hint(e):
    """Short Russian hint for the most common errors (developer: @yfeeeee)."""
    name = type(e).__name__
    msg = str(e)
    low = msg.lower()
    if "invalidtoken" in name.lower() or "unauthorized" in low or "token is invalid" in low or "invalid token" in low:
        return "Telegram не принял токен: проверь token.txt (токен из @BotFather, без пробелов и лишних строк)."
    if isinstance(e, ModuleNotFoundError):
        mod = getattr(e, "name", None) or msg
        return "Нет модуля «%s». Поставь его: вкладка Linux, затем pip install %s, и запусти файл кнопкой «Запуск в Linux»." % (mod, str(mod).split(".")[0])
    if isinstance(e, IndentationError):
        return "Ошибка отступов: проверь, что отступы одинаковые (4 пробела) и после «:» есть блок."
    if isinstance(e, SyntaxError):
        return "Синтаксическая ошибка: проверь строку %s (скобки, кавычки, двоеточия)." % (e.lineno,)
    if isinstance(e, NameError):
        return "Имя не определено: опечатка или не хватает import / присваивания. " + msg
    if isinstance(e, FileNotFoundError):
        return "Файл не найден. Текущая папка при запуске — папка скрипта."
    if isinstance(e, ZeroDivisionError):
        return "Деление на ноль."
    if isinstance(e, KeyError):
        return "Нет такого ключа в словаре: " + msg
    if isinstance(e, IndexError):
        return "Индекс выходит за границы списка."
    if isinstance(e, AttributeError):
        return "У объекта нет такого атрибута или метода: " + msg
    if isinstance(e, TypeError):
        return "Неподходящий тип или неверное число аргументов: " + msg
    if isinstance(e, ValueError):
        return "Неверное значение: " + msg
    if isinstance(e, ConnectionError) or "network" in low or "name resolution" in low:
        return "Проблема с сетью: проверь интернет."
    return ""


def _load_env(path):
    """Loads KEY=VALUE lines from .env into os.environ. Developer: @yfeeeee"""
    try:
        if not os.path.exists(path):
            return
        n = 0
        for line in open(path, encoding="utf-8").read().splitlines():
            line = line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            k, v = line.split("=", 1)
            os.environ[k.strip()] = v.strip().strip('"').strip("'")
            n += 1
        print("[env] загружено переменных из .env: " + str(n))
    except Exception as e:
        print("[env] ошибка чтения .env: " + str(e))


def run(script_path, log_path):
    w = _LogWriter(log_path)
    sys.stdout = w
    sys.stderr = w
    workdir = os.path.dirname(script_path)
    os.chdir(workdir)
    if workdir not in sys.path:
        sys.path.insert(0, workdir)
    print("[run] " + os.path.basename(script_path))
    _load_env(os.path.join(workdir, ".env"))
    try:
        _prepare_thread_env()
        runpy.run_path(script_path, run_name="__main__")
        print("[done] exit 0")
    except SystemExit as e:
        print("[done] exit %s" % (e.code,))
    except BaseException as e:
        traceback.print_exc()
        try:
            h = _hint(e)
            if h:
                print("[подсказка] " + h)
        except Exception:
            pass
        print("[done] crashed")

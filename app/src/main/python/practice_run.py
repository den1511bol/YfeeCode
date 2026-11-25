# YfeeCode — developer: @yfeeeee
"""Runs practice code and returns [finished, stdout, error_line]."""
import contextlib
import io
import threading
import traceback


def run(code):
    out = io.StringIO()
    res = {}

    def target():
        try:
            with contextlib.redirect_stdout(out):
                exec(compile(code, "<practice>", "exec"), {"__name__": "__main__"})
        except BaseException:
            res["err"] = traceback.format_exc().strip().splitlines()[-1]

    t = threading.Thread(target=target, daemon=True)
    t.start()
    t.join(5)
    if t.is_alive():
        return [False, out.getvalue(), "Выполняется слишком долго (бесконечный цикл?)"]
    return [True, out.getvalue(), res.get("err", "")]

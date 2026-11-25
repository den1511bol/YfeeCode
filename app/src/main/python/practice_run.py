# YfeeCode — developer: @yfeeeee
"""Runs practice code and returns [finished, stdout, error_line]."""
import contextlib
import ctypes
import io
import sys
import threading
import traceback


def run(code):
    out = io.StringIO()
    res = {}

    def target():
        try:
            with contextlib.redirect_stdout(out):
                exec(compile(code, "<practice>", "exec"), {"__name__": "__main__"})
        except SystemExit as e:
            if e.code not in (None, 0):
                res["err"] = "SystemExit: %s" % (e.code,)
        except BaseException:
            res["err"] = traceback.format_exc().strip().splitlines()[-1]

    t = threading.Thread(target=target, daemon=True)
    t.start()
    t.join(5)
    if t.is_alive():
        # stop the runaway thread so it does not burn CPU or keep stdout redirected
        ctypes.pythonapi.PyThreadState_SetAsyncExc(ctypes.c_ulong(t.ident), ctypes.py_object(SystemExit))
        t.join(1)
        if t.is_alive():
            sys.stdout = sys.__stdout__
        return [False, out.getvalue(), "Выполняется слишком долго (бесконечный цикл?)"]
    return [True, out.getvalue(), res.get("err", "")]

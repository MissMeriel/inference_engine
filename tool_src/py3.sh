python3 -m venv .venv
. .venv/bin/activate
pip install 2to3 matplotlib
2to3 -w *.py

#!/bin/bash

LOGS="/home/jockerlost/Escritorio/HelpTata/logs"

if [ ! -f "$LOGS/pids.txt" ]; then
  echo "No se encontraron procesos iniciados por start-helptata.sh"
  exit 0
fi

echo "Deteniendo HelpTata..."
while read pid; do
  if kill -0 "$pid" 2>/dev/null; then
    kill "$pid"
    echo "  -> Detenido PID $pid"
  fi
done < "$LOGS/pids.txt"

rm -f "$LOGS/pids.txt"
echo "Listo."

#!/bin/bash

BASE="/home/jockerlost/Escritorio/HelpTata"
LOGS="$BASE/logs"
mkdir -p "$LOGS"

echo "Iniciando HelpTata..."

start_ms() {
  local nombre=$1
  local dir=$2
  echo "  -> $nombre"
  cd "$dir" && nohup ./mvnw spring-boot:run > "$LOGS/$nombre.log" 2>&1 &
  echo $! >> "$LOGS/pids.txt"
}

# Limpiar PIDs anteriores
> "$LOGS/pids.txt"

start_ms "msUsuario"            "$BASE/msUsuarioHelpTata"
start_ms "msLogs"               "$BASE/msLogsHelpTata"
start_ms "msDireccion"          "$BASE/msDireccionHelpTata"
start_ms "msProgreso"           "$BASE/msProgresoHelpTata"
start_ms "msTutoriales"         "$BASE/msTutorialesHelpTata"
start_ms "msEvaluaciones"       "$BASE/msEvaluacionesHelpTata"
start_ms "msPreguntasRespuestas" "$BASE/msPreguntasYRespuestasHelpTata"

echo "  -> Frontend"
cd "$BASE/HelpTataFrontend" && nohup npm run dev > "$LOGS/frontend.log" 2>&1 &
echo $! >> "$LOGS/pids.txt"

echo ""
echo "Todo iniciado. Los logs están en: $LOGS/"
echo "Para ver un log en tiempo real: tail -f $LOGS/msTutoriales.log"
echo "Para detener todo: $BASE/stop-helptata.sh"

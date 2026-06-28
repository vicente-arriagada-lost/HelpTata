#!/bin/bash
# Uso: ./monitor-errors.sh
# Muestra en tiempo real los errores REALES de todos los contenedores HelpTata.

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

echo -e "${BOLD}╔══════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║        Monitor de Errores — HelpTata             ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${RESET}"
echo -e "  Solo errores reales — ruido de arranque filtrado"
echo -e "  Ctrl+C para detener\n"

docker compose logs -f --no-color 2>&1 \
| grep -i --line-buffered "ERROR\|WARN\|exception\|warn" \
| grep -v --line-buffered -E \
    "PostgreSQLDialect|open-in-view|UserDetailsManager|deprecated|InitializeUser|AuthenticationProvider" \
| grep -v --line-buffered -E \
    "No static resource|NoResourceFoundException|GlobalException.*request '/'|No se encontró progreso para" \
| grep -v --line-buffered -E \
    "trust.*authentication|no usable system locales|time=\".*level=warning" \
| grep -v --line-buffered -E \
    "directory index.*is forbidden" \
| grep -v --line-buffered -E \
    "\|\s*(at |Caused by:|\.{3}[0-9])" \
| grep -v --line-buffered -E \
    "\.jar!|~\[.*\.jar|ErrorReportValve|ExceptionTranslationFilter|StandardWrapperValve" \
| while IFS= read -r linea; do

    # ── Nombre del contenedor ─────────────────────────────────────────────────
    contenedor=$(echo "$linea" | sed 's/[[:space:]]*|.*//' | tr -d ' ')
    contenedor=$(echo "$contenedor" | sed 's/^helptata-//')

    # ── Mensaje (lo que viene después del |) ─────────────────────────────────
    mensaje=$(echo "$linea" | cut -d'|' -f2- | sed 's/^[[:space:]]*//')

    # ── Saltar líneas vacías ──────────────────────────────────────────────────
    [ -z "$(echo "$mensaje" | tr -d '[:space:]')" ] && continue

    # ── Nivel ─────────────────────────────────────────────────────────────────
    if echo "$mensaje" | grep -qiE "\bERROR\b"; then
        nivel="ERROR"
        color=$RED
    else
        nivel="WARN"
        color=$YELLOW
    fi

    # ── Campos extras ─────────────────────────────────────────────────────────
    http_code=$(echo "$mensaje" | grep -oP 'HTTP \K[0-9]+' | head -1 || true)
    endpoint=$(echo "$mensaje" | grep -oP '(?<=en )/[^\s:,]+' | head -1 || true)
    excepcion=$(echo "$mensaje" | grep -oP '\b\w+Exception\b' | grep -v "ExceptionTranslationFilter\|ExceptionHandler" | head -1 || true)

    # ── Detalle: texto después del último ": " ────────────────────────────────
    detalle=$(echo "$mensaje" | grep -oP '(?<=: ).*' | tail -1 | sed 's/^[[:space:]]*//')
    [ -z "$detalle" ] && detalle=$(echo "$mensaje" | cut -c1-150)

    # ── Bloque formateado ─────────────────────────────────────────────────────
    hora=$(date '+%H:%M:%S')
    echo -e "${color}${BOLD}▶ Fallo en: ${contenedor:-desconocido}${RESET}"
    echo -e "  ${BLUE}Hora:${RESET}      $hora"
    echo -e "  ${BLUE}Nivel:${RESET}     ${color}${nivel}${RESET}"
    [ -n "$http_code" ] && echo -e "  ${BLUE}Código:${RESET}    HTTP $http_code"
    [ -n "$endpoint"  ] && echo -e "  ${BLUE}Endpoint:${RESET}  $endpoint"
    [ -n "$excepcion" ] && echo -e "  ${BLUE}Excepción:${RESET} ${RED}$excepcion${RESET}"
    echo -e "  ${BLUE}Detalle:${RESET}   $detalle"
    echo -e "  ${CYAN}──────────────────────────────────────────────────${RESET}\n"
done

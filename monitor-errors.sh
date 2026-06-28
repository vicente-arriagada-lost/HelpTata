#!/bin/bash
# Uso: ./monitor-errors.sh
# Muestra en tiempo real los errores REALES de todos los contenedores HelpTata.
# Filtra ruido conocido de arranque, healthchecks y comportamiento esperado.

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# ── Ruido de arranque de Spring Boot (aparecen al iniciar, no son bugs) ───────
IGNORAR_SPRING="PostgreSQLDialect|open-in-view|UserDetailsManager|deprecated|InitializeUser|AuthenticationProvider"

# ── Comportamiento esperado que NO es un error real ───────────────────────────
# · "No static resource for '/'" → healthcheck de Cloudflare tocando / en MS de solo API
# · "No se encontró progreso"    → usuario que aún no ha empezado un tutorial (diseñado así)
# · "trust.*authentication"      → mensaje informativo de postgres
# · "no usable system locales"   → mensaje de postgres en alpine, inofensivo
# · time=".* level=warning       → stderr del demonio docker-compose, no es log de un MS
IGNORAR_OK="No static resource .* for request '/'|NoResourceFoundException.*'/'|GlobalException.*request '/'|No se encontró progreso para|trust.*authentication|no usable system locales|time=\".*level=warning"

echo -e "${BOLD}╔══════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║        Monitor de Errores — HelpTata             ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${RESET}"
echo -e "  Solo errores reales — ruido de arranque filtrado"
echo -e "  Ctrl+C para detener\n"

docker compose logs -f --no-color 2>&1 \
| grep -i --line-buffered "ERROR\|WARN\|Exception\|warn" \
| grep -v --line-buffered -E "$IGNORAR_SPRING" \
| grep -v --line-buffered -E "$IGNORAR_OK" \
| while IFS= read -r linea; do

    # ── Nombre del contenedor ─────────────────────────────────────────────────
    contenedor=$(echo "$linea" | sed 's/[[:space:]]*|.*//' | tr -d ' ')
    contenedor=$(echo "$contenedor" | sed 's/^helptata-//')

    # ── Mensaje (lo que viene después del |) ─────────────────────────────────
    mensaje=$(echo "$linea" | cut -d'|' -f2- | sed 's/^[[:space:]]*//')

    # ── Saltar líneas de stack trace (empiezan con "at " o "Caused by:") ─────
    if echo "$mensaje" | grep -qE '^\s+(at |Caused by:|\.{3}[0-9])'; then
        continue
    fi

    # ── Saltar líneas vacías después de extraer el mensaje ───────────────────
    contenido=$(echo "$mensaje" | sed 's/^[0-9T:Z. -]*\(INFO\|WARN\|ERROR\|DEBUG\).*//' | tr -d '[:space:]')
    [ -z "$contenido" ] && continue

    # ── Nivel ─────────────────────────────────────────────────────────────────
    if echo "$mensaje" | grep -qiE "\bERROR\b|Exception"; then
        nivel="ERROR"
        color=$RED
    else
        nivel="WARN"
        color=$YELLOW
    fi

    # ── Código HTTP ───────────────────────────────────────────────────────────
    http_code=$(echo "$mensaje" | grep -oP 'HTTP \K[0-9]+' | head -1 || true)

    # ── Endpoint ──────────────────────────────────────────────────────────────
    endpoint=$(echo "$mensaje" | grep -oP '(?<=en )/[^\s:,]+' | head -1 || true)

    # ── Tipo de excepción ─────────────────────────────────────────────────────
    excepcion=$(echo "$mensaje" | grep -oP '\b\w+Exception\b' | head -1 || true)

    # ── Detalle limpio ────────────────────────────────────────────────────────
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

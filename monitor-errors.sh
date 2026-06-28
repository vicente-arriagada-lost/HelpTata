#!/bin/bash
# Uso: ./monitor-errors.sh
# Muestra en tiempo real los errores de todos los contenedores HelpTata.

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# Warnings de arranque que no son útiles en producción
IGNORAR="HHH90000|open-in-view|UserDetailsManager|PostgreSQLDialect|deprecated|InitializeUser"

echo -e "${BOLD}╔══════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║        Monitor de Errores — HelpTata             ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${RESET}"
echo -e "  Escuchando errores de todos los contenedores..."
echo -e "  Ctrl+C para detener\n"

docker compose logs -f --no-color 2>&1 \
| grep -i --line-buffered "ERROR\|WARN\|Exception\|error HTTP\|warn" \
| grep -v --line-buffered "$IGNORAR" \
| while IFS= read -r linea; do

    # ── Nombre del contenedor ──────────────────────────────────
    contenedor=$(echo "$linea" | sed 's/[[:space:]]*|.*//' | tr -d ' ')
    # Limpiar prefijo "helptata-" si existe
    contenedor=$(echo "$contenedor" | sed 's/helptata-/ms-/')

    # ── Mensaje (lo que viene después del |) ──────────────────
    mensaje=$(echo "$linea" | cut -d'|' -f2- | sed 's/^[[:space:]]*//')

    # ── Nivel: ERROR o WARN ────────────────────────────────────
    if echo "$mensaje" | grep -qiE "ERROR|Exception"; then
        nivel="ERROR"
        color=$RED
    else
        nivel="WARN"
        color=$YELLOW
    fi

    # ── Código HTTP si viene en el mensaje ─────────────────────
    http_code=$(echo "$mensaje" | grep -oP 'HTTP \K[0-9]+' || true)

    # ── Endpoint si viene en el mensaje ───────────────────────
    endpoint=$(echo "$mensaje" | grep -oP 'en \K/[^\s:]+' || true)

    # ── Detalle limpio: texto después del último ":" ───────────
    detalle=$(echo "$mensaje" | grep -oP '(?<=: )[^:]+$' | sed 's/^[[:space:]]*//' || echo "$mensaje" | cut -c1-120)

    # ── Tipo de excepción si aparece ──────────────────────────
    excepcion=$(echo "$mensaje" | grep -oP '\w+Exception' | head -1 || true)

    # ── Imprimir bloque formateado ─────────────────────────────
    hora=$(date '+%H:%M:%S')
    echo -e "${color}${BOLD}▶ Fallo en: ${contenedor}${RESET}"
    echo -e "  ${BLUE}Hora:${RESET}     $hora"
    echo -e "  ${BLUE}Nivel:${RESET}    ${color}${nivel}${RESET}"
    [ -n "$http_code" ] && echo -e "  ${BLUE}Código:${RESET}   HTTP $http_code"
    [ -n "$endpoint"  ] && echo -e "  ${BLUE}Endpoint:${RESET} $endpoint"
    [ -n "$excepcion" ] && echo -e "  ${BLUE}Excepción:${RESET}${RED} $excepcion${RESET}"
    echo -e "  ${BLUE}Detalle:${RESET}  $detalle"
    echo -e "  ${CYAN}──────────────────────────────────────────────────${RESET}\n"
done

#!/bin/bash
# Ejecuta los tests unitarios de todos los microservicios HelpTata.
# Uso: ./run-tests.sh

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
BOLD='\033[1m'
RESET='\033[0m'

PASSED=0
FAILED=0
SKIPPED=0
declare -a FALLOS=()

# MS con tests de servicio (excluye msTataBot que no tiene tests reales)
MS=(
  "msUsuarioHelpTata"
  "msLogsHelpTata"
  "msTutorialesHelpTata"
  "msProgresoHelpTata"
  "msDireccionHelpTata"
  "msEvaluacionesHelpTata"
  "msPreguntasYRespuestasHelpTata"
)

echo -e "${BOLD}╔══════════════════════════════════════════════════╗${RESET}"
echo -e "${BOLD}║       Tests Unitarios — HelpTata                 ║${RESET}"
echo -e "${BOLD}╚══════════════════════════════════════════════════╝${RESET}\n"

# ── Limpiar target/ de root (creados por Docker) ─────────────────────────────
# Detecta si hay archivos root dentro de target/ (no solo si el dir es no-escribible)
tiene_archivos_root() {
  find "$1" -user root -maxdepth 4 -print -quit 2>/dev/null | grep -q .
}

TARGETS_ROOT=()
for ms in "${MS[@]}"; do
  DIR="$ROOT_DIR/$ms"
  if [ -d "$DIR/target" ] && ([ ! -w "$DIR/target" ] || tiene_archivos_root "$DIR/target"); then
    TARGETS_ROOT+=("$DIR/target")
  fi
done

if [ ${#TARGETS_ROOT[@]} -gt 0 ]; then
  echo -e "${YELLOW}⚠ Carpetas target/ con archivos de Docker (root) — limpiando...${RESET}"
  if sudo rm -rf "${TARGETS_ROOT[@]}" 2>/dev/null; then
    echo -e "${GREEN}✔ Limpieza completada${RESET}\n"
  else
    echo -e "${RED}✘ No se pudo limpiar automáticamente. Ejecuta primero:${RESET}"
    echo -e "  ${BOLD}sudo rm -rf ms*/target msPreguntasYRespuestasHelpTata/target${RESET}"
    echo -e "  y luego vuelve a ejecutar ./run-tests.sh\n"
    exit 1
  fi
fi

for ms in "${MS[@]}"; do
  DIR="$ROOT_DIR/$ms"
  echo -e "${BLUE}${BOLD}▶ $ms${RESET}"

  if [ ! -d "$DIR" ]; then
    echo -e "  ${YELLOW}⚠ Carpeta no encontrada, saltando...${RESET}\n"
    ((SKIPPED++))
    continue
  fi

  # Usar mvnw si existe, sino mvn global
  if [ -f "$DIR/mvnw" ]; then
    MVN="$DIR/mvnw"
  else
    MVN="mvn"
  fi

  # Ejecutar tests capturando salida (clean evita conflictos con compilaciones previas)
  OUTPUT=$("$MVN" -f "$DIR/pom.xml" clean test -q 2>&1)
  EXIT_CODE=$?

  # Si maven clean falla por archivos root-owned, limpiar con sudo y reintentar
  if [ $EXIT_CODE -ne 0 ] && echo "$OUTPUT" | grep -q "Failed to delete"; then
    echo -e "  ${YELLOW}⚠ Archivos de Docker bloqueando clean — limpiando con sudo...${RESET}"
    sudo rm -rf "$DIR/target" 2>/dev/null
    OUTPUT=$("$MVN" -f "$DIR/pom.xml" clean test -q 2>&1)
    EXIT_CODE=$?
  fi

  if [ $EXIT_CODE -eq 0 ]; then
    # Extraer cantidad de tests del resumen de Maven
    RESUMEN=$(echo "$OUTPUT" | grep -E "Tests run:" | tail -1)
    RUNS=$(echo "$RESUMEN"   | grep -oP 'Tests run: \K[0-9]+')
    ERRORS=$(echo "$RESUMEN" | grep -oP 'Errors: \K[0-9]+')
    FAILS=$(echo "$RESUMEN"  | grep -oP 'Failures: \K[0-9]+')
    echo -e "  ${GREEN}✔ PASÓ${RESET} — ${RUNS:-?} tests, ${FAILS:-0} fallos, ${ERRORS:-0} errores"
    ((PASSED++))
  else
    echo -e "  ${RED}✘ FALLÓ${RESET}"
    # Mostrar solo las líneas de error relevantes
    echo "$OUTPUT" | grep -E "FAILED|ERROR|Tests run:|BUILD" | sed 's/^/    /'
    FALLOS+=("$ms")
    ((FAILED++))
  fi
  echo ""
done

# ── Resumen final ──────────────────────────────────────────────────────────────
echo -e "${BOLD}══════════════════════════════════════════════════${RESET}"
echo -e "${BOLD}  Resultado final${RESET}"
echo -e "${BOLD}══════════════════════════════════════════════════${RESET}"
echo -e "  ${GREEN}Pasaron:${RESET}  $PASSED"
echo -e "  ${RED}Fallaron:${RESET} $FAILED"
[ $SKIPPED -gt 0 ] && echo -e "  ${YELLOW}Saltados:${RESET} $SKIPPED"

if [ ${#FALLOS[@]} -gt 0 ]; then
  echo ""
  echo -e "  ${RED}${BOLD}MS con fallos:${RESET}"
  for f in "${FALLOS[@]}"; do
    echo -e "    ${RED}• $f${RESET}"
  done
  echo ""
  exit 1
else
  echo ""
  echo -e "  ${GREEN}${BOLD}✔ Todos los tests pasaron correctamente${RESET}"
  echo ""
  exit 0
fi

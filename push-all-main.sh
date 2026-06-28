#!/bin/bash

SOURCE_BRANCH="Vicente"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

# formato: "carpeta:rama_principal"
REPOS=(
  "msDireccionHelpTata:main"
  "msEvaluacionesHelpTata:main"
  "msLogsHelpTata:main"
  "msPreguntasYRespuestasHelpTata:master"
  "msProgresoHelpTata:main"
  "msUsuarioHelpTata:main"
  "msTutorialesHelpTata:main"
  "msTataBot:master"
  "helptataFront:main"
)

for entry in "${REPOS[@]}"; do
  DIR="${entry%%:*}"
  TARGET="${entry##*:}"
  FULL_PATH="$ROOT_DIR/$DIR"

  echo ""
  echo "══════════════════════════════════════════"
  echo "  $DIR → $TARGET"
  echo "══════════════════════════════════════════"

  if [ ! -d "$FULL_PATH/.git" ]; then
    echo "  ERROR: no es un repositorio git, saltando."
    continue
  fi

  cd "$FULL_PATH"

  # Crear rama destino si no existe localmente
  if ! git show-ref --quiet refs/heads/$TARGET; then
    echo "  Creando rama $TARGET desde $SOURCE_BRANCH..."
    git checkout -b $TARGET
  else
    git checkout $TARGET
  fi

  # Mergear Vicente en la rama destino
  git merge $SOURCE_BRANCH --no-edit

  if [ $? -ne 0 ]; then
    echo "  ERROR: conflicto al mergear $SOURCE_BRANCH en $TARGET. Resuelve manualmente."
    git merge --abort 2>/dev/null
    git checkout $SOURCE_BRANCH
    continue
  fi

  # Push
  echo "  Pusheando a origin/$TARGET..."
  git push -u origin $TARGET

  if [ $? -eq 0 ]; then
    echo "  OK"
  else
    echo "  ERROR en el push de $DIR"
  fi

  git checkout $SOURCE_BRANCH
done

echo ""
echo "══════════════════════════════════════════"
echo "  Proceso completado."
echo "══════════════════════════════════════════"

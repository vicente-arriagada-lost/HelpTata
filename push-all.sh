#!/bin/bash
# Uso: ./push-all.sh "mensaje del commit"
# Pushea el repo principal (HelpTata) y el sub-repo de msPreguntasYRespuestas.

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
MSG="${1:-actualización general}"

ok()    { echo "  ✔ $1"; }
error() { echo "  ✘ $1"; }
titulo(){ echo ""; echo "══════════════════════════════════════"; echo "  $1"; echo "══════════════════════════════════════"; }

# ── 1. Repo principal HelpTata ─────────────────────────────────────────────────
titulo "HelpTata (repo principal)"
cd "$ROOT_DIR"

git add -A

if git diff --cached --quiet; then
  echo "  Sin cambios nuevos."
else
  git commit -m "$MSG" && ok "Commit creado" || { error "Falló el commit"; exit 1; }
fi

git push origin Vicente && ok "Push a Vicente" || { error "Falló push a Vicente"; exit 1; }

git checkout main
git merge Vicente --no-edit && ok "Merge Vicente → main" || { error "Conflicto en merge"; git merge --abort; git checkout Vicente; exit 1; }
git push origin main && ok "Push a main" || { error "Falló push a main"; }
git checkout Vicente

# ── 2. Sub-repo msPreguntasYRespuestas ────────────────────────────────────────
titulo "msPreguntasYRespuestasHelpTata"
cd "$ROOT_DIR/msPreguntasYRespuestasHelpTata"

git add -A

if git diff --cached --quiet; then
  echo "  Sin cambios nuevos."
else
  git commit -m "$MSG" && ok "Commit creado" || { error "Falló el commit"; }
fi

git push origin master && ok "Push a master" || { error "Falló push a master"; }

# ── Fin ────────────────────────────────────────────────────────────────────────
echo ""
echo "══════════════════════════════════════"
echo "  Completado."
echo "══════════════════════════════════════"

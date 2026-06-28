FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# IP/dominio del servidor — se pasa desde docker-compose como build arg
ARG SERVER_IP=localhost
RUN if [ "$SERVER_IP" = "localhost" ]; then \
  printf "VITE_MS_USUARIO_URL=http://localhost:8080\n\
VITE_MS_TUTORIALES_URL=http://localhost:8082\n\
VITE_MS_PROGRESO_URL=http://localhost:8083\n\
VITE_MS_DIRECCION_URL=http://localhost:8084\n\
VITE_MS_EVALUACIONES_URL=http://localhost:8085\n\
VITE_MS_PREGUNTAS_URL=http://localhost:8086\n\
VITE_MS_TATABOT_URL=http://localhost:8087\n\
VITE_MS_LOGS_URL=http://localhost:8081\n" > .env; \
else \
  printf "VITE_MS_USUARIO_URL=https://%s\n\
VITE_MS_TUTORIALES_URL=https://%s\n\
VITE_MS_PROGRESO_URL=https://%s\n\
VITE_MS_DIRECCION_URL=https://%s\n\
VITE_MS_EVALUACIONES_URL=https://%s\n\
VITE_MS_PREGUNTAS_URL=https://%s\n\
VITE_MS_TATABOT_URL=https://%s\n\
VITE_MS_LOGS_URL=https://%s\n" \
"$SERVER_IP" "$SERVER_IP" "$SERVER_IP" "$SERVER_IP" \
"$SERVER_IP" "$SERVER_IP" "$SERVER_IP" "$SERVER_IP" > .env; \
fi

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 5000

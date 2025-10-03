FROM node:20-alpine AS build
WORKDIR /app
COPY package.json angular.json tsconfig.json ./
RUN npm i --omit=dev
COPY src ./src
RUN npx ng build --configuration production
FROM nginx:alpine
COPY --from=build /app/dist/ /usr/share/nginx/html/
EXPOSE 80

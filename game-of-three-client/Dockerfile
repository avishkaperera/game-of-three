FROM node:alpine
COPY ./package.json .
COPY ./package-lock.json .
RUN npm i
COPY . .
EXPOSE 3000
CMD ["npm", "run", "start"]
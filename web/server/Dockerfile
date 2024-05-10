FROM node:18-alpine

RUN npm install -g nodemon

WORKDIR /app

COPY package.json package-lock.json ./

COPY . .

RUN npm install

RUN npx prisma generate

EXPOSE 4000

CMD ["npm", "start"]
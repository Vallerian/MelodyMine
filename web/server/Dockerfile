# Use the official Node.js image as the base image
FROM node:latest

# Set the working directory in the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install project dependencies
RUN npm ci --only=production

# Copy the rest of the application files to the working directory
COPY . .

# Run Prisma generate
RUN npx prisma generate

# Expose the port the app runs on
EXPOSE 4000

# Command to run the application
CMD ["npm", "start"]

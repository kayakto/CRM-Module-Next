# Select language | Выберите язык
- [English Version](Readme.md)
- [Русская версия](Readme-RU.md)

# Application Location
<p><strong>Backend REST API</strong> is available at:</p>

<a>https://84.252.132.66/api</a>

<p><strong>Swagger Documentation</strong> is available at:</p>

<a>https://84.252.132.66/api/swagger-ui/index.html</a>

# Backend Launch Instructions
1. Clone the repository:
    ```bash
    git clone https://github.com/kayakto/CRM-Module-Uralintern.git
    ```
2. Navigate to the project directory:
    ```bash
    cd CRM-Module-Uralintern
    ```
3. Generate certificates for HTTPS:
   ```bash
   mkdir certificates
   ```
   ```bash
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout certificates/bytebuilders-selfsigned.key -out certificates/bytebuilders-selfsigned.crt
   ```

4. Build and start the application using Docker Compose:
    ```bash
    docker-compose up --build -d
    ```
   
<ul>
    <li>
        <p>The application will be available at:</p>
        <a href="https://localhost/api">https://localhost:8080/api</a>
    </li>
    <li>
        <p>Swagger documentation will be available at:</p>
        <a href="https://localhost/api/swagger-ui/index.html#">https://localhost:8080/api/swagger-ui/index.html#</a>
    </li>
</ul>



5. To stop the application and remove containers, execute one of the following commands:

   ```bash
   docker-compose down
   ```
   or

   ```bash
   docker-compose down --remove-orphans
   ```


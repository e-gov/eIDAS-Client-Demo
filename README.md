# eIDAS-Client-Demo
Web application with UI for eIDAS Client demonstration

1. Download
`git clone https://github.com/e-gov/eIDAS-Client-Demo.git`

2. Build
`./mvnw clean install`

3. Run against eIDAS-Client service
`java -Deidas.client.ws.url="https://localhost:1889" -jar target/eidas-client-demo-webapp-1.0-SNAPSHOT.war`
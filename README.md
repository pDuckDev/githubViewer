

# 🚀 GitHub Repositories Viewer

**Java 21 · Spring Boot 3.5 · Maven**

Aplikacja REST, która dla zadanego użytkownika GitHub zwraca listę publicznych repozytoriów wraz z gałęziami i SHA ostatnich commitów.

---

## 🔗 Endpoint

GET /api/v1/github/users/{username}/repositories


- **200 OK**  
  [
  {
  "repositoryName": "repo",
  "ownerLogin": "user",
  "branches": [
  { "branchName": "main", "lastCommitSha": "abc123" },
  { "branchName": "dev", "lastCommitSha": "def456" }
  ]
  }
  ]

- **404 Not Found**  
  {
  "status": 404,
  "message": "User 'foo' not found"
  }

---

## ⚙️ Konfiguracja

W pliku `src/main/resources/application.yml`:

gitHub:
url: https://api.github.com

server:
port: 8080

- `gitHub.url` — baza URL GitHub API
- `server.port` — port HTTP serwera

---

## ▶️ Uruchomienie

git clone https://github.com/pDuckDev/githubViewer.git

cd githubViewer

mvn clean package

mvn spring-boot:run


**Przykład:**  
curl http://localhost:8080/api/v1/github/users/octocat/repositories


---

## ✅ Testy

Integracyjny test z WireMock sprawdza happy path:

mvn test

---
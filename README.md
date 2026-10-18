# Job Portal (Spring Boot)

A full-stack Job Portal where **employers** post jobs and **job seekers** search and apply. Built with Java, Spring Boot, Spring Security and MySQL.

## Features
- User registration and login with **BCrypt** password hashing
- **Role-based access control** (Spring Security): `EMPLOYER` and `JOB_SEEKER`
- Employers: add, edit and delete jobs
- Job seekers: search jobs by title, company or location, and apply (duplicate applications are blocked)
- Session-based authentication, protected REST APIs
- Simple HTML/JavaScript frontend

## Tech Stack
Java 17 | Spring Boot | Spring Security | Spring Data JPA (Hibernate) | MySQL | Maven | HTML, CSS, JavaScript

## REST API
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/register` | Public |
| POST | `/login` | Public |
| POST | `/api/logout` | Logged in |
| GET | `/jobs` | Public |
| POST | `/jobs` | Employer |
| PUT | `/jobs/{id}` | Employer |
| DELETE | `/jobs/{id}` | Employer |
| POST | `/apply` | Job Seeker |
| GET | `/applications/me` | Job Seeker |

## Run Locally
1. Install JDK 17+ and MySQL
2. Clone the repo:
   `git clone https://github.com/navaneetha-123/job-portal-springboot.git`
3. Copy `src/main/resources/application.properties.example` to `application.properties` and set your MySQL password
4. Run `JobportalApplication` (or `./mvnw spring-boot:run`)
5. Open `http://localhost:9090/register.html`

## Author
Navaneetha Poola
## Screenshots
<img width="603" height="335" alt="Jobseeker" src="https://github.com/user-attachments/assets/c4f1bfc3-eb6a-49fe-bc64-2f6a23fb4976" />
<img width="631" height="235" alt="employer" src="https://github.com/user-attachments/assets/75b08163-abdb-4762-8c1a-eb05fc51846b" />


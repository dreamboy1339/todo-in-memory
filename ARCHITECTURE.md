# ARCHITECTURE

## 목적
인메모리 TODO API의 현재 코드 구조와 런타임 흐름을 정리합니다.

## 기술 스택
- Spring Boot 3.5
- Java 21
- Gradle
- springdoc-openapi (Swagger UI)

## 패키지 구조
- com.asdf.todo
  - TodoApplication: Spring Boot 진입점
  - TodoInMemoryApplication: 빈 플레이스홀더 클래스
  - config
    - ApiDocumentationConfig: OpenAPI 메타데이터 Bean
  - controller
    - TodoController: TODO REST 엔드포인트
  - model
    - Todo: 데이터 모델
  - repository
    - TodoInMemoryRepository: 인메모리 저장소
  - service
    - TodoService: 비즈니스 로직 계층

## 런타임 흐름
1. HTTP 요청이 `/api/todos/v1` 경로의 `TodoController`에 도착합니다.
2. 컨트롤러가 `TodoService`로 위임합니다.
3. 서비스가 `TodoInMemoryRepository`로 위임합니다.
4. 리포지토리가 `HashMap<Long, Todo>`와 `AtomicLong` ID 카운터로 데이터를 저장합니다.

## API 표면
- GET `/api/todos/v1` -> 200(목록), 비어있으면 204
- GET `/api/todos/v1/{id}` -> 200(단건), 없으면 404
- POST `/api/todos/v1` -> 201(생성)
- PUT `/api/todos/v1/{id}` -> 200(수정), 없으면 404
- DELETE `/api/todos/v1/{id}` -> 204(삭제), 없으면 404

## 데이터 모델
`Todo` 필드:
- id: Long
- title: String (필수)
- description: String
- completed: boolean

## 구성
- `ApiDocumentationConfig`가 Swagger UI용 OpenAPI 정보를 노출합니다.
- `application.properties`에 `spring.application.name=todo`가 설정되어 있습니다.

## 테스트
- Controller 테스트: `TodoControllerTests`
- Service 테스트: `TodoServiceTests`
- Spring Boot 컨텍스트 테스트: `TodoApplicationTests`

## 참고
- 저장소는 인메모리이며, 재시작 시 데이터가 사라집니다.
- `TodoInMemoryRepository`는 동기화되지 않은 `HashMap`을 사용합니다.

## 안드로이드 개발 관점 비교
- Controller는 안드로이드의 UI 진입점(Activity/Fragment) 대신 서버 측 HTTP 엔드포인트에 해당합니다.
- Service는 도메인 로직을 캡슐화한 UseCase/Interactor와 유사하며, ViewModel 아래 계층에 해당합니다.
- Repository는 Android의 Repository 패턴과 동일한 역할이며, 현재는 In-Memory DataSource와 유사합니다.
- In-Memory 저장소는 Room/SQLite 같은 영속 저장소로 교체 가능한 구조입니다.
- `application.properties` 기반 설정은 Android의 Gradle 설정 및 AppConfig 초기화와 유사한 위치에 있습니다.

## MVVM 흐름 대비
- Android: View(Activity/Fragment) -> ViewModel -> UseCase/Repository -> DataSource
- Spring: Controller -> Service -> Repository -> In-Memory Store
- ViewModel의 상태 관리와 유사하게, Controller는 요청/응답 조합과 상태 전이를 담당합니다.

## DataSource 분리 관점
- 현재 Repository는 단일 In-Memory 구현체입니다.
- Android에서 LocalDataSource/RemoteDataSource를 두듯이, 추후 DB/외부 API로 분리하기 용이합니다.
- 인터페이스 기반 Repository를 두면 교체 비용이 줄어듭니다.

## 테스트 전략 비교
- Controller 테스트는 Android의 UI 계층 테스트(로컬)와 유사합니다.
- Service 테스트는 ViewModel/UseCase 유닛 테스트와 대응됩니다.
- Repository 테스트는 DataSource 단위 테스트와 대응됩니다.

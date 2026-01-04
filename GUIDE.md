# 학습 가이드: Mockito any() 이해

## 문서 확인
- 확인한 위치: https://docs.spring.io/spring-boot/3.5/reference/testing/index.html
- 확인한 위치: https://docs.spring.io/spring-boot/3.5/reference/
- 결과: 해당 페이지에서 Mockito의 `ArgumentMatchers`나 `any()`에 대한 직접 설명은 찾지 못했습니다.
- 확인한 위치: https://javadoc.io/doc/org.mockito/mockito-core/5.21.0/org.mockito/org/mockito/ArgumentMatchers.html
- 결과: `any(Class)`/`anyXxx`는 타입 체크로 `null`을 매칭하지 않으며, `null` 매칭에는 `nullable(Class)`을 사용하도록 안내합니다.

## 목표
- 테스트 코드에서 `any()`의 역할을 이해하고 사용 의도를 파악합니다.

## 핵심 개념
- `any()`는 Mockito의 `ArgumentMatchers`로, **지정한 타입이면 어떤 값이든 매칭**합니다.
- 인자 값보다 **호출 자체**에 집중해 스텁/검증을 구성할 때 사용합니다.

## 프로젝트 코드 적용
- 위치: `src/test/java/com/asdf/todo/controller/TodoControllerTests.java`
- 사용 예
  - `given(todoService.save(any(Todo.class))).willReturn(todo);`
  - `given(todoService.update(anyLong(), any(Todo.class))).willReturn(updatedTodo);`

## 의미
- `save(any(Todo.class))`: 어떤 `Todo`가 와도 동일 결과를 반환하도록 스텁.
- `update(anyLong(), any(Todo.class))`: `id`와 `Todo`가 무엇이든 동일 결과를 반환하도록 스텁.
- 컨트롤러의 HTTP 응답 로직에 집중하기 위한 단순화 목적입니다.

## 주의사항
- 매처 사용 시 모든 인자를 매처로 통일해야 합니다.
  - 예: `update(1L, any(Todo.class))`는 예외를 유발할 수 있어 `anyLong()`로 통일.
- `any(Class)`/`anyXxx`는 타입 체크로 `null`을 매칭하지 않습니다.
- `null` 허용이 필요하면 `nullable()` 같은 매처를 고려합니다.

## 매처 비교 요약
- `any(Class)`: 타입만 맞으면 어떤 값이든 매칭합니다. `null`은 매칭되지 않습니다.
- `eq(value)`: 값이 정확히 같을 때 매칭합니다. 다른 매처와 함께 쓸 때 유용합니다.
- `argThat(matcher)`: 커스텀 조건으로 매칭합니다. 복잡한 조건에 사용합니다.
- `nullable(Class)`: 해당 타입에서 `null`을 허용해 매칭합니다.

## 프로젝트 맞춤 예시
- 컨트롤러 테스트에서 인자 내용을 더 엄격히 보려면 `argThat`를 사용합니다.
  - 예: 제목이 비어있지 않은 `Todo`만 매칭하도록 조건을 추가할 수 있습니다.
- 부분적으로 값 검증이 필요하면 `eq`와 조합합니다.
  - 예: `update(eq(1L), any(Todo.class))`처럼 특정 ID를 고정합니다.
- `null` 입력을 허용하는 케이스가 생기면 `nullable(Todo.class)`로 스텁합니다.

## 요약
- `any()`는 인자 값을 무시하고 호출을 매칭하는 Mockito 매처입니다.
- 이 프로젝트에서는 컨트롤러 테스트에서 서비스 호출을 단순화하기 위해 사용되고 있습니다.

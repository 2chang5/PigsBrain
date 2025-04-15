# [Compose의 sideEffect](https://developer.android.com/develop/ui/compose/side-effects?hl=ko)

컴포즈 함수형 패러다임 적용
실제 UI 표시 제외 외부 상태변경 sideEffect 이거 함수형 개념좀 참고필요 

## 상태 및 효과 사용 사례
앱 상태 변경 하려는경우 사이드 이펙트가(사람이 생각하는 범위내에서 실행되도록)-> 막 중간취소, 멀티스레드 환경
안드로이드에서 제공하는 Effect API를 사용해라

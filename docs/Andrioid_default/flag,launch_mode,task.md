# flag, launch mode

Task 및 스택 관련 [공식문서](https://developer.android.com/guide/components/activities/tasks-and-back-stack)의 핵심을 정리한 글 입니다.

### 진입점 액티비티에서(루트 런처)에서 백버튼 동작

intentFilter 중

- ACTION_MAIN
- CATEGORY_LAUNCHER

이 두개가 잡혀있는 액티비티(시작점)의 뒤로가기 동작은 버전에 따라 동작이 다름

- 11 이하
  - 액티비티 피니시해서 날려버림
- 12 이상
  - 태스크와 시작점 액티비티를 백그라운드로 놔서 다음번 실행에 warm start를 할수있게함
  - 아예 새로운시작: cold start, 살아있는거 다시키는거 warm start  
<br>
<br>
이 때문에 onBackPressed 직접 오버라이드 하는것보다 AndroidX Activity Apis 권장한다.

-> 안드로이드 13(33)부터 deprecated 되었다.

OnBackPressedDispatcher를 쓰자
<br>
<br>
## Manage tasks 

이제 주 관심사다 태스크 내 액티비티 인스턴스 관리하는 방법이다.

인스턴스 관리하는 방법은 두가지라고 나와있다.

- manifest 내 attributes 를 사용하는 방법
- StartActivity() 에 붙여주는 intent flag

해당 두가지 방법 모두 설정한 경우 플래그로 설정한것이 우선순위가 더 높다.
<br>
<br>
### Manifest file 을 이용한 launchMode 설정

#### 1. standard

디폴트로 잡혀있는것

별다른 제약없이 인스턴스가 생성되는 형태이다.

#### 2. singleTop

현 태스크 스택내 최상위에 있는 경우에만 적용되는 룰

- 태스크 내 여러 인스턴스 존재 가능
- 태스크 최상위 인스턴스가 동일한 액티비티일 경우에는 새로운 인스턴스 생성이 아닌 onNewIntent를 통해 기존 인스턴스 유지

예시)

A-B-C-D / D실행

A-B-C-D 유지하지만 onNewIntent() 실행 / 생명주기 실제로 호출해보면 onPause -> onNewIntent -> onPause

참고: 그냥 액티비티 실행시 onNewIntent 콜백은 실행안됨
<br>
<br>

🚨주의 / 안드로이드 14부터 매개변수가 두개인 콜백이 존재하는데 실제로 로그찍어보면 이게아니라

```kotlin
override fun onNewIntent(intent: Intent, caller: ComponentCaller)
```

매개변수 하나짜리 밑에게 호출됨

```kotlin
override fun onNewIntent(intent: Intent)
```
<br>

#### 3. singleTask

사전지식: taskAffinity

태스크당 고유 id라고 생각하면된다 / 기본값은 패키지명

taskAffinity를 manifest에서 따로 지정하면 해당 액티비티는 지정한 id값을 가진 태스크에서 별도 실행된다.
<br>
<br>

인스턴스 동시존재 여부: 인스턴스는 하나만 존재하게됨

동작: 새로운 태스크를 열어 루트로 만들거나(해당 액티비티가 기존과 다른 taskAffinity를 가진경우)

동일한 affinity를 가진 task 내부에서 해당 인스턴스와의 액티비티를 모두 꺼버리고 해당 인스턴스를 스택 가장위쪽에 표시하며 onNewIntent를 호출한다.

A- B(SingleTask)-C-D  인 상황에서 B 호출시 

A-B상태로 스택이 조정된다.
<br>
<br>

#### 4. singleInstance

해당 설정을 하면 해당 액티비티는 무조건 본인만의 태스크를 열고 그안에서만 존재함

또한 재활용시 onNewIntent 콜백 호출

ex) A -> B(singleInstance) -> C

A: A Task, B: B Task(singleinstance여서), C: C Task(B로 부터 불려서)

ex) ex) A -> B(singleInstance) -> C ->B

A: A Task, B: B Task(재활용되어서 가장위로 포커스됨), C: C Task(B로 부터 불려서)
<br>
<br>

#### 5. singleInstancePerTask

기본적으로 singleTask랑 기능이 같지만(중간에 있는 액티비티 날려버리는것까지)

FLAG_ACTIVITY_MULTIPLE_TASK 또는 FLAG_ACTIVITY_NEW_DOCUMENT 플래그가 설정시 여러 태스크에 여러 인스턴스로 존재 가능하다.

- FLAG_ACTIVITY_MULTIPLE_TASK

  - 기본적으로 singleTask, singleInstance는 하나의 인스턴스만 허용되지만

    이 플래그를 사용하면 **같은 액티비티를 여러 개 Task에서 중복 실행할 수 있게 허용**합니다.

- FLAG_ACTIVITY_NEW_DOCUMENT

  - 멀티 윈도우, Recent 지원용이라는데 잘 안쓸거같음

뭐 그렇다는데 사실 이건 잘 안쓸거같다.
<br>
<br>


### 플래그를 이용한 launchMode 설정

#### 1. [`FLAG_ACTIVITY_NEW_TASK`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_NEW_TASK)

공식문서에는 간결하게 나와있는데 singleTask 랑 좀 다른데 왜 같다고 하는지 모르겠다.

예시로 어떻게 동작하는지 봐보자

- 현재 포그라운드 태스크에 없는경우

A - B  / C를 FLAG_ACTIVITY_NEW_TASK를 붙여서 실행

A - B - C / A, B는 같은 태스크 / C는 새로운 태스크

onNewIntent가 아닌 그냥 생명주기 탐

- 현재 포그라운트 태스크인 경우

A - B - C /  B를 FLAG_ACTIVITY_NEW_TASK를 붙여서 실행

아무일도 일어나지 않음 onNewIntent도 호출 안됨

- 백그라운드 태스크인 경우

A - B - C / B를 FLAG_ACTIVITY_NEW_TASK를 붙여서 실행

화면에는 C가 표시되고 해당태스크가 포그라운드로 바뀜

B는 onNewIntent가 호출됨
<br>
<br>


#### 2. [`FLAG_ACTIVITY_SINGLE_TOP`](https://developer.android.com/reference/android/content/Intent?hl=ko#FLAG_ACTIVITY_SINGLE_TOP)

진짜 singletop이랑 동작이 같음 최상단에 있을때만 onNewIntent와 함께 인스턴스 재활용함
<br>
<br>

#### 3. [`FLAG_ACTIVITY_CLEAR_TOP`](https://developer.android.com/reference/android/content/Intent#FLAG_ACTIVITY_CLEAR_TOP)

이미 태스크 안에 액티비티가 실행되고 있으면 중간거 다 clear하면서 onNewIntent 호출하면서 기존인스턴스 가장위로 올리는거임

FLAG_ACTIVTY_NEW_TASK랑 같이 조합해서 많이 사용하는데 다른 태스크에서 해당 액티비티를 찾아서 처리하도록 하기때문이라함
<br>
<br>

참고: launchmode가  standard 인 액티비티에 적용하는경우

onNewIntent호출되며 인스턴스 재활용하는게 아닌

기존 액티비티 날리고 새로운액티비티 생명주기 새로타면서 중간에 스택을 날리는 형태가됨

-> standard의 기본 동작이 startActivity마다 새로운 액티비티 인스턴스를 생성하기 때문임
<br>
<br>

이거 말고도 플래그 많은데 해당 문서에서는 다루지 않아 밑에서 추가하겠다.
<br>
<br>

### Handle affinities

서로 다른 앱도 동일한 task Affinity를 다룰수 있고 같은 task로 다뤄질 수 있음
<br>
<br>

**FLAG_ACTIVITY_NEW_TASK** 을 사용할때 새로 키는 액티비티의 affinity와 같은 task affinity를 가진 task가 존재한다면

새로운 태스크를 여는것이 아닌 거기에 붙음 
<br>
<br>

[`allowTaskReparenting`](https://developer.android.com/guide/topics/manifest/activity-element#reparent) 라는 속성이 있는데 이 속성을 키면 (manifest에서 설정) 태스크를 옮겨 다닐수 있다고 한다.

이설정이 켜져있는 상태에서 다른앱에서 이 액티비티를 키고(다른 태스크에 들어감) 그러고 우리앱을 키면(affinity가 같은) 우리쪽 태스크의 가장 상단으로 이사온다고 한다.

특수한 상황이 아니면 안쓸거같다. 
<br>
<br>

### Clear the back stack

사용자가 태스크를 오래 떠나있으면 시스템이 루트 액티비티를 제외하고 다 날려버린다고 한다.

사용자가 오래비움 ==  새로 작업을 시작 하는 의미에서 라는데(자원관리 차원도 포함 일것같다.)

어쩃든 이렇게 시스템 상에서 날리는 행위를 조정할수있는 속성이 있다고 한다.
<br>
<br>

#### [`alwaysRetainTaskState`](https://developer.android.com/guide/topics/manifest/activity-element#always)

해당 속성이 태스크 루트 액티비티에 설정되어있다면 

유저가 오래 떠나있어도 액티비티 제거가 이루어지지 않는다고 한다.
<br>
<br>

#### [`clearTaskOnLaunch`](https://developer.android.com/guide/topics/manifest/activity-element#clear)

해당 속성이 설정되어있으면 시간이 오래안지나도 태스크를 떠났다가 돌아오면 그냥 루트 빼고 다 날려버린다.

intent에  FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 플래그를 같이 설정해줘야 동작한다고 한다.
<br>
<br>

#### [`finishOnTaskLaunch`](https://developer.android.com/guide/topics/manifest/activity-element#finish)

액티비티에 적용하는건데 민감정보 같은거 해당 태스크를 떠나는순간 액티비티꺼버리는 용도로 사용(쓸만할듯)

이 또한 intent에  FLAG_ACTIVITY_RESET_TASK_IF_NEEDED 플래그를 같이 설정해줘야 동작한다고 한다.
<br>
<br>
<br>
<br>

## 추가사항

Flag는 위에서 다룬거 말고 겁나 많다. 쓸만한것들을 정리해보았다.([출처](https://medium.com/@logishudson0218/intent-flag%EC%97%90-%EB%8C%80%ED%95%9C-%EC%9D%B4%ED%95%B4-d8c91ddd3bfc))

- **FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS**

**최근 실행목록에 표시하지 않길 원하는** Activity가 있을 경우 사용하면 표시되지 않음

- **FLAG_ACTIVITY_FORWARD_RESULT**

startActivityForResult를 이용해서 Activity를 호출한 이후, 호출하는 쪽이 아닌 한번 더 거쳐서 Result를 받고 싶은 경우에 쓰임

Ex) A → B → C , C에서 setResult 설정, B에서 finish 하게 되면 A → C 로 값을 받을 수 있게 됨

A → B → C → D도 전달가능(중간에 다 액티비티 킬때마다 flag넣어줘야함)

- **FLAG_ACTIVITY_NO_ANIMATION :** Activity 전환 시 애니메이션 무시
- **FLAG_ACTIVITY_NO_HISTORY :** Activity가 **Stack에 쌓이지 않게 함**, 로딩 화면(SplashActivity) 등에 사용

- **FLAG_ACTIVITY_REORDER_TO_FRONT**

실행하고자 하는 Activity가 Task에 있으면생성하는 대신 순서를 가장 위로 올림

순서를 역전해서 올리기 때문에 잘못 사용하게 되면 Activity의 흐름이 꼬일 수 있는 문제점이 있다.


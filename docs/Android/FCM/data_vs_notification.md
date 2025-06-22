# Fcm 관련된 내용을 학습하며 정리한 요약 문서입니다.([공식문서](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*15agoq2*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw))
메시지에 담아 보내는 Json을 기준으로 구분한 내용을 담고있습니다.  
Notification, Data, Notification+data 타입에 대한 내용이 주를 이룹니다.  

### FCM 아키텍처 개요

<img width="821" alt="Image" src="https://github.com/user-attachments/assets/b3d7bdad-3203-4059-8a90-a37dca53e2e9" />

- 1번 영역: 파이어베이스 콘솔을 통한 FCM GUI 컨트롤 환경 조성(Notifiaction composer) Optional한 기능
- 붉은색 상자 영역: 서버가 관리하는 영역 -> Platfotm-level message transport layer 또한 각 플렛폼별 서버 조정로직 레이어
- 연두색 상자영역: 클라이언트(안드로이드) 개발자가 신경써야하는 영역

### FCM 메시지 클라이언트 차원의 구분
- Notification messages
- Data messages
- Data + Notification 혼합 messages

#### 메시지 타입별 차이

기본적인 개념: 각 타입을 구분하는데 두가지 관점으로 바라보면 된다.
Notification 자동처리, 전달되는 JSON 내 Notification/Data를 이용하는지


| 구분 | Notification 메시지 | Data 메시지 |
|------|----------------------|-------------|
| **기본 Notificatio(안드로이드) 처리** | ✅ 처리함 (백그라운드일 때만) | ❌ 처리하지않음 (직접 처리 필요) |
| **커스텀 키-값 전달** | 🔸 가능한데 data를 섞어쓰는 형태 (data 필드에 추가 가능) | ✅ 가능 |
| **언제 사용하나?** | 단순 푸시 (예: 공지사항, 마케팅 메시지) | 사용자 맞춤 처리 필요시 (예: 채팅, 딥링크, 내부 처리 로직 등) |

[기본 Notification(안드로이드) 처리]

- Notification(FCM)
    - 백그라운드
        - FCM에서 기본적으로 Notification 처리됨 -> 커스터마이징 불가
        - onMessageReceived() 호출 되지 않음
    - 포그라운드 
        - 기본 Notification 처리되지않음
        -  onMessageReceived() 호출됨 직점 notification 처리해야함

- Data
    - 백그라운드, 포그라운드 동일 
    - 기본 Notification 처리 없음
    - onMessageReceived를 통해 데이터를 받아서 notification 처리를 통해 메시지 표출해야함 

- Notification + Data 
    - 백그라운드
        - FCM에서 기본적으로 Notification 처리됨 -> 커스터마이징 불가
        - onMessageReceived 호출되지 않음
        - data에 포함된 데이터들 Intent에 전달 -> Notification을 통해 진입한 Activity내에서 getExtra를 통해서 데이터 전달 받을 수 있음([관련문서](https://firebase.google.com/docs/cloud-messaging/android/receive?utm_source=chatgpt.com#handling_messages))[주석 이미지](https://github.com/2chang5/PigsBrain/blob/main/imageRes/FCM_%E1%84%8C%E1%85%AE%E1%84%89%E1%85%A5%E1%86%A8_%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5_1.png) 참고
    - 포그라운드
        - 기본 Notification 처리되지않음
        -  onMessageReceived() 호출됨 직점 notification 처리해야함
        - data, notification 내 데이터 전부 접근 가능

상황에 맞춰 써야겠지만 Data 형식으로 직접 모든 부분을 커스텀해서 사용하는것이 실상황에서는 맞을것으로 판단됨

### Notification
담아서 전달할 수 있는 데이터가 FCM 에서 사전 정의된 내용밖에 없음 
해당 Key value 값은 3가지임 ([관련 문서](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?hl=ko&_gl=1*r38dy0*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#notification))
- title
- body
- image

또한[ 해당 문서](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages?hl=ko&_gl=1*w4etmz*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA1NzQ4NDIkbzIkZzAkdDE3NTA1NzQ4NDIkajYwJGwwJGgw)에 나와있는 
- AndoridConfig(안드로이드 플렛폼 개별설정-> IOS,Web과 구별되는 설정)
- AndroidNotification(안드로이드 노티피케이션 띄우는 설정)
을 통해서 설정을 해줄수는 있지만 Notification Key 내부에 들어가는 데이터는 아님

-> AndoridConfig,AndroidNotification 설정은 data 타입에서도 활용가능

관련 JSON 예시
```json
// notification 예시
{
  "message": {
    "token": "YOUR_FCM_DEVICE_TOKEN",

    "notification": {
      "title": "🔥 핫세일 알림",
      "body": "지금 들어오면 50% 쿠폰!"
    },

    "android": {
      "notification": {
        "icon": "ic_stat_promo",
        "color": "#FF4081",
        "sound": "default",
        "click_action": "OPEN_PROMO",
        "channel_id": "promo_channel",
        "priority": "PRIORITY_HIGH",
        "visibility": "PUBLIC",
        "vibrate_timings": ["0s", "300ms", "200ms", "300ms"],
        "light_settings": {
          "color": { "red": 1.0, "green": 0.5, "blue": 0.5, "alpha": 1.0 },
          "light_on_duration": "2s",
          "light_off_duration": "1s"
        }
      }
    }
  }
}
```

```json
// data 예시
{
  "message": {
    "token": "YOUR_FCM_DEVICE_TOKEN",

    "data": {
      "deep_link": "cashwalk://promo?id=1234",
      "promo_id": "abcd1234"
    },

    "android": {
      "notification": {
        "icon": "ic_stat_data",
        "channel_id": "data_channel",
        "click_action": "OPEN_DEEP_LINK"
      }
    }
  }
}
```


### Data 
내부에 담기는 데이터들 맘대로 설정 가능 -> 서버에서 보내주는것 알아서 클라에서 해석해서 쓰면됨
하지만 예약어는 쓰지 않도록 주의해야함

![Image](https://github.com/user-attachments/assets/58934313-eb9e-46df-8159-fb1e74f6981d)

### 플렛폼 메시지 맞춤 설정[(관련문서)](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*9mg4nv*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#customizing-a-message-across-platforms)
위쪽에서 언급했지만 안드, IOS, Web 모두 각각의 설정을 담을수있는 Json key value 가 있음. 
-> 안드로이드의 경우 Aroid Config, Android Notification

### 메시지 우선순위 설정
```json
{
  "message":{
    "topic":"subscriber-updates",
    "notification":{
      "body" : "This week's edition is now available.",
      "title" : "NewsMagazine.com",
    },
    "data" : {
      "volume" : "3.21.15",
      "contents" : "http://www.news-magazine.com/world-week/21659772"
    },
    "android":{
      "priority":"normal"
    },
    "apns":{
      "headers":{
        "apns-priority":"5"
      }
    },
    "webpush": {
      "headers": {
        "Urgency": "high"
      }
    }
  }
}
```

다음과 같이 설정하는 Json 영역이 있지만 사실상 data 사용해서 커스텀하면 그냥 안드로이드 내에서 처리하면 됨

### 🚨제한 및 할당량
사실 사이드에서는 필요없지만 실무라면 이 리미트에 걸릴 가능성 있음
항상 참고하고 사용하자
[관련 문서
](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*9mg4nv*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#throttling-and-scaling)


#### 생략사항(필수 사항아니라 해당 문서내 생략):
- [비축소형 메시지 및 축소형 메시지](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*9mg4nv*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#collapsible_and_non-collapsible_messages)
- [메시지 수명 설정(메시지 기한설정)](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*9mg4nv*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#ttl)
- [메시지 수명](https://firebase.google.com/docs/cloud-messaging/concept-options?hl=ko&_gl=1*9mg4nv*_up*MQ..*_ga*MTM5OTgwNjkzMS4xNzUwNDg2NjI2*_ga_CW55HF8NVT*czE3NTA0ODY2MjYkbzEkZzAkdDE3NTA0ODY2MzMkajUzJGwwJGgw#lifetime)

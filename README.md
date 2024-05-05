# webflux-coroutines-file-move
webflux + coroutines을 이용한 파일 이동기 만들어보기

### 📁 데이터 세팅 

**1. 디렉토리 세팅**

```
# root 디렉토리에서
$mkdir storage
$cd storage
$mkdir 01
$mkdir 01
```

**2. 서버 가동**

**3. 데이터 세팅**

**[GET]** http://localhost:8080/mock/data 1000개의 데이터**(각 3MB 파일)**가 `storage/01`로 복사된다.

<br><br>

## 🗓️ 데이터 검증

|데이터 개수|Blocking Time|
|------|---|
|4,000 개|설정 안함|

|종류|Running Time|
|------|---|
|webflux + blocking|380(ms)|
|webflux + coroutine async|224(ms)|

<br><br>

|데이터 개수|Blocking Time|
|------|---|
|4,000 개|10 (ms)|

|종류|Running Time|
|------|---|
|webflux + blocking|25(s) 944(ms)|
|webflux + coroutine async|491 (ms)|

<br><br>

|데이터 개수|Blocking Time|
|------|---|
|4,000 개|1000 (ms)|

|종류|Running Time|
|------|---|
|webflux + blocking|1초씩 걸려서 4,000(s)로 추정|
|webflux + coroutine async|31(s) 574(ms)|






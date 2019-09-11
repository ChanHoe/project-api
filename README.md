# project-api

**개발 프레임워크 및 빌드/실행**

- 개발환경
  - Java 1.8
  - Spring Boot 2.1.8
  - jpa 
  - lombok
  - maven

- Build & Excute
​	`maven build && java -jar project-api-0.0.1-SNAPSHOT.jar`


## 1. CSV File Read

```
1. csv 파일을 resources 디렉토리 하위에 위치 시킨다.(해당 과제에서만 유요함)
2. csv 파일의 위치를 classPathResource 를 통해 알아온다. 
3. java.io 의 File 클래스를 통해  File 객체를 생성한다. 
4. file 객체를 읽어드릴때 jackson.dataformat 의 csv mapper를 활용하여 object에 맵핑시킨다.
5. csv 파일을 통해 읽어드린 정보를 CSVData라는 객체 List에 담는다.  
```

## 2. Entity 생성 및 레코드 등록

```
1. 지원정보 엔티티와 기관 엔티티로 분리
2. 기관 엔티티와 지원정보 엔티티간의 관계를 1:N 으로 임의 정의
3. CSVData 리스트를 순환하며 각 엔티티에 맞는 정보를 셋팅한다.
4. 정보셋팅시 지원금액 항목과 이차지원 항목은 정렬 조건으로 사용하기 위해 각 항목에 대한 변환 값 저장용 컬럼을 추가
 - 지원금액 : 한글 화폐단위를 숫자로 변환(추천금액은 최대값으로 셋팅)
 - 이차지원 : 지원율의 평균율을 구하여 저장(전액은 100%로 셋팅)  
```


## 3. API 정보

### 3. 1. 기본정보

- 데이터 타입 : JSON 

| 구분    | 내용             | 비고                             |
| :------ | :--------------- | :---------------------------- |
| result    | 결과                 | 성공 : success, 실패 : fail       |
| message | 메시지           | 실패시 실패사유                        |
| data    | 요청에 대한 내용 |                                  |


### 3. 2. API 목록

#### 3.2.1. 데이터 생성

> URL : /api/info/create
>
> Method : POST
>

#### Parameter 

| 없음  |


#### 3.2.2. 전체 지원정보 데이터 조회

> URL : /api/info/search/all
>
> Method : GET
>

#### Parameter 

| 없음  |

#### 3.2.3. 지자체명을 통한 지원정보 데이터 조회

> URL : /api/info/search/region
>
> Method : GET
>

#### Parameter

| 구분     | Type   | 필수여부 |
| ------ | ------ | -------- |
| region | String | Y        |

#### 3.2.4. 지자체 정보 수정

> URL : /api/info/modify
>
> Method : PUT
>

#### Parameter

| 구분          | Type   | 필수여부 |
| ---------- | ------ | -------- |
| region      | String | Y         |
| target      | String | N         |
| usage      | String | N         |
| limit         | String | N         |
| rate         | String | N         |
| insititute   | String | N         |
| mgmt      | String | N         |
| reception | String | N         |

#### 3.2.5. 지원금액 내림차순(동일시 이차보전 오름차순)으로 특정 개수 지자체명 조회

> URL : /api/info/sort/region
>
> Method : GET
>

#### Parameter

| 구분     | Type   | 필수여부 |
| ------ | ------  | -------- |
| size   | int       | Y        |

#### 3.2.6. 이차보전 비율이 가장 작은 추천 기관명 조회

> URL : /api/info/atleast/rate
>
> Method : GET
>

#### Parameter

| 없음  |
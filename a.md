## 서론

이 문서는 이동욱님 저서 **스프링 부트와 AWS로 혼자 구현하는 웹 서비스** 를 기준으로 작성하였습니다. <br>
<br>
Travis의 유료화로 인해, 기본 요금을 사용하려면 신용카드를 등록해야 하는 번거로움과, 1달 이후 유료로 전환이 되어 새로운 방법을 찾아야 했고, 다른 프로젝트에서도  GitHub Action을 이용해 사용하기 위해 고민하다 작성합니다.<br>
<br>
gitHub Action은 처음 사용해보고, 배워가는 단계라서 틀린 부분이 많을 수 있습니다.
해당 문서는 **동작** 에 핵심을 두었고, 자세한 내용은 추가로 포스팅 하여 첨부할 예정입니다. 
<br>
Github Actions 는 개인 Github 코드 저장소에서 빌드 / 배포 환경을 구축할 수 있는 도구입니다. <br>
public 저장소에서는 무료로 지원합니다.<br>
<br>

해당 버전은 
Date : 2022.07.06 
JAVA 11
Spring Boot 2.4.1
Gradle 7.4
기준으로 작성하였습니다. 버전 문제로 진행되지 않을 수 있습니다.

## GitHub Actions 연동하기

Page 323 ~ 324
해당 내용은 위에 서술한 바와 같이 Travis의 유료화로 진행하지 않습니다.

Page 325 ~ 327 

해당하는 레파지토리에서 Actions 를 선택한 후 Publish Java Package with Gradle - Configure을 선택합니다.
위에 set up a workflow yourself를 선택해도 괜찮습니다.
<br>

![new gitHub Action](https://user-images.githubusercontent.com/104341003/177378726-5f114575-fe17-46e6-ab91-c1b5792928b1.png)

<br>
선택했으면, 다음과 같이 작성합니다. 

```C
name: deploy # (0) GitHub Action에서 보여질 이름을 지정합니다. 

on:
  release:
    types: [push] # (1) push시 자동으로 실행됩니다.
  push:
    branches: [main] # (2) main 브랜치에서 동작합니다.
  workflow_dispatch: # (3) 수동으로도 실행이 가능힙니다.


jobs:
  build: 
    runs-on: ubuntu-latest # (4) 해당 스크립트를 작동할 OS 입니다.
    permissions: 
      contents: read 
      packages: write

    steps:
    - name: Checkout 
      uses: actions/checkout@v3  # (5) 프로젝트 코드를 CheckOut합니다.    
      
    - name: Set up JDK 11 
      uses: actions/setup-java@v3 # (6) 
      with:
        java-version: '11' 
        distribution: 'temurin' 
        
    - name: Grant execute permission for gradlew
      run: chmod +x ./gradlew # (7)
      shell: bash

    - name: Build with Gradle
      run: ./gradlew clean build # (8)
      shell: bash
```

(#0) ```name: deploy```
* GitHub Actions에서 보여질 이름을 지정합니다.

(#1)  ```on: release: types: [push]```
* Action을 실행하게 할 방법을 지정합니다. 
 
(#2) ```(on:) push:branches: [main]```
* 현재 만드는 Github Action의 트리거 브랜치를 지정합니다.

> (#1) (#2)```push``` : main 브랜치가 푸쉬되면 해당 Action을 실행합니다.

(#3) ```workflow_dispatch:``` 
* 수동으로도 실행이 가능합니다.

(#4) ```jobs: build: runs-on: ubuntu-latest```
* 해당 Github Action 스크립트가 작동될 OS 환경을 지정합니다. 

(#5) ```uses: actions/checkout@v3```
* 프로젝트 코드를 CheckOut합니다.

(#6) ```uses: actions/setup-java@v1```
* Github Action이 실행될 OS에 Java를 설치합니다.
* 책에는 8로 설치되어 있으나, 저는 해당 프로젝트를 11 버전으로 진행하였습니다. 그래서 11로 제작하였습니다.

(#7) ```run: chmod +x ./gradlew```
* gradle wrapper를 실행할 수 있도록 실행 권한 (+x)을 줍니다. ./gradlew를 실행하기 위함입니다.

(#8)```run: ./gradlew clean build```
* gradle wrapper를 통해 해당 프로젝트를 build 합니다.

파일 생성이 끝났으면, 해당 파일의 이름을 지정하고 commit 합니다.

Actions으로 이동해 Action을 확인합니다. 

![image](https://user-images.githubusercontent.com/104341003/177391519-dced3800-0c55-496a-ab48-341704693750.png)

빌드를 클릭하면 자세한 정보를 확인할 수 있습니다.
<br>
<br>

![image](https://user-images.githubusercontent.com/104341003/177391685-ed2748e1-d4e9-48f4-b765-01840aedfa96.png)

<br>
<br>

![image](https://user-images.githubusercontent.com/104341003/177391890-695b3fe0-2409-4ccf-b677-c5c3b8023c66.png)

<br>
<br>
당장은 해당 Action을 사용하지 않을 것 이기 때문에 사용하지 않는 Action은 바로바로 삭제합니다. 
프리 요금제의 경우 1달에 2000시간을 초과하는 경우, 사용이 제한됩니다.
<br>
<br>

![image](https://user-images.githubusercontent.com/104341003/177395579-471dfcc0-2b83-4e81-9fc0-7ed410f43fd4.png)

<br>
<br>
다음은 이메일 등록입니다. gitHub에서는 trigger가 될 때마다 이메일을 받을 수 있습니다. 책에 있는 내용에는 CI 실행 완료시에만 가도록 설정되어있지만, push되는 모든 알림에 메일이 오게 됩니다.
 <br>

![image](https://user-images.githubusercontent.com/104341003/177382447-4c5f5023-dd0a-40d8-adbc-19b04114248b.png)

<br>
<br>
gitHub내에서 commit 하거나 외부에서 push하는 경우 메일로 확인 받을 수 있습니다.
<br>

## GitHub Action와 AWS S3 연동하기

<br>
Page 327 ~ 332
책에 나와 있는 내용과 거의 흡사합니다. 변경된 내용이 없다고 생각하셔도 좋습니다.

간단하게 설명하면 깃허브 액션을 사용하면 다음과 같은 구조로 진행이 됩니다. 

![image](https://user-images.githubusercontent.com/104341003/177384544-2e29c7df-4103-4406-9f78-88fc0207397a.png)

<br>
<br>
AWS S3는 파일 서버로, 이미지 파일 / 정적 파일 등을 관리하는 기능입니다.
실제 배포는AWS CodeDeploy에서 진행되는데, AWS CodeDeploy는 저장 기능이 없기 때문에, AWS S3와 연동하여  .jar 파일을 받아 배포할 수 있도록 진행됩니다. 

> AWS CodeDeploy에서도 빌드와 배포 모두 가능하지만
> 해당 실습에서는 AWS CodeDeploy는 배포를, 빌드는 GitHub Action에서 진행됩니다. 
> 빌드없이 배포만 하는경우 AWS CodeDeploy 하나로 진행하는 경우 대응하기 어렵기 때문입니다.

### AWS Key 발급

외부 서비스가 접근이 가능하도록 해당 권한을 가진 AWS Key를 생성하여야합니다.
AWS에서는 해당 서비스를 IAM(Identity and Access Management) 에서 관리합니다.

사용자 -> 사용자 추가 버튼을 눌러 해당 서비스를 사용합니다.

![image](https://user-images.githubusercontent.com/104341003/177385867-e19d8217-3543-4998-977f-9dc3ab69bb77.png)

![image](https://user-images.githubusercontent.com/104341003/177386019-0c157334-bb74-448c-8e70-6e256cc8e837.png)

<br>
<br>
사용자 이름을 지정하고 , 프로그래밍 방식 엑세스 유형을 선택합니다.

![image](https://user-images.githubusercontent.com/104341003/177386164-25b5593f-570d-41ba-9089-14b34ca5c9ff.png)

<br>
<br>
이후 기존 정책 직접 연결을 선택한 후 두 개의 권한을 체크하여 다음으로 넘어갑니다.

![image](https://user-images.githubusercontent.com/104341003/177388015-b70f50bc-819b-4019-8f52-22501c711653.png)

![image](https://user-images.githubusercontent.com/104341003/177387975-2dfe6de3-ee22-4a5e-a818-4fcfdf705c1e.png)

<br>
<br>
태그는 인지 가능한 이름으로 적용합니다.

![image](https://user-images.githubusercontent.com/104341003/177388137-74bcee88-7ea2-416e-aaa5-7854ed703d02.png)

<br>
<br>
이후, 생성 권한 설정 항목을 확인합니다.

![image](https://user-images.githubusercontent.com/104341003/177388214-33f88e6b-17a2-4548-9016-5e95bce9ceb5.png)

<br>
<br>

### 굉장히 중요합니다. 
엑세스 키와 비밀 엑세스 키는 개인으로 보관해야 하며, 해당 창을 넘어가면 **비밀 엑세스 키는 더이상 저장할 수 없습니다.**
반드시 다운로드를 받던, 메모장에 따로 보관하던 해주세요.
<br>

![image](https://user-images.githubusercontent.com/104341003/177388247-1136b0a5-3ab7-4117-8632-0b87f163fc42.png)


<br>
<br>
Page 333

해당 키를 GitHub Action에 등록해야 합니다. 

추가사항으로 Action에서의 날짜는 실제 우리가 빌드한 날짜와 다르게 되어있습니다. 해당 내용을 yml 파일에 추가하면서, AWS 인증도 추가하도록 하겠습니다.

### 1. yml 작성 전 Action에서 키를 등록합니다. 우리의 GitHub는 public 이기 때문에, **해당 키를 그대로 저장할 경우 외부로 노출** 됩니다. 

다음과 같이 secret key를 지정하여 안전하게 보호합니다. 
해당 Repository - Settings - Secrets - Actions -> New repository secret
<br>

![image](https://user-images.githubusercontent.com/104341003/177389049-7308e36f-645d-4cc7-9f83-c0c668061a0d.png)
<br>
총 두 개의 시크릿을 만들겁니다. 

Name에는 각각
```AWS_ACCESS_KEY_ID```
```AWS_SECRET_ACCESS_KEY```

Value에는 각각 저장한 ACCESS_KEY와 SECRET_ACCESS_KEY를  입력합니다.
<br>

![image](https://user-images.githubusercontent.com/104341003/177389302-931c1c10-306e-43f8-82a3-06ad553847f2.png)
<br>
<br>
### 2. 저장이 완료되었다면, 해당 Repository -  code의 맨 위에 github/workflows 로 이동하여 yml 파일을 다시 열어줍니다.
<br>

![image](https://user-images.githubusercontent.com/104341003/177389631-50a43e70-8450-458b-9972-0a1a75f58e87.png)
<br>
<br>
위에 언급한 추가사항인  Action에서의 날짜는 실제 우리가 빌드한 날짜와 다르게 되어있는 부분을 추가하도록 하겠습니다. 

```C

 ...code...

    - name: Build with Gradle
      run: ./gradlew clean build # (8) 
      shell: bash

      # 이전에 추가했던 내용 아래에 작성하면 됩니다. 

    - name: Get current time
      uses: 1466587594/get-current-time@v2  # (9)
      id: current-time
      with:
        format: YYYY-MM-DDTHH-mm-ss 
        utcOffset: "+09:00"

    - name: Show Current Time
      run: echo "CurrentTime=${{steps.current-time.outputs.formattedTime}}" # (10)
      shell: bash
```
      
(#9) ```uses: 1466587594/get-current-time@v2```

* utcOffset: "+09:00": 해당 action의 기준이 UTC이므로 한국시간인 KST로 진행 하기 위해  offset에 +09:00 를 해줍니다.

(#10) ```run: echo "CurrentTime=${{steps.current-time.outputs.formattedTime}}" ```

* 지정한 포맷대로 현재 시간을 보여줍니다.

저장한 후 Actions으로 이동해 Action을 확인합니다. 
이후 Action을 삭제합니다.


### 3. AWS S3에 저장할 수 있도록 버킷을 생성합니다.
<br>

![image](https://user-images.githubusercontent.com/104341003/177394832-b322d1c1-1a06-4484-8124-3485b456e36d.png)
<br>
<br>

![image](https://user-images.githubusercontent.com/104341003/177394910-3887c9a6-35a3-4d18-92fc-12cfd004f24c.png)
<br>
<br>

버킷의 이름을 지정한 후 다른 설정 값은 변경하지 않고, 다음과 같이 생성합니다.
<br>

![image](https://user-images.githubusercontent.com/104341003/177395082-fb1f6e2e-08d1-494f-9cd1-77fdc3442d21.png)
![image](https://user-images.githubusercontent.com/104341003/177395168-2683617b-d215-4636-8245-b1fc89d23d6d.png)

<br>
<br>

### 4. Git Hub Action과 AWS를 연동합니다. 

해당 Repository -  code의 맨 위에 github/workflows 로 이동하여 yml 파일을 다시 열어줍니다.

다음과 같은 내용을 추가합니다.

```C
# main.yml의 최상단입니다.
#새로 추가된 내용입니다. (11) 
env:  
  S3_BUCKET_NAME: ward-build 
  PROJECT_NAME: aws-web-service 

name: deploy # (0) GitHub Action에서 보여질 이름을 지정합니다. 

on:
  release:
    types: [push] # (1) push시 자동으로 실행됩니다.
  push:
    branches: [main] # (2) main 브랜치에서 동작합니다.
  workflow_dispatch: # (3) 수동으로도 실행이 가능합니다.

...code...
```
(#11) ```env```
* 환경 변수를 이름 : 값 으로 지정합니다. 자주 사용되거나, 변경될 수 있는 부분은 환경 변수로 지정하면 편하게 사용할 수 있습니다.

S3_BUCKET_NAME:  내가 설정한 S3 버킷 이름
PROJECT_NAME: 내 Git Hub 프로젝트 이름

으로 변경해야합니다.

또한 맨 아래에 다음과 같이 내용을 추가합니다 .

```C
...code...

 - name: Show Current Time
      run: echo "CurrentTime=${{steps.current-time.outputs.formattedTime}}"    # (10)
      shell: bash
    # 아래부분이 추가되는 내용입니다.
    - name: Make zip file
      run: zip -r ./$PROJECT_NAME.zip .         # (12)
      shell: bash

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v1      #(13)
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ap-northeast-2

    - name: Upload to S3
      run: aws s3 cp --region ap-northeast-2 ./$PROJECT_NAME.zip s3://$S3_BUCKET_NAME/$PROJECT_NAME/$PROJECT_NAME.zip    #(14)

```

(#12) ```run: zip -r ./$PROJECT_NAME.zip .```

* 프로젝트 이름으로 해당 폴더를 모두 압축시킵니다. (빌드된 전체 폴더)

(#13) ```uses: aws-actions/configure-aws-credentials@v1 ```

* aws에 해당 키 값으로 접속을 진행합니다. 

(#14) ```run: aws s3 cp --region ap-northeast-2 ./$PROJECT_NAME.zip s3://$S3_BUCKET_NAME/$PROJECT_NAME/$PROJECT_NAME.zip```

* s3 에 프로젝트 이름에 해당하는 폴더에 zip파일을 저장합니다.

해당 내용을 추가한 후, Action에서 빌드를 확인해 보면, 정상적으로 빌드가 완료되었고 S3에 해당 압축 파일이 들어있는 것을 확인할 수 있습니다. 

![image](https://user-images.githubusercontent.com/104341003/177398959-0e79f1c3-bc8c-4c84-bce7-413e90b27ce6.png)

![image](https://user-images.githubusercontent.com/104341003/177398942-efaac8be-bcc6-4714-8dec-fe7e2a92fd39.png)




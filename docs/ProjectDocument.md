# 프로젝트 개요
콘서트 예약 서비스를 구현합니다.  
실제로는 콘서트 대행사와 결제 시스템이 연동되어 구현되지만, 이번 프로젝트에서는 순수하게 콘서트 예약 시스템 자체에만 집중하여 구현합니다.

### 프로젝트 목표
1. 콘서트 예약 시스템을 이용하는 사용자는 대기열을 기다린 후, 예약 서비스에 접근이 가능한 형태로 구현됩니다.  

2. 포인트 시스템을 활용하여, 좌석을 구입할 잔액은 미리 충전하도록 합니다.

3. 좌석 중복 예약이 불가능 하도록 구현합니다. 

## 시스템 콘텍트스 다이어그램
시스템 구성의 전반적인 시나리오를 나타내었습니다.

> 1) 실제 콘서트 예약 시스템이 구현된다면, 콘서트 대행사로부터 데이터를 가져와야 합니다.  
>
>2) 포인트 충전을 위해서는 결제가 필요합니다.  
이에 결제시스템을 연동하는 시나리오를 표현하였습니다.  

## 컨테이너 다이어그램 및 시스템 컨텍스트 다이어그램
시스템에 구성될 아키텍쳐를 나타내었습니다. 가상으로 외부 결제 시스템 및 콘서트 주최사와 연동하는 시나리오를 추가했습니다.  
![systemcontext diagram](https://github.com/user-attachments/assets/c1076be8-3e1d-4cfb-9c29-232128a00e67)

1) 시스템 전체적으로는 Web Application Server, Queue, DB로 구성됩니다.  
![container diagram](https://github.com/user-attachments/assets/73618706-28ff-4e25-8258-755579a0f625)

--------
## 도메인 다이어그램
콘서트 예약 시스템의 전체적인 도메인 구조를 나타내었습니다.  
![domain diagram](https://github.com/user-attachments/assets/8c9ae881-1ee6-4a67-aa23-71bc4a5c7672)

## 시퀀스 다이어그램

1) 토큰 발급  
![token](https://www.plantuml.com/plantuml/dpng/SoWkIImgAStDuU8goIp9ILLusxRWRUC6XTSRvhpQqSd55LgSMgIWQwVWav-UcQTWfV1wuPnNstUycRj3uKttdDVzBLnW2P2AaFpirBpuWDAadCpY3A0cArIZ82RPwE9JD-Fjp1aAN5mEgNafG1S10000)

------
3) 포인트 충전  
![point](https://www.plantuml.com/plantuml/dpng/SoWkIImgAStDuU8goIp9ILLusx_cpTmTRxjsABpPjlBDWXOkhj3Yr285NJk5WFpCl99uvup4elJK-E3KehBCv5G5AmmD2czcJNcpQIqNDkKWOy8oYqeJS-834YjpKu4AWYkBIr9pWUQyshJXpO8vKDqQlld9sPhOpR3H9MZ6t8clkxVYCGFk7Q2UDtCMl5dGy6Rk30oVGBI2r8A20AHqtfGtKnSglDumu-tC6LnS3gbvAS2G0m00)

------
5) 예약  
![reserve](https://www.plantuml.com/plantuml/dpng/VPBDIiD058NtUOfvWRw0Y1HruQxLubf2Es1mJ22Pk9OwWjP24QoDfItZZnGXXT16ZD2-KERc7Pmu7HEwS7lETy_St9EgHqsN-kV4CAX52KQm2e2Diaw28BY1_iemZZtieieEElInZz4M4jtBD8bW42EOCfXS6ygGH4vAGCwBRmQTDmJyFlrQedgj_fTISriAUbsACCaZiiTRGwhARZHFSSidl--qEKn721ZFhiPhOhjI9jB59mmY1Bml5Z4IKM-jmEIz48FiAIZoznoxYLrwO1AIB_7I1JRMB0mVO3uhNlgFtSnjO9CMqCREsbMoXdQ9qpmh4kgEPUTGrWjXdEG5fFC8-4YVIFdb2xP367wKUD6EOJ13m9cu2ulW8-nXzyAabcEVx6DgMiIJqwfTFICB5SzwuLAJdC4yrbqfLd-wKSLsI_uuFm00)

------

## 클래스 다이어그램
![class](https://www.plantuml.com/plantuml/dpng/lLV1RXit4BtlLn2-H6wG8BaQXH4rTW41ZPFQsw10qGErEv89SyajoLNgj0LIDAVU2-IIeeVyGNFi5wNAFvGxebP3at8BGUWbiTuRalFUpCvo7rd7ZUiQMTnXor_VVVpbrVBjNspv-jtoZzyAUoLKomrlM2MvjMFdZ9Xs3YQLLXVY9R07XL_syUVtV__-nYzcVitoxPi2WzbN5WpxeM3i7XCrEs9VQ3N7Nvqd9YSUkV3R4Kpn1cAqzVkyqAQEqJIkCi0Tr6FduOdoYfH3l6lh77xF_tEN6PWBwy2C3ZsxqA96LEgvK0XDjPR0LV5J4GGzqqAvhOhEOJQ9G0CD5meNuV4Evc1sJvVNDUvbHxpH7UxfVz7S-pGo8KGBHit1OHIA36bjH9Rw2bHMfEiPMhTKEOrypcK77YrFlouHXUzROU24knuz6PUdvUJ9wUv-p461yJGc7DTmmQMe2HNhDp0pO2_ZTHi3pi62MN0dTDu6i-7HZDkRG9iwWrhWBWMhIwWx2Gb1JWpkKPC8U-6uw-mU7etgKciBn0dgBtOGuKx0SI4jXX1uu-XJKoUJWBWp-VT7gUNN3IYNTpVZYTEEop4-0wJ7XnNxymaNeRxO8Lo5H3WifYikgqvYXuP4qyUHU3Tnq6pr3zdzDzrtdLSgtFKD_reZAjZ7E1iiwDD76_86F5kLdiXFskA_Dr1k-F1ScuJulJLAQtG5rWOneyn83XHzc0AqyU82UDO8B6qiU06gWsmBd7ND54pGH5j3lqZ1nrfsZHfmCd2Wt_Ype65dNuGTBxnUFfMGc1BEwKLdnlJpNc1gHYgTecV9pK7g2gTLZ5QyvLNI_Bk-fnpcIYkB0EbnC3Dj6gwglA9smsT4JRR5fcfhi9KHRIeOBu6PGGggJj6TVI0wgHi49hqU1bE-tmEvLNgs_0aw7D9hB4LpK-UwDx47BMa6AchOWoXQO-HT4E-LQUZ7MbLWw8lromv2Y-D0xdyTCGG_9L7du1dhZdqORLsQR9y5ADnWP5RXHopTuf0yVxYcXjMQmrnqY6a1hcuD4FQqQTsrZo7ZOaLytacPubQyn2EJLq5wH32737tIrMbB9BJ2MVXqLehg2foDFOkkLdrtqEO8nU-_SIQrpvRkYqmePvuyYy-bQeUOy_XmZ0d7UqU4SbuQho0qH7cCNj1A2mVD4IjlgsfveuXbMhDfjZQ2lfR6nl1hcWQQrnzl_PRVV1k0cT7AuTB1Ncx8RzAwQ-mksNiKqOV_lzWxxCFxLn_-VBt-ECRlsuE71tuMu7_uKHMZ3-xV_-HWzKcL8qY9SpJU_JB4S34eo0Oaax0mJ-8bOXitpAq2jyZaWxVD6-n3aj4Gann9cDo1VGnXmYjopHE6l2u8dwmaKIbPX7pPmTcAUR8kNx5j3Fkep8ZR7ltZCSC_2-pW12qUWQgxHXR_0000)

## ERD
![erd](https://www.plantuml.com/plantuml/dpng/hPRFRjGm4CRlVehSe4WhLKMzS6heXX81soeD5JVa9bFL8h5JnswfTliStBXnHqAy5Ef3uAndTxsrBK6qr-yRs_FpZFySzOfAfTk64E2AgVlaOmyocPCaUP6mEdcLlCvFybcPdBqpaZPUFZNYHLeSlqsBvo_tznBDsOq6uquc6C9f2qxGmU7Xdd4wslTtGjQ-4uwl9505TQgCDKtBhCm_PCC2NUtB3-lqpmJZAfY_cs41LxbZlXbC2IrbV3dUE4REJh9YT1wb-08ybeSQp7owpUpkP3nF77n16mt6ABDFfT7WQySaJAa29vlHkHTWbhwbYeaeWLo7M0xq_er0o3fiz41LKA-keTOD13mt4v_VCSyLLRftjlnmVtoNdSewTaeGBw__TrSwUjy2Lz4Dy-cKKBH9Mw6tEXR3Ty-RAsZZp1WGvHvVTS-uqTIcvK9tabMmWuDxPiiG0yKgZIwlNqNg5n1ZlH8UZA_WET0m4nOW3dGBN4EiRZFTNeBqkcom2d4Nr8z5erlkMVQemdAHqzFtMJeRpNenPFpvpw_VZzz-Fdx_4SP6i7nRTPbdmMUf4PMz6JsdeXsjpFgxQ4AGLqAsb5ShZcW_RY97bD0P7QQjeQyawn0ONvjUKHbePE3rhbwWSppwqOv5FuhiaFdWqHrnPXv8IjO-FQktb3Nqie7XEFMxksqslnQ3spcQrvw85tykh2bOpFAk6vFCiwI0PjdL_JNhUhBy0ouMaybYOJzK6qNCxTV6LTmFWwlRPsDBGLv2d279Wrt9HWUCORE3EjQA43FKNHNV8fGnUhtqsdVdzrHdzi57zUdTGbPHw0TEhXiId89WPjQVuuwZQCgzoXnrmG8g8KV0QzqsVm40)

## 상세 아키텍쳐 정보

> Language : Java 17  
>
>DB : MySql 8.0
>
>Queue : 2주차 Java 구현 Queue 활용  
>이후 Redis나 AMQP활용하여 리팩터링  
>
>Server : Spring boot 3.4.1

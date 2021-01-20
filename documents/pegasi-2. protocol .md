## Pegasi Protocol 

聚合协议主要包括2部分

1. 聚合配置文件的请求协议，主要用以控制每个App 的所有广告位的广告网络配置，包括优先级和请求参数
2. 聚合相关的上报协议，在广告展现、点击时上报相关信息。目前包含RCV 和 天马上报2套上报协议。（待迁移到天马上报）



### 聚合配置文件请求协议

协议说明

请求参数说明

| HTTP Param | 是否必须         | 类型     | 备注                                       |
| ---------- | ------------ | ------ | ---------------------------------------- |
| Method     | 必须           |        | GET                                      |
| User-Agent | 必须           |        | 从Android 系统获取                            |
| Scheme     | 必须           |        | http/https，建议https，并且验证证书。实现时最好支持本地的默认配置 |
| Host       | 必须           |        | 海外 unconf.adkmob.com <br/> 国内 unconf.mobad.ijinshan.com |
| Path       | 必须           |        | /b/                                      |
| Param      | 必须(如下具体说明)   |        | action=pos_config&postype=1&mid=1018&posid=&androidid=faab4b2f2b2c98b1&cver=20550542&lan=CN_zh&sdkv=3.11 |
|            | action       | String | 固定为 "pos_config"                         |
|            | postype      | Int    | 固定为 "1"                                  |
|            | mid          | Int    | Media Id, 由Pegasi 后台创建对应App 后获取, 如 "1018" |
|            | lan          | String | 从android 系统获取, 如 "CN_zh"                 |
|            | androidid    | String | android id , 从系统获取, 如 "faab4b2f2b2c98b1" |
|            | cver         | Int    | 客户端版本号, 从客户App 获取, 如 "20550542"          |
|            | posid        | Int    | 固定为 "" (空)                               |
|            | sdkv         | String | (聚合SDK 专用), 记录聚合SDK 版本号, 如"3.11".        |
|            |              |        |                                          |
|            | test_country | 仅内部测试  | 取值国家, 如US, HK, 用以获取指定国家的配置               |

返回结果说明, Json 格式

| 参数                         | 类型     | 含义                                       |
| -------------------------- | ------ | ---------------------------------------- |
| poslist                    | array  | + 广告位列表, 内含每个广告位的配置信息                    |
| poslist[].adtype           | int    | 对应广告位类型                                  |
| poslist[].placeid          | int    | 广告位id, 标记在后台创建的广告位id                     |
| poslist[].info             | array  | + 内含每个广告网络的配置信息, dict 格式                 |
| poslist[].info[]           | dict   |                                          |
| poslist[].info[].name      | string | 每个广告网络对应名称, 用以匹配不同广告网络<br/>如Facebook Native 广告的名称 fb，（fb_h, fb_b, fb_l, 主要支持high, balance, low , 在原有名称后附加 _h, _l, _b) |
| poslist[].info[].parameter | string | 每个广告网络的请求参数。有时候会要求一个pos 配置多个id， 用 ";" 分割，如某个fb 配置了 "334260980075913_617047241797284; 334260980075913_384378171730860"。需要adapter 相应支持 |
| poslist[].info[].weight    | int    | 优先级。数字越大，优先级越高。如果小于或者等于0, 表示这个广告network 未开启。 |



接口示例 （海外产品 File manager）

```
curl -H "User-Agent: Mozilla/5.0 (Linux; Android 6.0.1; MI NOTE LTE Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.98 Mobile Safari/537.36" -H "Host: unconf.adkmob.com" --compressed "http://unconf.adkmob.com/b/?action=pos_config&postype=1&mid=1018&posid=&androidid=faab4b2f2b2c98b1&cver=20550542&lan=CN_zh&sdkv=3.11"

http://unconf.adkmob.com/b/?action=pos_config&postype=1&mid=1018&posid=&androidid=faab4b2f2b2c98b1&cver=20550542&lan=CN_zh&sdkv=3.11
```

返回结果

```
{
    "poslist": [
        {
            "adtype": 2,
            "info": [
                {
                    "name": "cm",
                    "parameter": "1018100",
                    "weight": 1
                },
                {
                    "name": "fb",
                    "parameter": "334260980075913_384378171730860",
                    "weight": 2
                }
            ],
            "placeid": 1018100
        },
        {
            "adtype": 1000,
            "info": [
                {
                    "name": "ab",
                    "parameter": "ca-app-pub-7704128875295302/1456381674",
                    "weight": 1
                },
                {
                    "name": "ab_h",
                    "parameter": "ca-app-pub-7704128875295302/8979648478",
                    "weight": 3
                },
                {
                    "name": "cm",
                    "parameter": "1018121",
                    "weight": 0
                },
                {
                    "name": "fb_h",
                    "parameter": "334260980075913_654155901419751",
                    "weight": 4
                },
                {
                    "name": "fb_l",
                    "parameter": "334260980075913_654156128086395",
                    "weight": 2
                }
            ],
            "placeid": 1018121
        }
    ]
}
```



### 上报协议（RCV）

在广告展现和点击的时候, 进行上报。

POST 下面数据到下面URL 

https://ssdk.adkmob.com/rp/ 海外

http://rcv.mobad.ijinshan.com/rp/  国内



| 参数           | 类型     | 说明                                       |
| ------------ | ------ | ---------------------------------------- |
| ac           | Int    | 50 展现, 60 点击                             |
| pos          | Int    | 1018110                                  |
| mid          | Int    | 1018                                     |
| aid          | String | faab4b2f2b2c98b1                         |
| lan          | String | zh_CN                                    |
| ext          |        |                                          |
| cmver        | String | 客户端版本好, 如"20550542"                      |
| mcc          |        | 460                                      |
| mnc          |        | 01                                       |
| gaid         | String | 2303eefa-f25b-49a9-9413-988f5f90b0e7     |
| pl           |        | 2                                        |
| channelid    |        |                                          |
| lp           |        | 0                                        |
| sdkv         |        | 3.11                                     |
| at           |        | 1478141943399                            |
| duple_status |        | 1                                        |
| attach       |        | [{"pkg":"com.mobvista.ad","sug":-1,"res":6042,"des":"","fbmess":"1"}] |



```
curl -H "User-Agent: Mozilla/5.0 (Linux; Android 6.0.1; MI NOTE LTE Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/52.0.2743.98 Mobile Safari/537.36" -H "Host: ssdk.adkmob.com" --data 'ac=50&pos=1018110&mid=1018&aid=faab4b2f2b2c98b1&lan=zh_CN&ext=&cmver=20550542&mcc=460&mnc=01&gaid=2303eefa-f25b-49a9-9413-988f5f90b0e7&pl=2&channelid=&lp=0&sdkv=3.11&at=1478142028778&duple_status=1&attach=[{"pkg":"com.admob.native","sug":-1,"res":3002,"des":"","fbpos":"ca-app-pub-7704128875295302\/4216009676","fbmeta":"GiZFwnA%2BQiWwPGSI7csCsjAwMDHTCua%2FP7xqHDX0XMVgplqA9ft%2BPMws74UxbZxEpIuO%2Bh%2FH%2BmkYbFEZhXUJksJM9MEQhLo6BEh0V5jNH%2Bw%2FstagshdbS3G3Zklcu%2BkvoADUqv2BjdVml9CnErZxZscD6tCEwvR8uB%2FxF8yAYqkJhjAfXpa4oo8LLzmZeFwnk3u9QJUGDdySI%2BF52NPksfQmdkf2MEgk12pf%2FziU%2FN%2FE0X1UrNlRUKxSCUPaibOxn3Gh9kTrzSGvtEGH5E2r134EsoecmWxJiJJQ%2BC4VuKT5QbcvAKCnFn17MKdHZAuZtyjCgBDBmhGS%2F7efs2HwITdwEj0AT4qzZDyo%2Bu9eDuBQv2BPEQklJeUpqCagmDkC0%2FFXz6prqiEpKhgvXlwhnToLTyEGaliAAiUPWTo2j6xNFDxagMCRFVViXyrRXiQ6DsmacHm1mOZqAkwoYUih%2BD4nyEjBj8VIleugEFrJWx%2BLfOZcIq3vK%2FLp6LChYIK9bG%2FnQWTG163jpwgGXTE7kqjWFRM%3D","fbmess":"1"}]' --compressed https://ssdk.adkmob.com/rp/
```



### 上报协议（天马）

详见 

https://drive.google.com/open?id=1V1pFV7kwDwLsMjfS5QoFpCnKyurL2wLgEN7QWsPMdjo


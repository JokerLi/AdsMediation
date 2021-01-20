

## 猎户商业



海外 http://www.cmcm.com/en-us/

- 广告主 http://ad.cmcm.com
  - Login orion  https://ori.cmcm.com/login/
- 变现  http://ad.cmcm.com  当前是MobPartner 的介绍 
  - Login orion
    - Orion http://pub.adkmob.com/index.php
    - MobPartner(Affiliate) https://github.com/MobPartner
    - Pegasi  
      - 天马SSP3.0：http://peg.cmcm.com/index—供外部流量使用
      - 天马SSP1.0：http://pub.adkmob.com—后台  http://pub.adkmob.com/index.php—前台
    - IBD 海外 天马&猎户的SDK：https://github.com/CMAdSDK



中国  http://cn.cmcm.com/

- 广告主	http://liehu.cmcm.com/
  - Orion Login http://liehu.cmcm.com/login/index.html 
- 变现 






## == TODO 

- Zip 
- 文档
  - AppWall 接口说明 
  - 自定义Adapter 的方法 
- 测试case 
  - 接入聚合SDK 的测试case 
  - 自定义Adapter 时的case 
- ​
- 猎户接口 
- LATER
  - 提供的Adapter 调整 
    - KEY, Res 写到对应的Adapter 中
  - ​



## SDK 接入测试

高阶： 

* enableDebug 接口
* 本地MapLocal 配置文件 (http://unconf.adkmob.com/b/ )
* 请求协议里面测试分国家的配置如 test_country=US 



## SDK Adapter 接入（修改，更新升级）测试

新增或者修改广告网络时，要求专门根据此用例进行测试验证

* Native 、Interstitial、AppWall 
* Adapter 对应的KEY, RES 有效性
* 屏保, WindowManager, 普通场景, List 4个场景的Native 测试
* 确认请求，过期，展现，点击，上报有效
* 配置文件的兼容，多id 测试 



 




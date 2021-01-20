聚合精简包: 只带猎户
cmadsdk_base_china.aar  国内
cmadsdk_base_world.aar  海外


聚合扩展包: 带聚合功能, 包含native, banner, 开屏等聚合优先级逻辑.
扩展包会提供2种版本: basic 和 all.
basic 只会包含cmcm 公司内部经常用的几个包. 如果要有额外支持, 则根据说明从 cmadsdk-extension-packages.zip 拷贝对应 adapter 和 aar/jar . 在工程进行配置.
all 会包含所有已经支持的外部sdk 包. (demo 会用all 包来提供测试验证)

聚合国内扩展包 ext_china
-  basic 版本
cmadsdk_ext_china_V3.4.0.aar
    内含 liehu国内, gdt, baidu(native)
- all 版本
cmadsdk_ext_china_all_V3.4.0.aar
    相比basic, 增加 baidu 开屏, intowow 视频

聚合海外扩展包 ext_world
- basic 版本
cmadsdk_ext_world_V3.4.0.aar
    内含 liehu海外, fb, yahoo
- basic_mopub 版本
    内含basic 以及 iab 和 mopub sdk
- all 版本
cmadsdk_ext_world_all_V3.4.0.aar
    相比basic_mopub, 增加 admob

# PrideSpecial Reborn

![模组加载器: Fabric/NeoForge](https://img.shields.io/badge/modloader-Fabric%2FNeoForge-pink)
![Minecraft: 1.21+](https://img.shields.io/badge/Minecraft-1.21%2B-green)
![Java: 21+](https://img.shields.io/badge/Java-21%2B-red?logo=openjdk)

[![上游: @YGP-Official/pridespecial](https://img.shields.io/badge/%E4%B8%8A%E6%B8%B8-%40YGP--Official%2Fpridespecial-gray?logo=modrinth)](https://modrinth.com/mod/pridespecial-reborn)

此模组允许用户通过配置文件，在不同情况下覆盖由 PrideLib 提供的骄傲旗帜。

[English](README.md) | **中文**

## 配置文件

位于 `.minecraft/config/pridespecial.json`，配置文件格式如下：
```json5
[
  {
    "flags": [
      // 骄傲旗帜列表，采用 pridelib JSON 格式定义
      {
        "colors": ["#ff0000", "#ffff00", "#0000ff", "#ffffff", "#000000"]
      },
      // 也可以是对已加载旗帜的引用
      "pride:rainbow",
      // ...
    ],
    // 指定类名，以便仅将上述覆盖应用于目标类。
    // 留空或省略表示适用于所有情况。
    "caller": ""
  },
  {
    // 如果不希望从列表中随机选择，也可以指定单个旗帜/旗帜引用
    "flags": "pride:transgender"
  }
  //...
]
```

## 内置旗帜类型

### `pridespecial:single`

也可以定义单色旗帜：
```json
{
  "shape": "pridespecial:single",
  "color": "#008543"
}
```

### `pridespecial:blank`

或者，也可以选择不渲染任何内容：
```json
{
  "shape": "pridespecial:blank"
}
```

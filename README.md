# PrideSpecial Reborn

![mod loader: Fabric/NeoForge](https://img.shields.io/badge/modloader-Fabric%2FNeoForge-pink)
![Minecraft: 1.21+](https://img.shields.io/badge/Minecraft-1.21%2B-green)
![Java: 21+](https://img.shields.io/badge/Java-21%2B-red?logo=openjdk)

[![Upstream: @YGP-Official/pridespecial](https://img.shields.io/badge/Upstream-%40YGP--Official%2Fpridespecial-gray?logo=modrinth)](https://modrinth.com/mod/pridespecial-reborn)

This mod allows users to override the pride flags returned by PrideLib under different circumstances
via configuration files.

## Configuration file

Located in `.minecraft/config/pridespecial.json`, the configuration file has the following format:
```json5
[
  {
    "flags": [
      // A list of Pride Flags, defined in pridelib JSON format
      {
        "colors": ["#ff0000", "#ffff00", "#0000ff", "#ffffff", "#000000"]
      },
      // Can also be a reference to a loaded flag
      "pride:rainbow",
      // ...
    ],
    // Specifying a class name so the override above can be only applied to the target class.
    // Leaving blank or omitting indicates that it applies to all circumstances.
    "caller": ""
  },
  {
    // Can also specify a single flag/flag reference if randomizing from a list is not preferred
    "flags": "pride:transgender"
  }
  //...
]
```

## Built-in flag types

### `pridespecial:single`

Single-colored flags can be defined as well:
```json
{
  "shape": "pridespecial:single",
  "color": "#008543"
}
```

### `pridespecial:blank`

Alternatively, it's possible to render nothing:
```json
{
  "shape": "pridespecial:blank"
}
```

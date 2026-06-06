# FTB SNBT

A FTB-flavor Stringified NBT Parsing Library.

Part of sources are ripped from [FTB-Library](https://github.com/FTBTeam/FTB-Library) **without permissions**.

## Usage

Requires Java 17+.

[![](https://jitpack.io/v/Taskeren/FTB-SNBT.svg)](https://jitpack.io/#Taskeren/FTB-SNBT)

```kts
repositories {
    // ...
    maven("https://jitpack.io")
}

dependencies {
    // ...
    implementation("com.github.Taskeren:FTB-SNBT:${TAG}")
}
```

## Null Tags

HelloNBT doesn't support null tags, so this library gives a workaround that you can pass a special Tag instance as null
tags. It will be used for `null`, `end` and `END`. And if it's missing, an exception is thrown when reading these
keywords.

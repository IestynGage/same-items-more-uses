# same-bucket-more-uses

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

This is an [Architectury](https://docs.architectury.dev/) project that builds for both Fabric and NeoForge:

* `common/` - loader-independent code and resources, shared by both builds.
* `fabric/` - Fabric entrypoint, `fabric.mod.json` and datagen.
* `neoforge/` - NeoForge entrypoint and `neoforge.mods.toml`.

Useful tasks:

* `./gradlew build` - builds `fabric/build/libs/*.jar` and `neoforge/build/libs/*.jar`.
* `./gradlew :fabric:runClient` / `./gradlew :neoforge:runClient` - run the game with the mod.
* `./gradlew :fabric:runDatagen` - regenerate recipes into `common/src/main/generated`.

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.

## New usages 

### Bucket

* Powder snow buckets can now be smelted to make water bucket.
* You can now craft a powder snow bucket by either:
  * using 8 snowballs and a bucket
  * bucket and a snow block
* Powder snow buckets now return 8 snowballs in the crafting table.

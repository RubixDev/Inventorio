# Inventorio - An inventory enhancement mod for Minecraft (Forge / Fabric)

## Showcase Video

[![Video Demonstration](https://img.youtube.com/vi/ZXQb49LaC30/maxresdefault.jpg)](https://youtu.be/ZXQb49LaC30)

## About

This is my vision of the Inventory Update for Minecraft. Of
[features](#Features) I believe are in line with Mojang's vision and could be
potentially added to the game.

Available for both Fabric and (Neo)Forge.

## Installation

Grab the jar file from the
[Release page](https://github.com/Lizard-Of-Oz/Inventorio/releases/).

You can also find this mod on [Modrinth](https://modrinth.com/mod/inventorio) or
CurseForge ([Fabric](https://www.curseforge.com/minecraft/mc-mods/inventorio),
[Forge](https://www.curseforge.com/minecraft/mc-mods/inventorio-forge))

Copy the jar file into `%root_folder%/mods/` alongside other mods.

### Dependencies

Dependencies for Fabric:

- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [Cloth Config](https://modrinth.com/mod/cloth-config)

Dependencies for Forge:

- [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge)
- [Cloth Config](https://modrinth.com/mod/cloth-config)

## Features

![Features](https://user-images.githubusercontent.com/701551/174822000-afce35b6-b6e2-4580-b428-715a764b64a3.png)

#### Toolbelt

Instead of taking space in the Hotbar, tools are now stored in their own place.
When you mine a block, a correct tool gets passively applied from the said
Toolbelt.

- Mending will mend tools in the Toolbelt before going into player's XP bar
- Axe will be used as a melee weapon in the sword slot is empty
- Hoe Toolbelt slot accepts shears. Sword Toolbelt slot accepts Trident.
- Toolbelt slots accept modded tools as long as they inherit from vanilla tool
  classes.

#### Utility Belt

An Offhand replacement with a dedicated hotbar of 4 slots that can be scrolled
through independently, and which skips the empty slots. You can use the selected
Utility independently, which allows you to akimbo two types of blocks or two
types of usable items.

#### Deep Pockets Enchantment

Each level of this enchantment adds an additional row to your inventory, up to 3
at max level. In addition, the first level adds 4 extra slots to the Utility
Belt (from 4 to 8)

The Enchanted Book can be crafted from 2 shulker shells and a book, as well as
traded from villagers or found in treasure chests.

![Crafting Recipe: 2 Shulker Shells + Book](https://user-images.githubusercontent.com/701551/197444061-ede483b9-eba4-4f85-901e-f3215f92b792.png)

#### Increased Ender Chest Capacity

The capacity of the Ender Chest has been doubled. This can be disabled in the
server-wide config, but you need to distribute the said config to all players if
you edit it.

#### Infinity Bow Requires No Arrow

This mod fixes a Vanilla bug when you need an arrow to use the Infinity Bow.

#### Totems of Undying activate from the Utility Belt

Instead of being permanently stuck in the offhand, it will go off from any of 4
(8 with Deep Pockets) Utility Belt slots.

### Player Settings

#### Segmented Hotbar

Accessing slots after 5 with a keyboard might be cumbersome because the keys are
just too far away. This feature makes the first keystroke select a section, and
the second keystroke will select an item inside that section.

There's a "Visual Only" option that keeps the default selection schema.

#### Firework Rocket Boost Button

A dedicated button to fire a boost rocket directly from your inventory while
flying. Can be co-bound to Jump.

#### Trident Loyalty Check

This option prevents you from throwing a Trident without Loyalty.

#### "Use Item" Applies To Offhand

The original idea was to bind each hand to its own dedicated button, but some
people found it confusing, and this option restores the vanilla behavior of
vanilla "Use Item", while "Use Utility" applies only to the Utility Belt /
Offhand.

#### Skip Empty Utility Slots

By default, scrolling and displaying the Utility Belt skips the empty slots, but
you can set this behavior to false. There's also a keybind (not bound by
default) that allows to scroll to the first empty Utility Belt slot.

#### Rebind Scroll Wheel to the Utility Belt

You can rebind the Scroll Wheel to scroll through the Utility Belt, while using
the number keys to chose from the Hotbar slots.

#### Dark Inventorio Player UI

Compatibility with dark theme mods and resource packs.

#### Swapped Hands

This option allows assigning the vanilla Hotbar to your Offhand, and the Utility
Belt to your Main Hand.

![image](https://user-images.githubusercontent.com/701551/120894901-e828dd00-c644-11eb-86aa-6935ad71002a.png)

## Global Settings

To improve mod compatibility, some features can be disabled on a game-wide level
for all players.

Global settings can be accessed by a keybind (only in a single player world) or
directly at `%root_folder%/config/inventorio_shared.json`.

Joining a server (either dedicated or hosted from another client) with
mismatching global settings will prompt a request to sync your settings and
restart the game, but sharing the config beforehand is recommended.

- `ExpandedEnderChest (default: true)` - when set to false, disables mixins
  responsible for increasing Ender Chest capacity.

- `InfinityBowNeedsNoArrow (default: true)` - when set to false, disables mixins
  responsible for Infinity Bow requiring no arrows.

- `TotemFromUtilityBelt (default: true)` - when set to false, disables mixins
  responsible for Totem of Undying going off from any Utility Belt slot.

- `AllowSwappedHands (default: true)` - when set to false, removes the option to
  [Swap Hands](#swapped-hands).

- `ToolBeltMode (default: ENABLED)` - Allows to disable the Toolbelt a)
  completely b) allow only the Toolbelt slots added by other mods

- `UtilityBeltShortDefaultSize (default: true)` - By default, the Deep Pockets
  Enchantment increases the Utility Belt capacity from 4 to 8. When set to
  false, the full capacity is given unconditionally.

- `DeepPocketsBookCraft (default: true)` - Can a Deep Pockets Book be crafted

- `DeepPocketsInTrades (default: true)` - Can a Deep Pockets Book be obtained in
  a villager trade

- `DeepPocketsInRandomSelection (default: true)` - Can a Deep Pockets Book be
  obtained in random selection (Enchanting Table and mob loot)

## Use in modpacks and with other mods

You can include this mod in a modpack or as a dependency for your own mod.

I just ask you to respect my work and include it in a way that would count as a
download of the mod on Modrinth or CurseForge and their reward programs.

- For Modpacks, don't embed the jar into the modpack but link to the mod (as
  should be the default behavior on both Modrinth and CurseForge).
- For Mods, don't embed the mod's jar inside your mod, but mark it as a
  dependency.

If you want to use this mode as a dependency, I recommend using the
[Modrinth maven](https://support.modrinth.com/en/articles/8801191-modrinth-maven).

Be advised that you need to manually keep track of the latest version available.

```groovy
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modCompileOnly "maven.modrinth:inventorio:1.9.3-1.20.4,fabric"
}
```

### Addon Slots, Toolbelt & Item Tags

`InventorioAPI` allows your mod to add custom Toolbelt slots and add custom
allowing and disallowing tags and conditions to toolbelt slots, including
existing ones.

Note: when working with the Toolbelt, please consider that its size may vary
depending on the mods and settings installed.\
Don't assume any particular size of the Toolbelt or the slot order across
multiple play sessions. ToolBelt size is the same for all players within the
same play session.\
Slot indices of the Deep Pockets and the Utility Belt are persistent.

Please use `InventorioAPI#findFittingToolBeltStack` to find a Toolbelt slot that
can accept an item and don't assume any persistent index.

<br/>

By default, any tool inheriting its Java class from a vanilla tool (e.g.
`PickaxeItem.java`) will be accepted by a corresponding slot.

Any tool with an [item tag](https://fabricmc.net/wiki/tutorial:tags)
`inventorio:%item_type%` will be accepted by a corresponding slot.

You can blacklist a tool from the Toolbelt slot by adding giving it a tag
`inventorio:%item_type%_blacklist`.

In Forge, a slot accepts any item with a corresponding
[ToolType](https://mcforge.readthedocs.io/en/latest/items/items/#basic-items).

In Fabric, `fabric:%item_type%` item tag is accepted by a corresponding slot.

Any custom filters and tags can be added via `InventorioAPI`

Note: `%item_type%` is always spelled in plural. Available item types:
`pickaxes`, `swords`, `axes`, `shovels`, `hoes`.

Note: Please ignore `InventorioModIntegration` and `ModIntegration` - they're
intended for Inventorio to integrate with other mods, **not** for other mods to
integrate with Inventorio.

## Contributing

Pull Requests for bug fixes and new features are always welcome. As this project
uses a somewhat unconventional setup, here is a short overview of the Gradle
modules at the time of writing this and how they are linked by the preprocessor:

![Project Graph](./assets/Inventorio%20Gradle%20Modules.svg?raw=true)

Code and resources that are common both across Minecraft versions and mod
loaders should go in [`./src/main/`](./src/main/) (this is the `1.20.4-common`
module). Generally only mixins are written in Java, the rest uses Kotlin.
Preprocessor comments can be used to restrict small sections of code to certain
Minecraft versions or mod loaders. For example:

```kotlin
//#if MC >= 12002
val isGlass = block.block is StainedGlassBlock || block.isOf(Blocks.GLASS)
//#else
//$$ val isGlass = block.block is AbstractGlassBlock
//#endif
```

or

```kotlin
//#if FABRIC
//$$ const val platform = "Fabric"
//#elseif FORGE
//$$ const val platform = "Forge"
//#elseif NEOFORGE
//$$ const val platform = "NeoForge"
//#else
@JvmField
val platform = throw IllegalStateException("unknown platform")
//#endif
```

Any code and resources that are mod loader specific should go in the latest
version for that loader. They will also be included for all previous versions
automatically (or rather for all linked modules as seen in the graph above). For
example, Fabric specific files go in
[`./versions/1.20.4-fabric/src/main/`](./versions/1.20.4-fabric/src/main/) and
after running the `preprocessCode` Gradle task the `1.20.2-fabric` and
`1.20.1-fabric` modules will also include those files.

# Inventorio - An inventory enhancement mod for Minecraft (Forge / Fabric)

## Showcase Video
[![Video Demonstration](https://img.youtube.com/vi/ZXQb49LaC30/maxresdefault.jpg)](https://youtu.be/ZXQb49LaC30)
 
## About
This is my vision of the Inventory Update for Minecraft. Of [features](#Features) I believe are in line with Mojang's vision and could be potentially added to the game.

## Installation
Grab the jar file from the [Release page](https://github.com/Lizard-Of-Oz/Inventorio/releases/).

You can also find this mod on CurseForge: [Fabric](https://www.curseforge.com/minecraft/mc-mods/inventorio) and [Forge](https://www.curseforge.com/minecraft/mc-mods/inventorio-forge)

Copy the jar file into `%root_folder%/mods/` alongside other mods.
### Dependencies
Dependencies for Fabric:
* [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
* [Fabric Language Kotlin](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin)
* [Cloth Config (Fabric)](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

Dependencies for Forge:
* [Kotlin for Forge](https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge)
* [Cloth Config (Forge)](https://www.curseforge.com/minecraft/mc-mods/cloth-config-forge)

![Features](https://user-images.githubusercontent.com/701551/118837735-4fc5f500-b8ef-11eb-92a0-f50698e04edf.png)

## Features 
#### Toolbelt
Instead of taking space in the Hotbar, tools are now stored in their own place.
When you mine a block, a correct tool gets passively applied from the said Toolbelt.
* Mending will mend tools in the Toolbelt before going into player's XP bar
* Axe will be used as a melee weapon in the sword slot is empty
* Hoe Toolbelt slot accepts shears. Sword Toolbelt slot accepts Trident.
* Toolbelt slots accept modded tools as long as they inherit from vanilla tool classes

#### Utility Belt
An Offhand replacement with a dedicated hotbar of 4 slots that can be scrolled through independently, and which skips the empty slots.
You can use the selected Utility independently, which allows you to akimbo two types of blocks or two types of usable items.

#### Deep Pockets Enchantment
Each level of this enchantment adds an additional row to your inventory, up to 3 at max level.
In addition, the first level adds 4 extra slots to the Utility Belt (from 4 to 8)

#### Increased Ender Chest Capacity
The capacity of the Ender Chest has been doubled.
This can be disabled in the server-wide config, but you need to distribute the said config to all players if you edit it.

#### Infinity Bow Requires No Arrow
This mod fixes a Vanilla bug when you need an arrow to use the Infinity Bow.

### Player Settings
#### Segmented Hotbar
Accessing slots after 5 with a keyboard might be cumbersome because the keys are just too far away. This feature makes the first keystroke select a section, and the second keystroke will select an item inside that section.

There's a "Visual Only" option that keeps the default selection schema.

#### Firework Rocket Boost Button
A dedicated button to fire a boost rocket directly from your inventory while flying. Can be co-bound to Jump.

#### Trident Loyalty Check 
This option prevents you from throwing a Trident without Loyalty.

#### "Use Item" Applies To Offhand
The original idea was to bind each hand to its own dedicated button, but some people found it confusing, and this option restores the vanilla behavior of vanilla "Use Item", while "Use Utility" applies only to the Utility Belt / Offhand.     

#### Skip Empty Utility Slots 
By default, scrolling and displaying the Utility Belt skips the empty slots, but you can set this behavior to false. There's also a keybind (not bound by default) that allows to scroll to the first empty Utility Belt slot.   

#### Rebind Scroll Wheel to the Utility Belt
You can rebind the Scroll Wheel to scroll through the Utility Belt, while using the number keys to chose from the Hotbar slots.   

#### Swapped Hands
This option allows to assign the vanilla Hotbar to your Offhand, and the Utility Belt to your Main Hand.

![image](https://user-images.githubusercontent.com/701551/120894901-e828dd00-c644-11eb-86aa-6935ad71002a.png)

## Global Settings
To improve mod compatibility, some features can be disabled on a game-wide level for all players. 

Global settings can be accessed by a keybind (only in a single player world) or directly at `%root_folder%/config/inventorio_shared.json`. 

Joining a server (either dedicated or hosted from another client) with mismatching global settings will prompt a request to sync your settings and restart the game (recommended).

* `DeepPocketsEnchantment (default: true)` - Can an Enchanted Book with Deep Pockets be obtained in Survival. Note: other mods may introduce non-vanilla ways of obtaining enchantments which Inventorio can't control.
* `ExpandedEnderChest (default: true)` - when set to `false`, disables mixins responsible for increasing Ender Chest capacity.
* `InfinityBowNeedsNoArrow (default: true)` - when set to `false`, disables mixins responsible for Infinity Bow requiring no arrows.
* `TotemFromUtilityBelt (default: true)` - when set to `false`, disables mixins responsible for Totem of Undying going off from any Utility Belt slot.
* `AllowSwappedHands (default: true)` - when set to `false`, removes the option to [Swap Hands](#swapped-hands).
* `IgnoreModdedHandlers (default: true)` - Some mods may replace the Player Inventory Screen or have a custom inventory screen based on it. Modifying a said screen is likely to cause compatibility issues with Inventorio.


* `Integrations.Gravestones (default: true)` - if Gravestones Mod is present, injects into it to fix compatibility bugs.
* `Integrations.JEI (default: true)` - if JEI is present, injects into it to fix compatibility bugs.

## Feedback, Use in Modpacks and Mod Compatibility
Feel free to use this mod in a modpack.

If you encounter bugs or compatibility issues with other mods, please report them on the [Issue Tracker](https://github.com/Lizard-Of-Oz/Inventorio/issues).

If you want to request a feature or modification, please use an Issue Tracker or make a [Pull Request](https://github.com/Lizard-Of-Oz/Inventorio/pulls).  

## Inventorio as a Dependency for Your Mod
Until this notice is removed (Summer 2021), the structure of the mod is still subject to change, but you can contact me if you need particular functionality or want something to stay consistent.

If you want to use this mode as a dependency, I recommend using JitPack. Please note that me using Architectury plugin causes the gradle setup to be different that normal: 

Fabric:
```
repositories {
  ...
  maven { url 'https://jitpack.io' }
}

dependencies {
  ...
  modCompileOnly ('com.github.Lizard-Of-Oz.Inventorio:inventorio-1.16-fabric:1.16-SNAPSHOT') { transitive = false }
}
```

Forge:
```
repositories {
  ...
  maven { url 'https://jitpack.io' }
}

dependencies {
  ...
  compileOnly ('com.github.Lizard-Of-Oz.Inventorio:inventorio-1.16-forge:1.16-SNAPSHOT') { transitive = false }
}
```

Note: I haven't figured out how to include documentation into the jar file generated by JitPack. Please read the [source code of InventorioAPI](https://github.com/Lizard-Of-Oz/Inventorio/blob/1.16/common/src/main/java/me/lizardofoz/inventorio/api/InventorioAPI.java)

### Addon Slots, Toolbelt & Item Tags
`InventorioAPI` allows your mod to add custom Toolbelt slots and add custom allowing and disallowing tags and conditions to toolbelt slots, including existing ones.

By default, any tool inheriting its Java class from a vanilla tool (e.g. `PickaxeItem.java`) will be accepted by a corresponding slot.

Any tool with an [item tag](https://fabricmc.net/wiki/tutorial:tags) `inventorio:%item_type%` will be accepted by a corresponding slot.

You can blacklist a tool from the Toolbelt slot by adding giving it a tag `inventorio:%item_type%_blacklist`.

In Forge, a slot accepts any item with a corresponding [ToolType](https://mcforge.readthedocs.io/en/latest/items/items/#basic-items).

In Fabric, `fabric:%item_type%` item tag is accepted by a corresponding slot.

Note: `%item_type%` is always spelled in plural. Available item types: `pickaxes`, `swords`, `axes`, `shovels`, `hoes`.

## (Somewhat) Confirmed Compatible Mods
Fabric:
* [BetterGraves](https://github.com/CerulanLumina/better-graves)
* [Gravestones](https://github.com/Geometrically/Gravestones)
* [Trinkets](https://github.com/emilyalexandra/trinkets) - Partially. This mod removes Hand and Offhand Trinkets slots to make space for the Utility Belt. Full compatibility requires changes in Trinkets.
* [Charm](https://github.com/svenhjol/Charm) - Partially. UI elements conflict with Inventorio, but known crashes have been solved.
* [Curios](https://github.com/TheIllusiveC4/Curios) - Partially. Inventorio slots are hidden in Curios' menu and vice-versa, but known crashes have been solved.

Forge:
* [GraveStone Mod](https://www.curseforge.com/minecraft/mc-mods/gravestone-mod)
* [Quark](https://www.curseforge.com/minecraft/mc-mods/quark)
* [JEI](https://www.curseforge.com/minecraft/mc-mods/jei)
* [Charm Reforged](https://github.com/svenhjol/CharmForge) - Partially. UI elements conflict with Inventorio, but known crashes have been solved.
* [Curios](https://github.com/TheIllusiveC4/Curios) - Partially. Inventorio slots are hidden in Curios' menu and vice-versa, but known crashes have been solved.
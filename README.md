# Inventorio - An inventory enhancement mod for Minecraft (Forge / Fabric)

## Showcase Video:
[![Video Demonstration](https://img.youtube.com/vi/ZXQb49LaC30/maxresdefault.jpg)](https://youtu.be/ZXQb49LaC30)
 
## About
This is my vision of the Inventory Update for Minecraft.
[Features](#Features) I believe are in line with Mojang's vision and could be potentially added to the game.

## Installation
Grab the jar file from the [Release page](https://github.com/Lizard-Of-Oz/Inventorio/releases/). CurseForge and Modrinth pages are coming soon.
 
Copy the jar file into `%root_folder%/mods/` alongside other mods.

Dependencies for Fabric:
* [Fabric API](https://github.com/FabricMC/fabric) (most mods require it anyway)
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

### Client Tweaks
#### Segmented Hotbar
Accessing slots after 5 with a keyboard might be cumbersome because the keys are just too far away. This feature makes the first keystroke select a section, and the second keystroke will select an item inside that section.

There's a "Visual Only" option that keeps the default selection schema.

#### Press Jump to use a Firework
When you press the jump button while gliding, it fires a boost rocket directly from your inventory.

#### Trident Loyalty Check 
This option  prevents you from throwing a Trident without Loyalty.

## Feedback, Use in Modpacks and Mod Compatibility
Feel free to use this mod in a modpack.

If you encounter bugs or compatibility issues with other mods, please report them on the [Issue Tracker](https://github.com/Lizard-Of-Oz/Inventorio/issues)

If you want to request a feature or modification, please use an Issue Tracker or make a [Pull Request](https://github.com/Lizard-Of-Oz/Inventorio/pulls)  

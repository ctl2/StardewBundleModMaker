# Stardew Bundle Mod Maker

Ever wanted to make some changes to the vanilla community-centre bundles in Stardew Valley (SV)? Maybe you've been searching for a bundle mod but can't find anything that _quite_ suits your needs. If so, then **Stardew Bundle Mod Maker** (SBMM) is the tool for you!

Given one JSON-format bundle-description file, SBMM generates a Content Patcher mod for SV, removing the need to write Manifest.json or Content.json files.

Normal bundle files are very tedious to work with. Since they use item IDs rather than names, modders must continuously reference ID dictionary files to write or decode them. SBMM provides a much simpler, more readable format for bundle files and allows for automatic conversion between SV and SBMM-format.

Most bundle mods only patch the English **`Bundles.xnb`** file, which causes issues when playing in other languages. SBMM can create patch files for *EVERY* language supported by vanilla SV, with mods automatically applying the appropriate language patch in-game.

### Limitations

Vanilla bundle images and colours cannot be changed by mods derived from files written in SBMM format.

No modifications to the game's logic are made by this tool, so the restrictions on bundle rewards described on the [Stardew Valley Wiki](https://stardewvalleywiki.com/Modding:Bundles) still apply.

Item metadata is ignored by bundles, so items like wine and jelly are all treated the same regardless of the crop used to produce them. Item names such as 'melon wine' will not be recognised by the tool.

### SBMM-Format Bundles

The following JSON object is an SBMM-format definition of a *Spring Foraging* bundle that requires either 4 regular quality daffodils, 3 silver quality dandelions, 2 gold quality leeks or 1 iridium quality wild horseradish, rewarding the player with 50 salmonberries:
```json
"Spring Foraging": {
    "Reward": "50 salmonberry",
    "Required Options": 1,
    "Options": [
        "4 daffodil",
        "3 silver dandelion",
        "2 gold leek",
        "1 iridium wild horseradish"
    ]
}
```

The **`[example.json](Inputs/Bundles/Custom/example.json)`** file demonstrates how a full set of SBMM-format bundle descriptions should be written. All keys should be unaltered (both in name and order), but all values may be altered at will, as long as data types are preserved. If you're new to JSON, see [this](https://www.digitalocean.com/community/tutorials/an-introduction-to-json#syntax-and-structure) website for an explanation of JSON's syntax and keys vs values.

## Requirements

Optional requirements are _italicised_. Stardew Valley JSON files can be acquired by following [these](https://stardewvalleywiki.com/Modding:Editing_XNB_files#Unpack_game_files) instructions on the Stardew Valley Wiki. The XNB metadata need not be removed. I'm unable to share them as I would be violating Copyright laws.

1. [SMAPI](https://www.nexusmods.com/stardewvalley/mods/2400)
2. [Content Patcher](https://www.nexusmods.com/stardewvalley/mods/1915)
3. JSON versions of the Stardew Valley bundle files (copied to StardewBundleModMaker/Bundles/Vanilla)
   - Bundles.json
   - _Bundles.de-DE.json_ (German)
   - _Bundles.es-ES.json_ (Spanish)
   - _Bundles.fr-FR.json_ (French)
   - _Bundles.hu-HU.json_ (Hungarian)
   - _Bundles.it-IT.json_ (Italian)
   - _Bundles.ja-JP.json_ (Japanese)
   - _Bundles.ko-KR.json_ (Korean)
   - _Bundles.pt-BR.json_ (Portuguese)
   - _Bundles.ru-RU.json_ (Russian)
   - _Bundles.tr-TR.json_ (Turkish)
   - _Bundles.zh-CN.json_ (Chinese)
4. JSON versions of the Stardew Valley object files (copied to StardewBundleModMaker/Items)
   - ObjectInformation.json (most items)
   - _BigCraftables.json_ (equipment and decorations)
   - _Furniture.json_
   - _hats.json_
   - _ClothingInformation.json_

### License

Gson is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2008 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

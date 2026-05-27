# Wrench Finder

Wrench Finder lets you aim at a block and press a key to pull a matching item into your main hand from everywhere.

## Storage

- Supports using items from the Inventory, the armor slots and Curios accessory slots.
- Supports using items from L2 Backpacks(1.21.1).
- Supports using items from Applied Energistics 2: Portable cells, Wireless terminals, and Wireless crafting terminals.
- Supports Tom’s Simple Storage wireless terminal, with access logic identical to the original.
- Supports Refined Storage (RS) wireless terminal, consuming energy based on item count and RS configuration, while allowing unlimited distance and cross-dimensional access.
- Enables nested reading for Shulker Boxes, Sophisticated Backpacks, and L2 Backpacks(1.21.1).

## Block-Item

Wrench Finder uses an ordered Block-Item mapping table on the client side. It has a default rule that coves almost all tech mod.

- Each rule contains one `blockPattern` and one ordered `itemPatterns` list.
- When you press the key while aiming at a block, the mod compares the block id against `lookupRules` from top to bottom.
- The first matching rule wins.
- Inside that rule, `itemPatterns` are also checked from top to bottom.
- The first item pattern that can be found in your inventory or supported remote storage is moved into the main hand.

The config data shape is effectively:

```json
[
    {
      "blockPattern": "minecraft:bedrock",
      "itemPatterns": [
        {
          "value": "create:wrench"
        },
        {
          "value": "minecraft:barrier"
        }
      ]
    },
    {
      "blockPattern": "*create*:*",
      "itemPatterns": [
        {
          "value": "create:wrench"
        }
      ]
    }
]
```

Pattern matching uses simple glob syntax:

- `*` matches any number of characters.
- Matching is done against the full registry id, such as `create:shaft` or `minecraft:crafting_table`.
- `create:*` matches every id whose namespace is exactly `create`.
- `*create*:*` matches every id whose namespace contains `create`, such as `create:shaft` and `createdeco:andesite_sheet`.
- `minecraft:*_planks` matches item or block ids like `minecraft:oak_planks`.

Examples:

- `minecraft:bedrock -> [create:wrench, minecraft:barrier]`
  Trying bedrock will first search for `create:wrench`; if not found, it will try `minecraft:barrier`.
- `botania:* -> [botania:dreamwood_wand, botania:twigwand]`
  Any Botania block can map to either wand, in priority order.
- `*ae*:* -> [ae2:network_tool, ae2:certus_quartz_wrench, ae2:nether_quartz_wrench]`
  Any block whose namespace contains `ae` will try these three AE2 tools in order.

If no explicit rule matches and `fallbackToBlockItem` is enabled, Wrench Finder will try the block's own item form.

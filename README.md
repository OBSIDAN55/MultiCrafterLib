<div align="center">

# MultiCrafter Lib


A Mindustry lib-mod for that add MultiCrafter and a lot of things Json and JavaScript mods.
Please check the [instruction](https://liplum.github.io/MultiCrafterLib/).

## now in the library in addition to MultiCrafter there are:
## PumpBlocked 
which allows to block the production of certain liquids and also the installation of a block on them
## UnitAssemblerWithMenu 
which adds a menu to the functional UnitAssembler with the choice of unit plan regardless of several standing modules

# Now working on Mindustry v147+!
___
</div>

## How to Use

Please check the [instruction](https://liplum.github.io/MultiCrafterLib/) to learn MultiCrafter.

### PumpBlocked:
#### {
#### "blockedLiquids": ["water", "oil", "slag"], // blocked liquids
#### "blockPlacementOnBlockedLiquids": true // blocking the installation on liquids shown in blockedLiquids
#### }

### UnitAssemblerWithMenu:
#### plansJson: [
#### {
#### "unit": "flare", // final unit
#### "time": 120, // time of production
#### "requirements": [
#### "router/2",
#### { "payload": "duo", "amount": 1 } // payload(blocks\units)
#### ],
#### "items": [ // items
#### "copper/50",
#### { "item": "lead", "amount": 30 }
#### ],
#### "liquids": [ // liquids
#### "water/15",
#### { "fluid": "oil", "amount": 5 }
#### ]
#### }
#### ]

## Licence

GNU General Public License v3.0

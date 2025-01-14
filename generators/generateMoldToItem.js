import fs from "fs";
import {deploying_path} from "./generators.js";
import {moldable_metals, moldables, metal_temps} from "./data.mjs";

export const generateMoldToItem = () => {
    moldable_metals.forEach(metal => {
        moldables.forEach(moldable => {
            const craft = {
                "type": "create:deploying",
                "ingredients": [
                    {
                        "type": "tfc:heatable",
                        "max_temp": 200,
                        "ingredient": {
                            "type": "forge:nbt",
                            "item": `tfc:ceramic/${moldable.name}_mold`,
                            "nbt": {
                                "tank": {
                                    "Amount": moldable.unit,
                                    "FluidName": `tfc:metal/${metal}`
                                }
                            }
                        }
                    },
                    {
                        "tag": "tfc:chisels"
                    }
                ],
                "results": [
                    {
                        "item": `tfc:metal/${moldable.name}/${metal}`
                    },
                    {
                        "item": `tfc:ceramic/${moldable.name}_mold`,
                        "chance": 0.75
                    }
                ]
            }
            fs.writeFileSync(`${deploying_path}/${moldable.name}_mold_of_${metal}_to_item.json`, JSON.stringify(craft, null, 4), 'utf8')
        })
    })
}
import fs from "fs";
import {deploying_path, itemResult, writeRecipe} from "./generators.js";

export const generateIngotMoldToIngot = (name) => {
    let data = {
        "type": "create:deploying",
        "ingredients": [
            {
                "type": "tfc:heatable",
                "max_temp": 200,
                "ingredient": {
                    "type": "forge:nbt",
                    "item": "tfc:ceramic/ingot_mold",
                    "nbt": {
                        "tank": {
                            "Amount": 100,
                            "FluidName": `tfc:metal/${name}`
                        }
                    }
                }
            },
            {
                "tag": "tfc:chisels"
            }
        ],
        "results": [
            itemResult(`tfc:metal/ingot/${name}`),
            itemResult("tfc:ceramic/ingot_mold", {"chance": 0.75})
        ]
    }
    writeRecipe(`${deploying_path}/mold_to_ingot_${name}.json`, data)
}

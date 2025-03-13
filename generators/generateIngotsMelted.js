import fs from "fs";
import {heated_mixing_path} from "./generators.js";

export const generateIngotsMelted = (name,min_temp) => {
    let data = {
        "type": "woodencog:heated_mixing",
        "ingredients": [
            {
              "ingredient": { "item": `tfc:metal/ingot/${name}` },
              "min_temp": min_temp,
              "max_temp": 3000
            }
        ],
        "results": [
            {
                "fluid": `tfc:metal/${name}`,
                "nbt": {},
                "amount": 100
            }
        ],
        "heatRequirement": min_temp
    }
    fs.writeFileSync(`${heated_mixing_path}/ingot_to_liquid_${name}.json`, JSON.stringify(data, null, 4), 'utf8')
}
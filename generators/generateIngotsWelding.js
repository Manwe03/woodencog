import fs from "fs";
import {heated_compacting_path} from "./generators.js";

export const generateIngotsWelding = (name,min_temp) => {
    let data = {
        "type": "woodencog:heated_compacting",
        "ingredients": [
            {
                "ingredient": { "item": `tfc:metal/ingot/${name}` },
                "min_temp": min_temp,
                "max_temp": 3000
            },
            {
                "ingredient": { "item": `tfc:metal/ingot/${name}` },
                "min_temp": min_temp,
                "max_temp": 3000
            },
            {
                "ingredient": { "item": "tfc:powder/flux" },
                "min_temp": 0,
                "max_temp": 0
            }
        ],
        "results": [
            {
              "item": `tfc:metal/double_ingot/${name}`,
              "temperature": 0,
              "copy_heat": true,
              "cooling": 0
            }
        ],
        "heatRequirement": min_temp
    }
    fs.writeFileSync(`${heated_compacting_path}/ingot_to_liquid_${name}.json`, JSON.stringify(data, null, 4), 'utf8')
}
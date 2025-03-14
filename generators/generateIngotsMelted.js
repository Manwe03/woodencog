import fs from "fs";
import {heated_mixing_path} from "./generators.js";

export const generateIngotsMelted = (name,welding) => {
    let data = {
        "type": "woodencog:heated_mixing",
        "ingredients": [
            {
              "ingredient": { "item": `tfc:metal/ingot/${name}` },
              "min_temp": welding + welding*0.2,
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
        "heatRequirement": welding + welding*0.2
    }
    fs.writeFileSync(`${heated_mixing_path}/ingot_to_liquid_${name}.json`, JSON.stringify(data, null, 4), 'utf8')
}
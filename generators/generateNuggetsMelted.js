import fs from "fs";
import {heated_mixing_path} from "./generators.js";

export const generateNuggetsMelted = (name, result, min_temp) => {
    [
        {type: 'small', quantity: 10},
        {type: 'poor', quantity: 15},
        {type: 'normal', quantity: 25},
        {type: 'rich', quantity: 35},
    ].forEach(type => {
        let data = {
            "type": "woodencog:heated_mixing",
            "ingredients": [
                {
                  "ingredient": { "item": `tfc:ore/${type.type}_${name}` },
                  "min_temp": min_temp,
                  "max_temp": 3000
                }
            ],
            "results": [
                {
                    "fluid": `tfc:metal/${result}`,
                    "nbt": {},
                    "amount": type.quantity
                }
            ],
            "heatRequirement": min_temp
        }
        fs.writeFileSync(`${heated_mixing_path}/nugget_${type.type}_to_liquid_${name}.json`, JSON.stringify(data, null, 4))
    });
}
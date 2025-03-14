import fs from "fs";
import {heated_pressing_path} from "./generators.js";

export const generateSheetCrafts = (name,working) => {
    let data = {
        "type": "woodencog:heated_pressing",
        "ingredients": [
            {
                "ingredient": { "item": `tfc:metal/double_ingot/${name}` },
                "min_temp": working+working*0.1,
                "max_temp": 3000
            }
        ],
        "results": [
            {
              "item": `tfc:metal/sheet/${name}`,
              "temperature": 0,
              "copy_heat": true,
              "cooling": 0
            }
        ]
    }
    fs.writeFileSync(`${heated_pressing_path}/sheet_${name}.json`, JSON.stringify(data, null, 4), 'utf8')
}
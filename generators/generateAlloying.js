import {heated_alloys} from "./data.mjs";
import fs from "fs";
import {heated_mixing_path} from "./generators.js";

export const generateAlloying = () => {
    heated_alloys.forEach(alloy => {
        const craft = {
            "type": "woodencog:heated_mixing",
            "ingredients": alloy.input,
            "results": [
                {
                    "fluid": alloy.result,
                    "nbt": {},
                    "amount": 100
                }
            ],
            "heatRequirement": alloy.temp,
            "processingTime": 400
        }

        fs.writeFileSync(`${heated_mixing_path}/create_mixing_alloying_${alloy.name}.json`, JSON.stringify(craft, null, 4), 'utf8')
    });
}
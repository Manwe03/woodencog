import fs from "fs";
import path from "path";
import {compacting_path, cutting_path, tfcPaths} from "./generators.js";

export const generateKnappingCrafts = () => {
    [
        {name: "clay_knapping", unit: 4, type: "create:compacting", path: compacting_path},
        {name: "fire_clay_knapping", unit: 4, type: "create:compacting", path: compacting_path},
        {name: "leather_knapping", unit: 1, type: "create:cutting", path: cutting_path},
        {name: "rock_knapping", unit: 1, type: "create:cutting", path: cutting_path},
    ].forEach(type => {
        const knapping_crafts = fs.readdirSync(`${tfcPaths}/${type.name}`).filter(file => path.extname(file) === ".json");
        knapping_crafts.forEach(file => {
            const fileData = fs.readFileSync(path.join(`${tfcPaths}/${type.name}`, file));
            const json = JSON.parse(fileData.toString());
            const ingredients = [];
            for (let i = 0; i < type.unit; i++) {
                ingredients.push({"tag": `tfc:${type.name}`})
            }
            const craft = {
                "type": type.type,
                "ingredients": ingredients,
                "results": [
                    json.result
                ]
            }

            fs.writeFileSync(`${type.path}/${file}`, JSON.stringify(craft, null, 4), 'utf8');
        });
    });
}
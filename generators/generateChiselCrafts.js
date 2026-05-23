import fs from "fs";
import path from "path";
import {cutting_path, itemResult, tfcPaths, writeRecipe} from "./generators.js";

export const generateChiselCrafts = () => {
    ["slab", "smooth", "stair"].forEach(type => {
        const chisel_crafts = fs.readdirSync(`${tfcPaths}/chisel/${type}`).filter(file => path.extname(file) === ".json");
        chisel_crafts.forEach(file => {
            const fileData = fs.readFileSync(path.join(`${tfcPaths}/chisel/${type}`, file));
            const json = JSON.parse(fileData.toString());
            const craft = {
                "type": "create:cutting",
                "ingredients": [
                    {
                        "item": json.ingredient
                    }
                ],
                "results": [
                    itemResult(json.result),
                    (() => {
                        if(json?.extra_drop?.item !== undefined){
                            return itemResult(json?.extra_drop?.item)
                        }
                    })()
                ].filter(result => result !== undefined)
            }

            writeRecipe(`${cutting_path}/${type}/${file}`, craft)
        });
    });
}

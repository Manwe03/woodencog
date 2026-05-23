import fs from "fs";
import {crushing_path, itemResult, tfcPaths, writeRecipe} from "./generators.js";
import path from "path";

export const generateCrusherCrafts = () => {
    const crushing_crafts = [
        ...fs.readdirSync(`${tfcPaths}/quern/`),
    ].filter(file => path.extname(file) === ".json");
    crushing_crafts.forEach(file => {
        const fileData = fs.readFileSync(path.join(`${tfcPaths}/quern/`, file))
        const json = JSON.parse(fileData.toString());
        let isTag = false;
        let ingredient = json.ingredient?.item;
        if(!ingredient) {
            ingredient = json.ingredient?.ingredient?.item;
        }
        if(!ingredient) {
            ingredient = json.ingredient.tag
            isTag = true;
        }
        let result = json.result.item;
        if(!result) {
            result = json.result.stack?.item;
        }
        const quantity = json.result.count ?? 1
        if(ingredient === undefined || result === undefined) return;
        let data_crushing = {
            "type": "create:crushing",
            "ingredients": [
                isTag ?
                {
                    "tag": ingredient
                }
                :
                {
                    "item": ingredient
                }
            ],
            "results": Array(quantity).fill(itemResult(result)),
            "processing_time": 400
        }
        writeRecipe(`${crushing_path}/crushing_${file}`, data_crushing)

        let data_milling = {
            "type": "create:milling",
            "ingredients": [
                isTag ?
                    {
                        "tag": ingredient
                    }
                    :
                    {
                        "item": ingredient
                    }
            ],
            "results": Array(quantity).fill(itemResult(result)),
            "processing_time": 400
        }
        writeRecipe(`${crushing_path}/milling_${file}`, data_milling)

    });
}

import fs from "fs";
import {heating_path, tfcPaths, writeRecipe} from "./generators.js";
import path from "path";

export const generateHeatingCrafts = () => {
    const heating_crafts = fs.readdirSync(`${tfcPaths}/heating/`).filter(file => path.extname(file) === ".json");
    heating_crafts.forEach(file => {
        const fileData = fs.readFileSync(path.join(`${tfcPaths}/heating/`, file));
        const json = JSON.parse(fileData.toString());
        const isSmoking = json.temperature <= 200;
        const ingredient = json.ingredient?.ingredient?.item ?? json.ingredient.item;
        const result = json.result_item?.stack?.item ?? json?.result_item?.item;
        if(ingredient === undefined || result === undefined) return;
        let data = {
            "type": isSmoking ? "minecraft:smoking" : "minecraft:smelting",
            "ingredient": {
                "item": ingredient
            },
            "result": result,
            "experience": 0.0,
            "cookingtime": 200
        }
        writeRecipe(`${heating_path}/${file}.json`, data)
    });
}

import {generateIngotMoldToIngot} from "./generateIngotMoldToIngot.js";
import {metals, nuggets} from "./data.mjs";
import {generateBarrelCrafts} from "./generateBarrelCrafts.js";
import {generateChiselCrafts} from "./generateChiselCrafts.js";
import {generateKnappingCrafts} from "./generateKnappingCrafts.js";
import {generateHeatingCrafts} from "./generateHeatingCrafts.js";
import {generateCrusherCrafts} from "./generateCrusherCrafts.js";
import {generateFilling} from "./generateFilling.js";
import {generateMoldToItem} from "./generateMoldToItem.js";
import fs from "fs";
import path from "path";
import {fileURLToPath} from "url";

const generatorDir = path.dirname(fileURLToPath(import.meta.url));
export const basePath = path.resolve(generatorDir, "..");

export const tfcPaths = path.join(generatorDir, "tfc_recipes");
export const generatedResourcesPath = path.join(basePath, "src", "main", "resources");
export const recipesPath = path.join(generatedResourcesPath, "data", "woodencog");
export const assetsPath = path.join(generatedResourcesPath, "assets");
export const mixing_path = path.join(recipesPath, "recipe", "mixing");
export const heated_mixing_path = path.join(recipesPath, "recipe", "heated_mixing");
export const heated_compacting_path = path.join(recipesPath, "recipe", "heated_compacting");
export const heated_pressing_path = path.join(recipesPath, "recipe", "heated_pressing");
export const advanced_filling_path = path.join(recipesPath, "recipe", "advanced_filling")
export const deploying_path = path.join(recipesPath, "recipe", "deploying");
export const cutting_path = path.join(recipesPath, "recipe", "cutting");
export const compacting_path = path.join(recipesPath, "recipe", "compacting");
export const heating_path = path.join(recipesPath, "recipe", "heating")
export const crushing_path = path.join(recipesPath, "recipe", "crushing")
export const sequenced_assembly_path = path.join(recipesPath, "recipe", "sequenced_assembly")

const createTypes = new Set([
    "create:compacting",
    "create:crushing",
    "create:cutting",
    "create:deploying",
    "create:filling",
    "create:milling",
    "create:mixing",
    "create:pressing",
    "create:sequenced_assembly"
]);

export const itemResult = (item, extra = {}) => ({
    ...extra,
    "id": item
});

export const fluidIngredient = (fluid, amount, isTag = false) => ({
    "type": isTag ? "neoforge:tag" : "neoforge:single",
    "amount": amount,
    [isTag ? "tag" : "fluid"]: fluid
});

const normalizeResult = (result) => {
    if (result?.item === undefined) {
        return result;
    }
    const {item, ...rest} = result;
    return itemResult(item, rest);
}

const normalizeCreateRecipe = (recipe) => {
    if (Array.isArray(recipe)) {
        return recipe.map(normalizeCreateRecipe);
    }
    if (recipe === null || typeof recipe !== "object") {
        return recipe;
    }

    const normalized = {...recipe};
    if (createTypes.has(normalized.type) && Array.isArray(normalized.results)) {
        normalized.results = normalized.results.map(normalizeResult);
    }
    if (Array.isArray(normalized.sequence)) {
        normalized.sequence = normalized.sequence.map(normalizeCreateRecipe);
    }
    if (normalized.transitionalItem !== undefined) {
        normalized.transitional_item = normalizeResult(normalized.transitionalItem);
        delete normalized.transitionalItem;
    }
    if (normalized.keepHeldItem !== undefined) {
        normalized.keep_held_item = normalized.keepHeldItem;
        delete normalized.keepHeldItem;
    }
    return normalized;
}

export const writeRecipe = (filePath, recipe) => {
    fs.mkdirSync(path.dirname(filePath), {recursive: true});
    fs.writeFileSync(filePath, JSON.stringify(normalizeCreateRecipe(recipe), null, 4), 'utf8');
}

metals.forEach(({name, welding, working}) => {
    generateIngotMoldToIngot(name)
});

generateBarrelCrafts();
generateChiselCrafts();
generateKnappingCrafts();
generateHeatingCrafts();
generateCrusherCrafts();
generateFilling();
generateMoldToItem();

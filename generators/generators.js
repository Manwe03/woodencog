import {generateIngotsMelted} from "./generateIngotsMelted.js";
import {generateIngotsWelding} from "./generateIngotsWelding.js";
import {generateIngotMoldToIngot} from "./generateIngotMoldToIngot.js";
import {generateNuggetsMelted} from "./generateNuggetsMelted.js";
//import {generateAnvilCrafts} from "./generateAnvilCrafts.js";
import {metals, nuggets} from "./data.mjs";
import {generateAlloying} from "./generateAlloying.js";
import {generateBarrelCrafts} from "./generateBarrelCrafts.js";
import {generateChiselCrafts} from "./generateChiselCrafts.js";
import {generateKnappingCrafts} from "./generateKnappingCrafts.js";
import {generateHeatingCrafts} from "./generateHeatingCrafts.js";
import {generateCrusherCrafts} from "./generateCrusherCrafts.js";
import {generateFilling} from "./generateFilling.js";
import {generateMoldToItem} from "./generateMoldToItem.js";

export const basePath = "../";

export const tfcPaths = `${basePath}/generators/tfc_recipes`;
export const recipesPath = `${basePath}/src/main/resources/data/woodencog`;
export const assetsPath = `${basePath}/src/main/resources/assets`;
export const mixing_path = `${recipesPath}/recipes/mixing`;
export const heated_mixing_path = `${recipesPath}/recipes/heated_mixing`;
export const heated_compacting_path = `${recipesPath}/recipes/heated_compacting`;
export const advanced_filling_path = `${recipesPath}/recipes/advanced_filling`
export const deploying_path = `${recipesPath}/recipes/deploying`;
export const cutting_path = `${recipesPath}/recipes/cutting`;
export const compacting_path = `${recipesPath}/recipes/compacting`;
export const heating_path = `${recipesPath}/recipes/heating`
export const crushing_path = `${recipesPath}/recipes/crushing`
//export const sequenced_assembly_path = `${recipesPath}/recipes/sequenced_assembly`
metals.forEach(({name, min_temp}) => {
    generateIngotsMelted(name,min_temp)
    generateIngotsWelding(name,min_temp)
    generateIngotMoldToIngot(name)
});

nuggets.forEach(({name, result, min_temp}) => {
    generateNuggetsMelted(name, result, min_temp)
});

//generateAnvilCrafts();
generateAlloying();
generateBarrelCrafts();
generateChiselCrafts();
generateKnappingCrafts();
generateHeatingCrafts();
generateCrusherCrafts();
generateFilling();
generateMoldToItem();
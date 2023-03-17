import fs from "fs";
import path from "path";
import {mixing_path, tfcPaths} from "./generators.js";

export const generateBarrelCrafts = () => {
    const barrel_crafts = fs.readdirSync(`${tfcPaths}/barrel`).filter(file => path.extname(file) === ".json");
    barrel_crafts.forEach(file => {
        const fileData = fs.readFileSync(path.join(`${tfcPaths}/barrel`, file));
        const json = JSON.parse(fileData.toString());
        if (json.type === "tfc:barrel_sealed") {
            let {input_item, input_fluid, output_fluid, output_item, duration} = json;
            duration = Math.max(1, Math.floor(duration / 10));

            let isfluidtag = false;
            let isitemtag = false;

            if (output_fluid === undefined && output_item === undefined) {
                return;
            }

            const ingredient_item = (() => {
                if (input_item) {
                    if (input_item?.ingredient?.ingredient?.tag !== undefined || input_item?.ingredient?.tag !== undefined) {
                        isitemtag = true;
                    }
                    return [input_item?.ingredient?.ingredient?.tag,
                        input_item?.ingredient?.tag,
                        input_item?.ingredient?.ingredient?.item,
                        input_item?.ingredient?.item].filter(item => item !== undefined)[0]
                }
            })()
            if (input_item !== undefined && ingredient_item === undefined) {
                return;
            }

            const ingredient_fluid = (() => {
                if (input_fluid) {
                    if (input_fluid?.ingredient?.tag != undefined) {
                        isfluidtag = true;
                        return input_fluid?.ingredient?.tag;
                    }
                    return input_fluid?.ingredient;
                }
            })()
            if (input_item !== undefined && ingredient_item === undefined) {
                return;
            }

            const output_item_item = output_item?.item;
            const output_item_count = output_item?.count;

            const output_fluid_fluid = output_fluid?.fluid;
            const output_fluid_count = output_fluid?.amount;
            if (output_fluid_fluid === undefined && output_item_item === undefined) return;

            const craft = {
                "type": "create:mixing",
                "ingredients": [
                    (() => {
                        if (ingredient_item) {
                            if (isitemtag) {
                                return {
                                    "tag": ingredient_item
                                }
                            } else {
                                return {
                                    "item": ingredient_item
                                }
                            }
                        }
                    })(),
                    (() => {
                        if (ingredient_fluid) {
                            if (isfluidtag) {
                                return {
                                    "fluidTag": ingredient_fluid,
                                    "amount": json.input_fluid.amount
                                }
                            }
                            return {
                                "fluid": ingredient_fluid,
                                "amount": json.input_fluid.amount
                            }
                        }
                    })(),
                ].filter(ingredient => ingredient !== undefined),
                "results": [
                    (() => {
                        if (output_item_item) {
                            return {
                                "item": output_item_item,
                                "count": output_item_count ?? 1
                            }
                        }
                    })(),
                    (() => {
                        if (output_fluid_fluid) {
                            return {
                                "fluid": output_fluid_fluid,
                                "amount": output_fluid_count
                            }
                        }
                    })(),
                ].filter(item => item !== undefined),
                "processingTime": duration
            }
            fs.writeFileSync(`${mixing_path}/barrel/${file}`, JSON.stringify(craft, null, 4), 'utf8')
        } else if (json.type === "tfc:barrel_instant_fluid") {
            let {primary_fluid, added_fluid, output_fluid} = json;
            const craft = {
                "type": "create:mixing",
                "ingredients": [
                    (() => {
                        if (primary_fluid.ingredient?.tag !== undefined) {
                            return {
                                "fluidTag": primary_fluid.ingredient.tag,
                                "amount": primary_fluid.amount
                            }
                        } else {
                            return {
                                "fluid": primary_fluid.ingredient,
                                "amount": primary_fluid.amount
                            }
                        }
                    })(),
                    (() => {
                        if (added_fluid.ingredient?.tag !== undefined) {
                            return {
                                "fluidTag": added_fluid.ingredient.tag,
                                "amount": added_fluid.amount
                            }
                        } else {
                            return {
                                "fluid": added_fluid.ingredient,
                                "amount": added_fluid.amount
                            }
                        }
                    })()
                ].filter(ingredient => ingredient !== undefined),
                "results": [
                    {
                        "fluid": output_fluid.fluid,
                        "amount": output_fluid.amount
                    },
                ],
                "processingTime": 1
            }
            fs.writeFileSync(`${mixing_path}/barrel/${file}`, JSON.stringify(craft, null, 4), 'utf8')
        }
    });
}
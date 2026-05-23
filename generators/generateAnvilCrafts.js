import fs from "fs";
import path from "path";
import {assetsPath, itemResult, sequenced_assembly_path, tfcPaths, writeRecipe} from "./generators.js";

const anvils_translation = {
    // hit two time
    'punch': (transitional) => {
        return [
            {
                "type": "create:pressing",
                "ingredients": [
                    {
                        "item": transitional
                    }
                ],
                "results": [
                    itemResult(transitional)
                ]
            },
            {
                "type": "create:pressing",
                "ingredients": [
                    {
                        "item": transitional
                    }
                ],
                "results": [
                    {
                        "item": transitional
                    }
                ]
            }
        ]
    },
    // hit one time
    'hit': (transitional) => {
        return [
            {
                "type": "create:pressing",
                "ingredients": [
                    {
                        "item": transitional
                    }
                ],
                "results": [
                    {
                        "item": transitional
                    }
                ]
            }
        ]
    },
    'upset': (transitional) => {
        return {
            "type": "create:deploying",
            "ingredients": [
                {
                    "item": transitional
                },
                {
                    "tag": "tfc:chisels"
                }
            ],
            "results": [
                itemResult(transitional)
            ]
        }
    },
    'draw': (transitional) => {
        return {
            "type": "create:deploying",
            "ingredients": [
                {
                    "item": transitional
                },
                {
                    "tag": "tfc:hammers"
                }
            ],
            "results": [
                itemResult(transitional)
            ]
        }
    },
    'bend': (transitional) => {
        return {
            "type": "create:deploying",
            "ingredients": [
                {
                    "item": transitional
                },
                {
                    "tag": "tfc:chisels"
                }
            ],
            "results": [
                itemResult(transitional)
            ]
        }
    },
    'shrink': (transitional) => {
        return {
            "type": "create:deploying",
            "ingredients": [
                {
                    "item": transitional
                },
                {
                    "tag": "tfc:maces"
                }
            ],
            "results": [
                itemResult(transitional)
            ]
        }
    }
}
export const generateAnvilCrafts = () => {
    const anvil_crafts = fs.readdirSync(`${tfcPaths}/anvil`).filter(file => path.extname(file) === ".json");

    anvil_crafts.forEach(file => {
        const fileData = fs.readFileSync(path.join(`${tfcPaths}/anvil`, file));
        const json = JSON.parse(fileData.toString());
        let types = [];
        types.push(...json.rules.map(name => name.split('_')[0]));
        types = types.filter((value, index) => types.indexOf(value) === index)
        const transitional_name = json.result.item.replace('tfc:', 'woodencog:').replace('minecraft:', 'woodencog:') + "/unfinished";
        const craft = {
            "type": "create:sequenced_assembly",
            "ingredient": json.input,
            "loops": 1,
            "results": [itemResult(json.result.item, {"count": json.result.count})],
            "sequence": [
                {
                    "type": "create:deploying",
                    "ingredients": [
                        {
                            "item": json.result.item
                        },
                        {
                            "item": json.result.item
                        }
                    ],
                    "results": [
                        itemResult(transitional_name)
                    ],
                    "keepHeldItem": true
                },
                types.map(type => anvils_translation[type](transitional_name)).flat()
            ].flat(),
            "transitional_item": itemResult(transitional_name)
        }

        const texture_id = json.result.item.replace("tfc:", "tfc:item/").replace("minecraft:", "item/");
        const path2 = transitional_name.replace("woodencog:", "").slice(0, -11);
        fs.mkdirSync(`${assetsPath}/woodencog/models/item/${path2}/`, { recursive: true }, (err) => console.error(err))
        fs.writeFileSync(`${assetsPath}/woodencog/models/item/${path2}/unfinished.json`, JSON.stringify({parent: texture_id}, null, 4), 'utf8');
        writeRecipe(`${sequenced_assembly_path}/${file}`, craft)
    });
}

